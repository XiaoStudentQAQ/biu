package com.biu.music.player

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayer @Inject constructor(
    val player: ExoPlayer
) {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _currentTrack = MutableStateFlow<TrackInfo?>(null)
    val currentTrack: StateFlow<TrackInfo?> = _currentTrack.asStateFlow()
    
    private val _playbackError = MutableStateFlow<String?>(null)
    val playbackError: StateFlow<String?> = _playbackError.asStateFlow()
    
    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }
        
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            mediaItem?.let {
                _currentTrack.value = TrackInfo(
                    bvid = it.mediaId,
                    title = it.mediaMetadata.title?.toString() ?: "",
                    artist = it.mediaMetadata.artist?.toString() ?: "",
                    coverUrl = it.mediaMetadata.artworkUri?.toString() ?: ""
                )
            }
        }
        
        override fun onPlayerError(error: PlaybackException) {
            Log.e(TAG, "播放器错误: ${error.errorCodeName} - ${error.message}", error)
            _playbackError.value = when (error.errorCode) {
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> "网络连接失败"
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> "网络连接超时"
                PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> "音频文件未找到"
                PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED -> "音频格式错误"
                PlaybackException.ERROR_CODE_DECODING_FAILED -> "音频解码失败"
                else -> "播放失败: ${error.message}"
            }
        }
    }
    
    init {
        player.addListener(playerListener)
    }
    
    fun play(bvid: String, audioUrl: String, title: String, artist: String, coverUrl: String) {
        try {
            _playbackError.value = null
            
            val mediaItem = MediaItem.Builder()
                .setMediaId(bvid)
                .setUri(audioUrl)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(title)
                        .setArtist(artist)
                        .setArtworkUri(android.net.Uri.parse(coverUrl))
                        .build()
                )
                .build()
            
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        } catch (e: Exception) {
            Log.e(TAG, "播放失败", e)
            _playbackError.value = "播放失败: ${e.message}"
        }
    }
    
    fun togglePlay() {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }
    
    fun seekTo(position: Long) {
        player.seekTo(position)
    }
    
    fun clearError() {
        _playbackError.value = null
    }
    
    /**
     * 清理资源 - 仅在应用完全退出时调用
     * 注意：由于 MusicPlayer 是单例，通常不需要手动调用此方法
     */
    fun release() {
        player.removeListener(playerListener)
        player.release()
    }
    
    companion object {
        private const val TAG = "MusicPlayer"
    }
}

data class TrackInfo(
    val bvid: String,
    val title: String,
    val artist: String,
    val coverUrl: String
)

