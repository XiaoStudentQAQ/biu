package com.biu.music.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.biu.music.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val musicRankList by viewModel.musicRankList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playbackError by viewModel.playbackError.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    // 显示播放错误的 Snackbar
    LaunchedEffect(playbackError) {
        playbackError?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.clearPlaybackError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("音乐排行榜") }
            )
        },
        bottomBar = {
            currentTrack?.let { track ->
                MiniPlayer(
                    title = track.title,
                    artist = track.artist,
                    coverUrl = track.coverUrl,
                    isPlaying = isPlaying,
                    onPlayPause = { viewModel.togglePlay() }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading && musicRankList.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = errorMessage ?: "加载失败")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadMusicRank() }) {
                            Text("重试")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(musicRankList) { item ->
                            MusicListItem(
                                title = item.title,
                                artist = item.author,
                                coverUrl = item.pic,
                                onClick = { viewModel.playMusic(item.bvid) }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // 显示错误消息
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }
}

@Composable
fun MusicListItem(
    title: String,
    artist: String,
    coverUrl: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = coverUrl,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Crop,
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BrokenImage,
                            contentDescription = "加载失败",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                placeholder = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "加载中",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun MiniPlayer(
    title: String,
    artist: String,
    coverUrl: String,
    isPlaying: Boolean,
    onPlayPause: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = coverUrl,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Crop,
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BrokenImage,
                            contentDescription = "加载失败",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                placeholder = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "加载中",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                Text(
                    text = artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            
            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "暂停" else "播放"
                )
            }
        }
    }
}

