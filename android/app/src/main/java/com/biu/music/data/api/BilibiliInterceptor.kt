package com.biu.music.data.api

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class BilibiliInterceptor @Inject constructor(
    private val authConfig: BiliAuthConfig
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val builder = originalRequest.newBuilder()
            .header("User-Agent", authConfig.userAgent)
            .header("Referer", authConfig.referer)
            .header("Origin", authConfig.origin)
        
        if (authConfig.cookie.isNotBlank()) {
            builder.header("Cookie", authConfig.cookie)
        }
        
        return chain.proceed(builder.build())
    }
}
