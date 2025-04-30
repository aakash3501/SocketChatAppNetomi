package com.aakash.chatapplication.domain.repository

import com.aakash.chatapplication.domain.ChatResponse
import kotlinx.coroutines.flow.Flow

interface PieSocketRepository {
    fun reconnect()
    fun connectToSocket() : Flow<ChatResponse>
    fun sendMessage(msg : String)
}