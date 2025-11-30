package com.biu.music.data.repository

import android.util.Log
import com.biu.music.data.api.BilibiliApi
import com.biu.music.data.model.*
import com.biu.music.data.wbi.WbiSigner
import com.biu.music.data.wbi.WbiSignException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BilibiliRepository @Inject constructor(
    private val api: BilibiliApi,
    private val wbiSigner: WbiSigner
) {
    
    suspend fun getVideoInfo(bvid: String): Result<VideoInfo> = withContext(Dispatchers.IO) {
        safeApiCall {
            val response = api.getVideoInfo(bvid)
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(BilibiliApiException(response.code, response.message))
            }
        }
    }
    
    suspend fun getPlayUrl(bvid: String, cid: Long): Result<PlayUrlData> = withContext(Dispatchers.IO) {
        safeApiCall {
            val params = mapOf(
                "bvid" to bvid,
                "cid" to cid.toString(),
                "qn" to "64",
                "fnval" to "16"
            )
            val response = api.getPlayUrl(wbiSigner.sign(params))
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(BilibiliApiException(response.code, response.message))
            }
        }
    }
    
    suspend fun search(keyword: String, page: Int = 1): Result<SearchResult> = withContext(Dispatchers.IO) {
        safeApiCall {
            val response = api.search(keyword, page)
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(BilibiliApiException(response.code, response.message))
            }
        }
    }
    
    suspend fun getMusicRank(): Result<List<MusicRankItem>> = withContext(Dispatchers.IO) {
        safeApiCall {
            val response = api.getMusicRank()
            if (response.code == 0 && response.data != null) {
                Result.success(response.data.list)
            } else {
                Result.failure(BilibiliApiException(response.code, response.message))
            }
        }
    }
    
    suspend fun getUserInfo(mid: Long): Result<UserInfo> = withContext(Dispatchers.IO) {
        safeApiCall {
            val params = mapOf("mid" to mid.toString())
            val response = api.getUserInfo(wbiSigner.sign(params))
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(BilibiliApiException(response.code, response.message))
            }
        }
    }
    
    suspend fun getFavFolders(mid: Long): Result<List<FavFolder>> = withContext(Dispatchers.IO) {
        safeApiCall {
            val response = api.getFavFolders(mid)
            if (response.code == 0 && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(BilibiliApiException(response.code, response.message))
            }
        }
    }
    
    /**
     * 安全的 API 调用包装器，统一处理异常
     */
    private suspend fun <T> safeApiCall(apiCall: suspend () -> Result<T>): Result<T> {
        return try {
            apiCall()
        } catch (e: WbiSignException) {
            Log.e(TAG, "WBI 签名错误: ${e.message}", e)
            Result.failure(RepositoryException("签名验证失败，请稍后重试", e))
        } catch (e: UnknownHostException) {
            Log.e(TAG, "网络连接失败: 无法解析域名", e)
            Result.failure(RepositoryException("网络连接失败，请检查网络设置", e))
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "网络请求超时", e)
            Result.failure(RepositoryException("网络请求超时，请稍后重试", e))
        } catch (e: IOException) {
            Log.e(TAG, "网络IO异常: ${e.message}", e)
            Result.failure(RepositoryException("网络异常，请检查网络连接", e))
        } catch (e: Exception) {
            Log.e(TAG, "未知错误: ${e.message}", e)
            Result.failure(RepositoryException("请求失败: ${e.message}", e))
        }
    }
    
    companion object {
        private const val TAG = "BilibiliRepository"
    }
}

/**
 * Bilibili API 异常
 */
data class BilibiliApiException(
    val code: Int,
    override val message: String
) : Exception("API错误 [$code]: $message")

/**
 * Repository 异常
 */
class RepositoryException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
