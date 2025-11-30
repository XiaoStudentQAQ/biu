package com.biu.music.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    @SerialName("code") val code: Int,
    @SerialName("message") val message: String = "",
    @SerialName("ttl") val ttl: Int = 1,
    @SerialName("data") val data: T? = null
)

@Serializable
data class VideoInfo(
    @SerialName("aid") val aid: Long,
    @SerialName("bvid") val bvid: String,
    @SerialName("cid") val cid: Long,
    @SerialName("title") val title: String,
    @SerialName("pic") val pic: String,
    @SerialName("desc") val desc: String = "",
    @SerialName("duration") val duration: Int,
    @SerialName("owner") val owner: Owner,
    @SerialName("pages") val pages: List<VideoPage> = emptyList()
)

@Serializable
data class Owner(
    @SerialName("mid") val mid: Long,
    @SerialName("name") val name: String,
    @SerialName("face") val face: String = ""
)

@Serializable
data class VideoPage(
    @SerialName("cid") val cid: Long,
    @SerialName("page") val page: Int,
    @SerialName("part") val part: String,
    @SerialName("duration") val duration: Int,
    @SerialName("first_frame") val firstFrame: String = ""
)

@Serializable
data class PlayUrlData(
    @SerialName("quality") val quality: Int,
    @SerialName("format") val format: String,
    @SerialName("timelength") val timelength: Long,
    @SerialName("accept_quality") val acceptQuality: List<Int> = emptyList(),
    @SerialName("durl") val durl: List<Durl>? = null,
    @SerialName("dash") val dash: Dash? = null
)

@Serializable
data class Durl(
    @SerialName("order") val order: Int,
    @SerialName("length") val length: Long,
    @SerialName("size") val size: Long,
    @SerialName("url") val url: String,
    @SerialName("backup_url") val backupUrl: List<String> = emptyList()
)

@Serializable
data class Dash(
    @SerialName("duration") val duration: Int,
    @SerialName("video") val video: List<DashVideo> = emptyList(),
    @SerialName("audio") val audio: List<DashAudio> = emptyList(),
    @SerialName("flac") val flac: FlacInfo? = null
)

@Serializable
data class DashVideo(
    @SerialName("id") val id: Int,
    @SerialName("baseUrl") val baseUrl: String,
    @SerialName("bandwidth") val bandwidth: Long,
    @SerialName("codecid") val codecid: Int
)

@Serializable
data class DashAudio(
    @SerialName("id") val id: Int,
    @SerialName("baseUrl") val baseUrl: String,
    @SerialName("bandwidth") val bandwidth: Long,
    @SerialName("codecid") val codecid: Int
)

@Serializable
data class FlacInfo(
    @SerialName("display") val display: Boolean,
    @SerialName("audio") val audio: DashAudio
)

@Serializable
data class SearchResult(
    @SerialName("result") val result: List<SearchItem> = emptyList(),
    @SerialName("numResults") val numResults: Int = 0
)

@Serializable
data class SearchItem(
    @SerialName("type") val type: String,
    @SerialName("id") val id: Long,
    @SerialName("bvid") val bvid: String? = null,
    @SerialName("title") val title: String,
    @SerialName("author") val author: String = "",
    @SerialName("pic") val pic: String = "",
    @SerialName("play") val play: Int = 0,
    @SerialName("duration") val duration: String = ""
)

@Serializable
data class MusicRankItem(
    @SerialName("bvid") val bvid: String,
    @SerialName("title") val title: String,
    @SerialName("pic") val pic: String,
    @SerialName("author") val author: String,
    @SerialName("mid") val mid: Long,
    @SerialName("play") val play: Int = 0,
    @SerialName("duration") val duration: Int = 0
)

@Serializable
data class MusicRankResponse(
    @SerialName("list") val list: List<MusicRankItem> = emptyList(),
    @SerialName("note") val note: String? = null
)

@Serializable
data class UserInfo(
    @SerialName("mid") val mid: Long,
    @SerialName("name") val name: String,
    @SerialName("face") val face: String,
    @SerialName("sign") val sign: String = "",
    @SerialName("level") val level: Int = 0
)

@Serializable
data class FavFolder(
    @SerialName("id") val id: Long,
    @SerialName("fid") val fid: Long,
    @SerialName("mid") val mid: Long,
    @SerialName("title") val title: String,
    @SerialName("cover") val cover: String = "",
    @SerialName("media_count") val mediaCount: Int = 0
)

@Serializable
data class NavInfo(
    @SerialName("wbi_img") val wbiImg: WbiImage? = null
)

@Serializable
data class WbiImage(
    @SerialName("img_url") val imgUrl: String = "",
    @SerialName("sub_url") val subUrl: String = ""
)
