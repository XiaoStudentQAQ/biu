package com.biu.music.di

import com.biu.music.data.api.BilibiliApi
import com.biu.music.data.api.BilibiliInterceptor
import com.biu.music.data.api.RetryInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        bilibiliInterceptor: BilibiliInterceptor,
        retryInterceptor: RetryInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(bilibiliInterceptor)
            .addInterceptor(retryInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (com.biu.music.BuildConfig.DEBUG) {
                        // 不记录 HEADERS 避免泄露 Cookie
                        HttpLoggingInterceptor.Level.BASIC
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                    // 敏感信息过滤
                    redactHeader("Cookie")
                    redactHeader("Authorization")
                }
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            // 添加重试机制
            .retryOnConnectionFailure(true)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.bilibili.com/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
    
    @Provides
    @Singleton
    fun provideBilibiliApi(retrofit: Retrofit): BilibiliApi {
        return retrofit.create(BilibiliApi::class.java)
    }
}
