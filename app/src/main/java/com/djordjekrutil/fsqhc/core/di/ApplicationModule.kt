package com.djordjekrutil.fsqhc.core.di

import android.content.Context
import com.djordjekrutil.fsqhc.BuildConfig
import com.djordjekrutil.fsqhc.core.util.ApiKeyManager
import com.djordjekrutil.fsqhc.feature.db.AppDatabase
import com.djordjekrutil.fsqhc.feature.repository.PlacesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule() {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val keyManager = ApiKeyManager.getInstance()

        return Retrofit.Builder()
            .baseUrl(keyManager.getApiUrl())
            .client(createClient(keyManager.getApiKey()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun createClient(apiKey: String): OkHttpClient {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        okHttpClientBuilder.connectTimeout(30, TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(30, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }

        okHttpClientBuilder.addInterceptor { chain ->
            val originalRequest = chain.request()
            val requestWithHeaders = originalRequest.newBuilder()
                .addHeader("Authorization", apiKey)
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(requestWithHeaders)
        }

        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getDatabase(context)

    @Provides
    @Singleton
    fun providePlacesRepository(
        placesRepositoryImpl: PlacesRepository.PlacesRepositoryImpl
    ): PlacesRepository = placesRepositoryImpl
}