package com.biu.music.di

import android.content.Context
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.biu.music.data.api.BiliAuthConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {
    
    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        authConfig: BiliAuthConfig,
        okHttpClient: OkHttpClient
    ): ExoPlayer {
        // 使用自定义的 DataSource.Factory 来动态设置请求头
        val dataSourceFactory = BilibiliHttpDataSource.Factory(authConfig, okHttpClient)
        
        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(context)
                    .setDataSourceFactory(dataSourceFactory)
            )
            .build()
    }
}

