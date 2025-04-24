package com.aakash.chatapplication

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aakash.chatapplication.model.MessageModel
import com.aakash.chatapplication.model.MessageType
import com.piesocket.channels.Channel
import com.piesocket.channels.PieSocket
import com.piesocket.channels.misc.PieSocketEvent
import com.piesocket.channels.misc.PieSocketEventListener
import com.piesocket.channels.misc.PieSocketOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private var piesocket: PieSocket? = null
    private var channel: Channel? = null
    var isConnected by mutableStateOf(false)
    var conversation = mutableStateListOf<MessageModel>()
    var sentMessages = mutableStateListOf<String>()
    var errorMessage by mutableStateOf("")
    var isConnecting = false
    val queuedMessages = mutableListOf<String>()

    fun connectToPieSocket() {
            if (!isConnected && !isConnecting)
                try {
                    isConnecting = true
                    val options = PieSocketOptions().apply {
                        this.apiKey = "F2UyiSIXco7ejsEcLlRK26wVH6OEWaO8a4LLrhRY"
                        this.clusterId = "s14509.blr1"
                    }

                    piesocket = PieSocket(options)
                    channel = piesocket?.join("default")


                    channel?.listen("system:connected", object : PieSocketEventListener() {
                        override fun handleEvent(event: PieSocketEvent) {
                            isConnected = true
                            isConnecting = false
                            errorMessage = ""
                            sendQueuedMessages()
                            Log.d("pie socket connected------", event.toString())
                        }
                    })

                    channel?.listen("message", object : PieSocketEventListener() {
                        override fun handleEvent(event: PieSocketEvent) {
                            Log.d("pie socket msg received------", event.toString())
                            if (queuedMessages.contains(event.data)) {
                                queuedMessages.remove(event.data)
                                conversation.removeIf { it.message == event.data }
                                conversation.add(
                                    MessageModel(
                                        message = event.data,
                                        type = MessageType.SENT
                                    )
                                )
                            } else {
                                conversation.add(
                                    MessageModel(
                                        message = event.data,
                                        type = if (event.data.equals(sentMessages.lastOrNull())) MessageType.SENT else MessageType.RECEIVE
                                    )
                                )
                            }
                        }

                    })

                    channel?.listen("system:error", object : PieSocketEventListener() {
                        override fun handleEvent(event: PieSocketEvent) {
                            isConnected = false
                            isConnecting = false
                            errorMessage = "Error: ${event.data?.toString()}"
                            Log.d("pie socket error------", event.toString())
                        }
                    })
                } catch (e: Exception) {
                    isConnected = false
                    isConnecting = false
                    errorMessage = "Failed to connect: ${e.message}"
                    e.printStackTrace()
                }
    }

    fun sendMessage(message: String) {
        if(message.isNotBlank()) {
            if (isConnected) {
                val event = PieSocketEvent("message").apply {
                    data = message
                }
                channel?.publish(event)
                sentMessages.add(message)
            } else {
                queuedMessages.add(message)
                conversation.add(
                    MessageModel(
                        message = message,
                        type = MessageType.SENT,
                        isQueued = true
                    )
                )
            }
        }
    }

    fun sendQueuedMessages() {
        for (msg in queuedMessages) {
            sendMessage(msg)
        }
    }
}