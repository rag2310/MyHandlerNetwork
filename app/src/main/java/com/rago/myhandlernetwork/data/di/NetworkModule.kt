package com.rago.myhandlernetwork.data.di

import android.content.Context
import com.rago.myhandlernetwork.data.utils.INetworkMonitor
import com.rago.myhandlernetwork.data.utils.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): INetworkMonitor {
        return NetworkMonitor(context)
    }
}