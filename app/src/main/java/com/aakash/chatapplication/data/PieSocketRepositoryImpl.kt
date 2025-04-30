package com.aakash.chatapplication.data

import com.aakash.chatapplication.domain.ChatResponse
import com.aakash.chatapplication.domain.OnConnected
import com.aakash.chatapplication.domain.OnConnectionError
import com.aakash.chatapplication.domain.OnMessageReceived
import com.aakash.chatapplication.domain.repository.PieSocketRepository
import com.aakash.chatapplication.utils.ChatEvents
import com.aakash.chatapplication.utils.DEFAULT_CHANNEL
import com.piesocket.channels.Channel
import com.piesocket.channels.PieSocket
import com.piesocket.channels.misc.PieSocketEvent
import com.piesocket.channels.misc.PieSocketEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PieSocketRepositoryImpl @Inject constructor(val pieSocket: PieSocket) : PieSocketRepository {
    val socketChannel: Channel = pieSocket.join(DEFAULT_CHANNEL)

    override fun reconnect() {
        socketChannel.reconnect()
    }

    override fun connectToSocket(): Flow<ChatResponse> {
        return callbackFlow {
            try {
                socketChannel.listen(
                    ChatEvents.SYSTEM_CONNECTED,
                    object : PieSocketEventListener() {
                        override fun handleEvent(event: PieSocketEvent) {
                            trySend(OnConnected)
                        }
                    })

                socketChannel.listen(ChatEvents.SYSTEM_MESSAGE, object : PieSocketEventListener() {
                    override fun handleEvent(event: PieSocketEvent) {
                        trySend(OnMessageReceived(event.data))
                    }

                })

                socketChannel.listen(ChatEvents.SYSTEM_ERROR, object : PieSocketEventListener() {
                    override fun handleEvent(event: PieSocketEvent) {
                        trySend(OnConnectionError(event.data))
                    }
                })
            } catch (e: Exception) {
                trySend(OnConnectionError(e.message ?: ""))
                e.printStackTrace()
            }
            awaitClose()
        }
    }

    override fun sendMessage(msg: String) {
        val event = PieSocketEvent(ChatEvents.SYSTEM_MESSAGE).apply {
            data = msg
        }
        socketChannel.publish(event)
    }
}