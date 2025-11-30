package com.biu.music.di

import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.TransferListener
import com.biu.music.data.api.BiliAuthConfig
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.io.InputStream

/**
 * 自定义 HttpDataSource，支持动态读取认证信息
 */
class BilibiliHttpDataSource private constructor(
    private val authConfig: BiliAuthConfig,
    private val callFactory: Call.Factory
) : HttpDataSource {
    
    private var dataSpec: DataSpec? = null
    private var response: Response? = null
    private var inputStream: InputStream? = null
    private var opened = false
    private var bytesRead = 0L
    
    override fun open(dataSpec: DataSpec): Long {
        this.dataSpec = dataSpec
        
        val url = dataSpec.uri.toString()
        val requestBuilder = Request.Builder()
            .url(url)
            .cacheControl(CacheControl.FORCE_NETWORK)
        
        // 动态添加请求头（每次请求都从 authConfig 读取最新值）
        requestBuilder.addHeader("User-Agent", authConfig.userAgent)
        requestBuilder.addHeader("Referer", authConfig.referer)
        requestBuilder.addHeader("Origin", authConfig.origin)
        
        val cookie = authConfig.cookie
        if (cookie.isNotBlank()) {
            requestBuilder.addHeader("Cookie", cookie)
        }
        
        // 处理 Range 请求
        val position = dataSpec.position
        val length = dataSpec.length
        if (position > 0 || length != -1L) {
            val rangeHeader = if (length != -1L) {
                "bytes=$position-${position + length - 1}"
            } else {
                "bytes=$position-"
            }
            requestBuilder.addHeader("Range", rangeHeader)
        }
        
        val request = requestBuilder.build()
        val call = callFactory.newCall(request)
        
        try {
            response = call.execute()
            val responseBody = response?.body
            
            if (response?.isSuccessful != true) {
                val code = response?.code ?: -1
                val message = response?.message ?: "未知错误"
                throw HttpDataSource.InvalidResponseCodeException(
                    code,
                    message,
                    emptyMap(),
                    dataSpec,
                    ByteArray(0)
                )
            }
            
            inputStream = responseBody?.byteStream()
            opened = true
            bytesRead = 0
            
            // 返回内容长度
            val contentLength = responseBody?.contentLength() ?: -1L
            return if (contentLength != -1L) contentLength else dataSpec.length
        } catch (e: IOException) {
            close()
            throw HttpDataSource.HttpDataSourceException.createForIOException(e, dataSpec, HttpDataSource.HttpDataSourceException.TYPE_OPEN)
        }
    }
    
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) return 0
        
        try {
            val bytesRead = inputStream?.read(buffer, offset, length) ?: -1
            if (bytesRead > 0) {
                this.bytesRead += bytesRead
            }
            return bytesRead
        } catch (e: IOException) {
            throw HttpDataSource.HttpDataSourceException.createForIOException(
                e,
                dataSpec!!,
                HttpDataSource.HttpDataSourceException.TYPE_READ
            )
        }
    }
    
    override fun close() {
        try {
            inputStream?.close()
        } finally {
            inputStream = null
            response?.close()
            response = null
            
            if (opened) {
                opened = false
            }
        }
    }
    
    override fun getUri() = dataSpec?.uri
    
    override fun getResponseHeaders() = response?.headers?.toMultimap() ?: emptyMap()
    
    override fun addTransferListener(transferListener: TransferListener) {
        // 不需要实现
    }
    
    /**
     * Factory 用于创建 BilibiliHttpDataSource 实例
     */
    class Factory(
        private val authConfig: BiliAuthConfig,
        private val callFactory: Call.Factory
    ) : HttpDataSource.Factory {
        
        override fun createDataSource(): HttpDataSource {
            return BilibiliHttpDataSource(authConfig, callFactory)
        }
        
        override fun setDefaultRequestProperties(defaultRequestProperties: Map<String, String>): HttpDataSource.Factory {
            // 不使用默认请求属性，因为我们动态设置
            return this
        }
    }
}

