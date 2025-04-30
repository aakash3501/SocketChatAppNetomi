package com.aakash.chatapplication.utils

object ChatEvents {
    const val SYSTEM_CONNECTED= "system:connected"
    const val SYSTEM_MESSAGE= "message"
    const val SYSTEM_ERROR= "system:error"
}

const val DEFAULT_CHANNEL = "default"

enum class ConnectionStatus{
    NOT_INITIATED,CONNECTED,DISCONNECTED,CONNECTING
}


enum class NetworkStatus{
    CONNECTED,DISCONNECTED
}