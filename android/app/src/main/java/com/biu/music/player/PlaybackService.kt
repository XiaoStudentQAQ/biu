package com.biu.music.player

import android.content.Intent
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlaybackService : MediaSessionService() {
    
    @Inject
    lateinit var musicPlayer: MusicPlayer
    
    private var mediaSession: MediaSession? = null
    private val player get() = musicPlayer.player
    private var errorListener: Player.Listener? = null
    
    override fun onCreate() {
        super.onCreate()
        
        try {
            // 配置音频属性
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            
            // 处理音频焦点
            player.setHandleAudioBecomingNoisy(true)
            
            // 添加播放器监听器（注意：MusicPlayer 中已经有 Listener，这里只监听特定事件）
            errorListener = object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Log.e(TAG, "播放器错误（Service）: ${error.errorCodeName}", error)
                    // Service 层的错误处理，如显示通知等
                }
                
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_IDLE -> Log.d(TAG, "播放器空闲")
                        Player.STATE_BUFFERING -> Log.d(TAG, "正在缓冲")
                        Player.STATE_READY -> Log.d(TAG, "准备就绪")
                        Player.STATE_ENDED -> Log.d(TAG, "播放结束")
                    }
                }
            }
            
            errorListener?.let { player.addListener(it) }
            
            // 创建 MediaSession
            mediaSession = MediaSession.Builder(this, player)
                .setCallback(MediaSessionCallback())
                .build()
            
            Log.d(TAG, "PlaybackService 创建成功")
        } catch (e: Exception) {
            Log.e(TAG, "PlaybackService 初始化失败", e)
            stopSelf()
        }
    }
    
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }
    
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        // 当任务被移除且播放器未在播放时，停止服务
        if (player?.playWhenReady == false || player?.playbackState == Player.STATE_IDLE) {
            stopSelf()
        }
    }
    
    override fun onDestroy() {
        Log.d(TAG, "PlaybackService 正在销毁")
        
        // 移除监听器
        errorListener?.let { player.removeListener(it) }
        errorListener = null
        
        // 释放 MediaSession（但不释放 player，因为它是单例）
        mediaSession?.run {
            release()
            mediaSession = null
        }
        
        super.onDestroy()
    }
    
    private inner class MediaSessionCallback : MediaSession.Callback {
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): com.google.common.util.concurrent.ListenableFuture<MutableList<MediaItem>> {
            val updatedMediaItems = mediaItems.map { mediaItem ->
                // 验证和处理媒体项
                if (mediaItem.requestMetadata.mediaUri == null && mediaItem.localConfiguration == null) {
                    Log.w(TAG, "媒体项缺少URI: ${mediaItem.mediaId}")
                }
                mediaItem.buildUpon().build()
            }.toMutableList()
            
            return com.google.common.util.concurrent.Futures.immediateFuture(updatedMediaItems)
        }
    }
    
    companion object {
        private const val TAG = "PlaybackService"
    }
}
