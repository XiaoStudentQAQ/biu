package com.biu.music.data.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

/**
 * 网络请求重试拦截器
 * 处理网络异常并进行智能重试
 * 
 * 注意：此拦截器用于处理网络层面的重试，不阻塞线程
 */
class RetryInterceptor @Inject constructor() : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var lastException: IOException? = null
        var response: Response? = null
        
        // 尝试最多 MAX_RETRY_COUNT 次
        for (attempt in 0 until MAX_RETRY_COUNT) {
            try {
                // 关闭之前的失败响应
                if (response != null && !response.isSuccessful) {
                    response.close()
                }
                
                response = chain.proceed(request)
                
                // 成功响应或客户端错误（4xx）不重试
                if (response.isSuccessful || response.code in 400..499) {
                    return response
                }
                
                // 服务器错误（5xx），记录日志
                Log.w(TAG, "请求失败 (${response.code}), 尝试次数: ${attempt + 1}/$MAX_RETRY_COUNT")
                
                // 如果不是最后一次尝试，关闭当前响应以便重试
                if (attempt < MAX_RETRY_COUNT - 1) {
                    response.close()
                } else {
                    // 最后一次尝试，返回失败的响应
                    return response
                }
                
            } catch (e: IOException) {
                lastException = e
                
                // 判断是否应该重试
                val shouldRetry = shouldRetry(e)
                
                Log.w(TAG, "网络异常: ${e::class.simpleName} - ${e.message}, " +
                        "是否重试: $shouldRetry, 尝试次数: ${attempt + 1}/$MAX_RETRY_COUNT")
                
                // 如果不应该重试，或已是最后一次尝试，抛出异常
                if (!shouldRetry || attempt >= MAX_RETRY_COUNT - 1) {
                    throw e
                }
            }
        }
        
        // 理论上不会到这里，但为了安全
        throw lastException ?: IOException("请求失败: ${response?.code ?: "未知错误"}")
    }
    
    private fun shouldRetry(exception: IOException): Boolean {
        return when (exception) {
            is SocketTimeoutException -> true
            is java.net.UnknownHostException -> false // DNS 解析失败，重试无意义
            is java.net.ConnectException -> true
            is java.net.SocketException -> true
            is javax.net.ssl.SSLException -> false // SSL 错误不重试
            else -> false
        }
    }
    
    companion object {
        private const val TAG = "RetryInterceptor"
        private const val MAX_RETRY_COUNT = 3
    }
}

