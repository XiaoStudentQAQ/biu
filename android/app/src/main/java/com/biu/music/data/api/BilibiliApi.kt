package com.biu.music.data.api

import com.biu.music.data.model.*
import retrofit2.http.GET
import retrofit2.http.Query

interface BilibiliApi {
    
    /**
     * 获取视频信息
     */
    @GET("x/web-interface/view")
    suspend fun getVideoInfo(
        @Query("bvid") bvid: String
    ): ApiResponse<VideoInfo>
    
    /**
     * 获取视频播放地址
     */
    @GET("x/player/wbi/playurl")
    suspend fun getPlayUrl(
        @Query("bvid") bvid: String,
        @Query("cid") cid: Long,
        @Query("qn") quality: Int = 64,
        @Query("fnval") fnval: Int = 16  // DASH 格式
    ): ApiResponse<PlayUrlData>
    
    /**
     * 搜索
     */
    @GET("x/web-interface/search/all/v2")
    suspend fun search(
        @Query("keyword") keyword: String,
        @Query("page") page: Int = 1,
        @Query("search_type") searchType: String = "video"
    ): ApiResponse<SearchResult>
    
    /**
     * 音乐排行榜
     */
    @GET("x/web-interface/ranking/v2")
    suspend fun getMusicRank(
        @Query("rid") rid: Int = 3,  // 3 = 音乐区
        @Query("type") type: String = "all"
    ): ApiResponse<List<MusicRankItem>>
    
    /**
     * 获取用户信息
     */
    @GET("x/space/wbi/acc/info")
    suspend fun getUserInfo(
        @Query("mid") mid: Long
    ): ApiResponse<UserInfo>
    
    /**
     * 获取收藏夹列表
     */
    @GET("x/v3/fav/folder/created/list-all")
    suspend fun getFavFolders(
        @Query("up_mid") mid: Long
    ): ApiResponse<List<FavFolder>>
}
