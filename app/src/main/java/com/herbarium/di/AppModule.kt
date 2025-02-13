package com.herbarium.di

import android.content.Context
import com.herbarium.util.LocationClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideLocationClient(@ApplicationContext context: Context): LocationClient {
        return LocationClient(context)
    }
}