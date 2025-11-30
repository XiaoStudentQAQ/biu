package com.biu.music.data.api

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.biu.music.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiliAuthConfig @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val userAgent: String = BuildConfig.BILIBILI_USER_AGENT.ifBlank { DEFAULT_USER_AGENT }
    val referer: String = BuildConfig.BILIBILI_REFERER.ifBlank { DEFAULT_REFERER }
    val origin: String = BuildConfig.BILIBILI_ORIGIN.ifBlank { referer }
    
    // 使用加密的 SharedPreferences 存储敏感信息
    private val encryptedPrefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        EncryptedSharedPreferences.create(
            context,
            "bilibili_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    val cookie: String
        get() {
            // 优先从加密存储中读取
            val storedCookie = encryptedPrefs.getString(COOKIE_KEY, null)
            if (!storedCookie.isNullOrBlank()) {
                return storedCookie
            }
            // 如果加密存储中没有，尝试从 BuildConfig 读取（仅用于首次迁移）
            val buildConfigCookie = BuildConfig.BILIBILI_COOKIE
            if (buildConfigCookie.isNotBlank()) {
                // 迁移到加密存储
                saveCookie(buildConfigCookie)
                return buildConfigCookie
            }
            return ""
        }
    
    fun saveCookie(cookie: String) {
        encryptedPrefs.edit().putString(COOKIE_KEY, cookie).apply()
    }
    
    fun clearCookie() {
        encryptedPrefs.edit().remove(COOKIE_KEY).apply()
    }

    companion object {
        private const val DEFAULT_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        private const val DEFAULT_REFERER = "https://www.bilibili.com"
        private const val COOKIE_KEY = "bilibili_cookie"
    }
}


