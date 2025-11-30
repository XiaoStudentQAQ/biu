package com.biu.music.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 网络状态监控器
 * 用于检测网络连接状态变化
 */
@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager = 
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    /**
     * 检查当前是否有可用的网络连接
     */
    fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    /**
     * 获取当前网络类型
     */
    fun getNetworkType(): NetworkType {
        val network = connectivityManager.activeNetwork ?: return NetworkType.NONE
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return NetworkType.NONE
        
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
            else -> NetworkType.OTHER
        }
    }
    
    /**
     * 观察网络状态变化
     */
    fun observeNetworkStatus(): Flow<Boolean> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            private val availableNetworks = mutableSetOf<Network>()
            
            override fun onAvailable(network: Network) {
                availableNetworks.add(network)
                trySend(true)
            }
            
            override fun onLost(network: Network) {
                availableNetworks.remove(network)
                trySend(availableNetworks.isNotEmpty())
            }
            
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                val isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                
                if (hasInternet && isValidated) {
                    availableNetworks.add(network)
                    trySend(true)
                } else {
                    availableNetworks.remove(network)
                    trySend(availableNetworks.isNotEmpty())
                }
            }
        }
        
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        
        // 发送初始状态
        trySend(isNetworkAvailable())
        
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }.distinctUntilChanged()
}

enum class NetworkType {
    NONE,
    WIFI,
    CELLULAR,
    ETHERNET,
    OTHER
}

