package com.aakash.chatapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aakash.chatapplication.domain.OnConnected
import com.aakash.chatapplication.domain.OnConnectionError
import com.aakash.chatapplication.domain.OnMessageReceived
import com.aakash.chatapplication.domain.intent.ChatIntent
import com.aakash.chatapplication.domain.intent.ConnectToChat
import com.aakash.chatapplication.domain.intent.SendMessage
import com.aakash.chatapplication.domain.repository.PieSocketRepository
import com.aakash.chatapplication.domain.state.ChatScreenState
import com.aakash.chatapplication.domain.model.MessageModel
import com.aakash.chatapplication.domain.model.MessageType
import com.aakash.chatapplication.utils.ConnectionStatus
import com.aakash.chatapplication.utils.ConnectivityHelper
import com.aakash.chatapplication.utils.NetworkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val pieSocketRepository: PieSocketRepository,
    val connectivityHelper: ConnectivityHelper
) :
    ViewModel() {

    private val _state = MutableStateFlow(ChatScreenState())
    val state = _state.asStateFlow()

    var sentMessages = mutableListOf<String>()
    val queuedMessages = mutableListOf<String>()

    init {
        listenToNetworkChanges()
    }

    fun handleIntent(intent: ChatIntent) {
        when (intent) {
            ConnectToChat -> {
                connectToChat()
            }

            is SendMessage -> {
                val message = intent.msg
                sendMessage(message)
            }
        }
    }

    private fun listenToNetworkChanges() {
        viewModelScope.launch {
            connectivityHelper.registerForNetworkChange().collect {
                when (it) {
                    NetworkStatus.CONNECTED -> {
                        connectToChat()
                    }

                    NetworkStatus.DISCONNECTED -> {}
                }
            }
        }
    }

    private fun sendMessage(message: String) {
        if (message.isNotBlank()) {
            if (state.value.connectionStatus == ConnectionStatus.CONNECTED) {
                pieSocketRepository.sendMessage(message)
                sentMessages.add(message)
            } else {
                queuedMessages.add(message)
                _state.value =
                    _state.value.copy(chatList = _state.value.chatList.toMutableList().apply {
                        add(
                            MessageModel(
                                message = message,
                                type = MessageType.SENT,
                                isQueued = true
                            )
                        )
                    })
            }
        }
    }

    private fun sendQueuedMessages() {
        for (msg in queuedMessages) {
            sendMessage(msg)
        }
    }

    private fun connectToChat() {
        viewModelScope.launch {
            if (state.value.connectionStatus == ConnectionStatus.NOT_INITIATED) {
                _state.value = _state.value.copy(connectionStatus = ConnectionStatus.CONNECTING)
                pieSocketRepository.connectToSocket().collect { response ->
                    when (response) {
                        OnConnected -> {
                            _state.value = _state.value.copy(
                                connectionStatus = ConnectionStatus.CONNECTED,
                                errorMessage = ""
                            )
                            sendQueuedMessages()
                        }

                        is OnConnectionError -> {
                            _state.value = _state.value.copy(
                                connectionStatus = ConnectionStatus.DISCONNECTED,
                                errorMessage = response.error
                            )
                        }

                        is OnMessageReceived -> {
                            if (queuedMessages.contains(response.msg)) {
                                queuedMessages.remove(response.msg)
                                _state.value =
                                    _state.value.copy(
                                        chatList = _state.value.chatList.toMutableList().apply {
                                            removeIf { it.message == response.msg }
                                            add(
                                                MessageModel(
                                                    message = response.msg,
                                                    type = MessageType.SENT
                                                )
                                            )
                                        })
                            } else {
                                _state.value =
                                    _state.value.copy(
                                        chatList = _state.value.chatList.toMutableList().apply {
                                            add(
                                                MessageModel(
                                                    message = response.msg,
                                                    type = if (response.msg == sentMessages.lastOrNull()) MessageType.SENT else MessageType.RECEIVE
                                                )
                                            )
                                        })
                            }
                        }
                    }

                }
            } else if (state.value.connectionStatus == ConnectionStatus.DISCONNECTED) {
                _state.value = _state.value.copy(connectionStatus = ConnectionStatus.CONNECTING)
                pieSocketRepository.reconnect()
            }
        }
    }
}

