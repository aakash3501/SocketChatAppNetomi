package com.aakash.chatapplication.di

import com.aakash.chatapplication.BuildConfig
import com.piesocket.channels.PieSocket
import com.piesocket.channels.misc.PieSocketOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SocketModule {

    @Singleton
    @Provides
    fun providePieSocketOptions(): PieSocketOptions {
        return PieSocketOptions().apply {
            this.apiKey = BuildConfig.PIE_SOCKET_API_KEY
            this.clusterId = BuildConfig.PIE_SOCKET_CLUSTER_ID
        }
    }

    @Singleton
    @Provides
    fun providePieSocket(options: PieSocketOptions): PieSocket {
        return PieSocket(options)
    }
}