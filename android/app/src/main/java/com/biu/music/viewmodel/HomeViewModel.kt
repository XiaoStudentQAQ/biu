package com.biu.music.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biu.music.data.model.MusicRankItem
import com.biu.music.data.repository.BilibiliRepository
import com.biu.music.player.MusicPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: BilibiliRepository,
    private val musicPlayer: MusicPlayer
) : ViewModel() {
    
    private val _musicRankList = MutableStateFlow<List<MusicRankItem>>(emptyList())
    val musicRankList: StateFlow<List<MusicRankItem>> = _musicRankList.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    val currentTrack = musicPlayer.currentTrack
    val isPlaying = musicPlayer.isPlaying
    val playbackError = musicPlayer.playbackError
    
    init {
        loadMusicRank()
    }
    
    fun loadMusicRank() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.getMusicRank()
                .onSuccess { list ->
                    _musicRankList.value = list
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "加载失败"
                }
            
            _isLoading.value = false
        }
    }
    
    fun playMusic(bvid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.getVideoInfo(bvid)
                .onSuccess { videoInfo ->
                    val cid = videoInfo.cid
                    
                    repository.getPlayUrl(bvid, cid)
                        .onSuccess { playUrlData ->
                            val audioUrl = playUrlData.dash?.audio?.firstOrNull()?.baseUrl
                                ?: playUrlData.dash?.flac?.audio?.baseUrl
                            
                            if (audioUrl != null) {
                                musicPlayer.play(
                                    bvid = bvid,
                                    audioUrl = audioUrl,
                                    title = videoInfo.title,
                                    artist = videoInfo.owner.name,
                                    coverUrl = videoInfo.pic
                                )
                            } else {
                                _errorMessage.value = "无法获取音频地址"
                            }
                        }
                        .onFailure { error ->
                            _errorMessage.value = error.message ?: "获取播放地址失败"
                        }
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "获取视频信息失败"
                }
            
            _isLoading.value = false
        }
    }
    
    fun togglePlay() {
        musicPlayer.togglePlay()
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun clearPlaybackError() {
        musicPlayer.clearError()
    }
}

