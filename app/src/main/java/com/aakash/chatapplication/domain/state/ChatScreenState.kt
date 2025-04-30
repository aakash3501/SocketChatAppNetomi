package com.aakash.chatapplication.domain.state

import com.aakash.chatapplication.domain.model.MessageModel
import com.aakash.chatapplication.utils.ConnectionStatus

data class ChatScreenState(
    val connectionStatus: ConnectionStatus = ConnectionStatus.NOT_INITIATED,
    val chatList: MutableList<MessageModel> = mutableListOf<MessageModel>(),
    val errorMessage: String = ""
)
