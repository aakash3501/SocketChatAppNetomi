package com.aakash.chatapplication.model

data class MessageModel(
    val message: String,
    val type: MessageType,
    var isQueued : Boolean = false
)

enum class MessageType {
    SENT, RECEIVE
}
