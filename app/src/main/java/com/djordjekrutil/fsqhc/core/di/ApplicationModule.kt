package com.djordjekrutil.fsqhc.core.di

import android.content.Context
import com.djordjekrutil.fsqhc.BuildConfig
import com.djordjekrutil.fsqhc.core.util.ApiKeyManager
import com.djordjekrutil.fsqhc.feature.datasource.PlacesLocalDataSource
import com.djordjekrutil.fsqhc.feature.datasource.PlacesLocalDataSourceImpl
import com.djordjekrutil.fsqhc.feature.datasource.PlacesNetworkDataSource
import com.djordjekrutil.fsqhc.feature.datasource.PlacesNetworkDataSourceImpl
import com.djordjekrutil.fsqhc.feature.db.AppDatabase
import com.djordjekrutil.fsqhc.feature.repository.LocationRepository
import com.djordjekrutil.fsqhc.feature.repository.PlacesRepository
import com.djordjekrutil.fsqhc.feature.repository.PlacesRepositoryImpl
import dagger.Binds
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
abstract class ApplicationModule {

    @Binds
    @Singleton
    abstract fun bindPlacesRepository(
        placesRepositoryImpl: PlacesRepositoryImpl
    ): PlacesRepository

    @Binds
    @Singleton
    abstract fun bindPlacesNetworkDataSource(
        placesNetworkDataSourceImpl: PlacesNetworkDataSourceImpl
    ): PlacesNetworkDataSource

    @Binds
    @Singleton
    abstract fun bindPlacesLocalDataSource(
        placesLocalDataSourceImpl: PlacesLocalDataSourceImpl
    ): PlacesLocalDataSource

    companion object {

        @Provides
        @Singleton
        fun provideRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(ApiKeyManager.getApiUrl())
                .client(createClient(ApiKeyManager.getApiKey()))
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
        fun provideLocationRepository(
            locationRepositoryImpl: LocationRepository.LocationRepositoryImpl
        ): LocationRepository = locationRepositoryImpl
    }
}