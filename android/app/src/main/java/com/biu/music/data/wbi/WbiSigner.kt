package com.biu.music.data.wbi

import android.util.Log
import com.biu.music.data.api.BilibiliApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WbiSigner @Inject constructor(
    private val api: BilibiliApi
) {
    
    private val mutex = Mutex()
    private var cachedKey: CachedKey? = null
    private var retryCount = 0
    
    suspend fun sign(params: Map<String, String>): Map<String, String> {
        return try {
            val mixinKey = ensureMixinKey()
            val wts = (System.currentTimeMillis() / 1000).toString()
            
            val sanitized = params
                .mapValues { (_, value) -> value.replace(FORBIDDEN_CHARS, "") }
                .toSortedMap()
            
            val encodedPairs = sanitized.entries.map { (key, value) ->
                val encodedKey = URLEncoder.encode(key, UTF_8)
                val encodedValue = URLEncoder.encode(value, UTF_8)
                encodedKey to encodedValue
            }
            
            val queryString = encodedPairs.joinToString("&") { (key, value) -> "$key=$value" }
            val signSource = "$queryString&wts=$wts"
            val wRid = md5(signSource + mixinKey)
            
            val result = LinkedHashMap<String, String>()
            encodedPairs.forEach { (key, value) ->
                result[key] = value
            }
            result["wts"] = wts
            result["w_rid"] = wRid
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "WBI 签名失败: ${e.message}", e)
            throw WbiSignException("WBI 签名生成失败", e)
        }
    }
    
    private suspend fun ensureMixinKey(): String = mutex.withLock {
        val now = System.currentTimeMillis()
        val cached = cachedKey
        
        // 检查缓存是否有效
        if (cached != null && now - cached.timestamp < CACHE_DURATION_MS) {
            retryCount = 0 // 重置重试计数
            return cached.key
        }
        
        // 缓存失效，尝试获取新的 key
        try {
            val response = api.getNavInfo()
            val wbiImg = response.data?.wbiImg
            
            if (response.code != 0 || wbiImg == null) {
                throw WbiSignException("无法获取 WBI 配置，API 返回码: ${response.code}, 消息: ${response.message}")
            }
            
            if (wbiImg.imgUrl.isBlank() || wbiImg.subUrl.isBlank()) {
                throw WbiSignException("WBI 配置不完整: imgUrl=${wbiImg.imgUrl}, subUrl=${wbiImg.subUrl}")
            }
            
            val imgKey = extractKey(wbiImg.imgUrl)
            val subKey = extractKey(wbiImg.subUrl)
            
            if (imgKey.isBlank() || subKey.isBlank()) {
                throw WbiSignException("无法从 URL 提取密钥")
            }
            
            val mixinKey = buildMixinKey(imgKey + subKey)
            
            cachedKey = CachedKey(mixinKey, now)
            retryCount = 0
            
            Log.d(TAG, "成功获取新的 WBI 密钥")
            return mixinKey
        } catch (e: Exception) {
            retryCount++
            Log.e(TAG, "获取 WBI 密钥失败 (重试次数: $retryCount): ${e.message}", e)
            
            // 如果有旧的缓存且重试次数未超限，使用旧缓存
            if (cached != null && retryCount < MAX_RETRY_WITH_OLD_KEY) {
                Log.w(TAG, "使用过期的 WBI 密钥")
                return cached.key
            }
            
            throw WbiSignException("获取 WBI 密钥失败，已重试 $retryCount 次", e)
        }
    }
    
    private fun extractKey(url: String): String {
        return url.substringAfterLast("/")
            .substringBefore(".")
    }
    
    private fun buildMixinKey(origin: String): String {
        val chars = origin.toCharArray()
        val builder = StringBuilder()
        mixinKeyEncTab.forEach { index ->
            if (index in chars.indices) {
                builder.append(chars[index])
            }
        }
        return builder.toString().take(32)
    }
    
    private fun md5(input: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val bytes = digest.digest(input.toByteArray(StandardCharsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * 手动清除缓存（用于测试或强制刷新）
     */
    suspend fun clearCache() = mutex.withLock {
        cachedKey = null
        retryCount = 0
        Log.d(TAG, "WBI 缓存已清除")
    }
    
    private data class CachedKey(
        val key: String,
        val timestamp: Long
    )
    
    companion object {
        private const val TAG = "WbiSigner"
        private val FORBIDDEN_CHARS = Regex("[!'()*]")
        private const val UTF_8 = "UTF-8"
        private const val CACHE_DURATION_MS = 6 * 60 * 60 * 1000L // 6小时
        private const val MAX_RETRY_WITH_OLD_KEY = 3 // 最多使用旧密钥的次数
        private val mixinKeyEncTab = intArrayOf(
            46, 7, 58, 9, 27, 18, 59, 24, 2, 53, 8, 23, 60, 34, 61, 19,
            16, 15, 35, 43, 3, 12, 4, 55, 57, 6, 13, 51, 1, 52, 30, 40,
            49, 29, 54, 56, 47, 32, 41, 36, 25, 14, 20, 42, 50, 5, 28, 31,
            22, 10, 37, 38, 45, 33, 26, 44, 0, 39, 17, 11, 21, 48
        )
    }
}

/**
 * WBI 签名异常
 */
class WbiSignException(message: String, cause: Throwable? = null) : Exception(message, cause)


