package com.aakash.chatapplication.di

import com.aakash.chatapplication.data.PieSocketRepositoryImpl
import com.aakash.chatapplication.domain.repository.PieSocketRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindPieSocketRepository(pieSocketRepositoryImpl: PieSocketRepositoryImpl): PieSocketRepository
}