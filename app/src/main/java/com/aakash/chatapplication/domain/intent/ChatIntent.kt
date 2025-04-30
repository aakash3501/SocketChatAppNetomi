package com.aakash.chatapplication.domain.intent

sealed interface ChatIntent

data object ConnectToChat : ChatIntent
data class SendMessage(val msg : String) : ChatIntent