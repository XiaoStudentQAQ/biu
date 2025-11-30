package com.biu.music.data.api

import okhttp3.Interceptor
import okhttp3.Response

class BilibiliInterceptor : Interceptor {
    
    companion object {
        private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        private const val REFERER = "https://www.bilibili.com"
        private const val ORIGIN = "https://www.bilibili.com"
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val newRequest = originalRequest.newBuilder()
            .addHeader("User-Agent", USER_AGENT)
            .addHeader("Referer", REFERER)
            .addHeader("Origin", ORIGIN)
            .build()
        
        return chain.proceed(newRequest)
    }
}
