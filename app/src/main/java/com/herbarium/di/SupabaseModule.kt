package com.herbarium.di

import com.herbarium.data.remote.api.SupabaseApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://zwpanainvfxrarkhyokh.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inp3cGFuYWludmZ4cmFya2h5b2toIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzI0NDMzMzYsImV4cCI6MjA0ODAxOTMzNn0.ezzRcuUjVJnRLmzil5N928teAr3RMqrO1aD3dDOkUwM"
        ) {
            install(Postgrest)
            install(Auth)
            install(Storage)
        }
    }

    @Provides
    fun provideSupabaseApi(supabaseClient: SupabaseClient): SupabaseApi {
        return SupabaseApi(supabaseClient)
    }
}