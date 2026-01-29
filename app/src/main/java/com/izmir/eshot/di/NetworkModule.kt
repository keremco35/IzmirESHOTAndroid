package com.izmir.eshot.di

import com.izmir.eshot.BuildConfig
import com.izmir.eshot.data.api.EshotApiService
import com.izmir.eshot.data.network.RetryInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val CONNECT_TIMEOUT_SECONDS = 15L
    private const val READ_TIMEOUT_SECONDS = 30L
    private const val WRITE_TIMEOUT_SECONDS = 30L
    private const val CONNECTION_POOL_IDLE_COUNT = 5
    private const val CONNECTION_POOL_KEEP_ALIVE_MINUTES = 5L

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideConnectionPool(): ConnectionPool {
        return ConnectionPool(
            CONNECTION_POOL_IDLE_COUNT,
            CONNECTION_POOL_KEEP_ALIVE_MINUTES,
            TimeUnit.MINUTES
        )
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(connectionPool: ConnectionPool): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .connectionPool(connectionPool)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(RetryInterceptor(maxRetries = 3))
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    @Named("FilesRetrofit")
    fun provideFilesRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(EshotApiService.BASE_URL_FILES)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    @Named("ApiRetrofit")
    fun provideApiRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(EshotApiService.BASE_URL_API)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    @Named("FilesApiService")
    fun provideFilesApiService(@Named("FilesRetrofit") retrofit: Retrofit): EshotApiService {
        return retrofit.create(EshotApiService::class.java)
    }

    @Provides
    @Singleton
    @Named("ApiService")
    fun provideApiService(@Named("ApiRetrofit") retrofit: Retrofit): EshotApiService {
        return retrofit.create(EshotApiService::class.java)
    }
}
