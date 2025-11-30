package com.biu.music.data.repository

import com.biu.music.data.api.BilibiliApi
import com.biu.music.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BilibiliRepository @Inject constructor(
    private val api: BilibiliApi
) {
    
    suspend fun getVideoInfo(bvid: String): Result<VideoInfo> = withContext(Dispatchers.IO) {
        try {
            val response = api.getVideoInfo(bvid)
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPlayUrl(bvid: String, cid: Long): Result<PlayUrlData> = withContext(Dispatchers.IO) {
        try {
            val response = api.getPlayUrl(bvid, cid)
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun search(keyword: String, page: Int = 1): Result<SearchResult> = withContext(Dispatchers.IO) {
        try {
            val response = api.search(keyword, page)
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMusicRank(): Result<List<MusicRankItem>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getMusicRank()
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserInfo(mid: Long): Result<UserInfo> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUserInfo(mid)
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getFavFolders(mid: Long): Result<List<FavFolder>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getFavFolders(mid)
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

