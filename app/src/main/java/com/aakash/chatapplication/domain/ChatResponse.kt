package com.aakash.chatapplication.domain

sealed interface ChatResponse

data class OnMessageReceived(val msg : String): ChatResponse
data object OnConnected: ChatResponse
data class OnConnectionError(val error : String): ChatResponse