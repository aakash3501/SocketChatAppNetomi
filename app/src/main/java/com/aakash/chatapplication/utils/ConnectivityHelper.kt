package com.aakash.chatapplication.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ConnectivityHelper @Inject constructor(@ApplicationContext val context: Context) {
    fun registerForNetworkChange(): Flow<NetworkStatus> {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        return callbackFlow {
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(NetworkStatus.CONNECTED)
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    trySend(NetworkStatus.DISCONNECTED)
                }
            }

            val connectivityManager =
                context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
            connectivityManager.requestNetwork(networkRequest, networkCallback)

            awaitClose()
        }
    }
}
