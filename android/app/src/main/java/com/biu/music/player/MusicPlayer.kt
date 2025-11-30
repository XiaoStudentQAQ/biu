package com.biu.music.player

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
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
    
    init {
        player.addListener(object : Player.Listener {
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
        })
    }
    
    fun play(bvid: String, audioUrl: String, title: String, artist: String, coverUrl: String) {
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
    
    fun release() {
        player.release()
    }
}

data class TrackInfo(
    val bvid: String,
    val title: String,
    val artist: String,
    val coverUrl: String
)

