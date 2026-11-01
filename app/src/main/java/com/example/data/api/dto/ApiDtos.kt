package com.example.data.api.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    val code: Int = 0,
    val message: String = "",
    val data: T? = null
)

@JsonClass(generateAdapter = true)
data class HomeResponseData(
    val operatingList: List<OperatingSectionDto>? = null,
    val platformList: List<PlatformDto>? = null
)

@JsonClass(generateAdapter = true)
data class PlatformDto(
    val name: String? = null,
    val uploadBy: String? = null
)

@JsonClass(generateAdapter = true)
data class OperatingSectionDto(
    val type: String? = null,
    val title: String? = null,
    val position: Int? = null,
    val banner: BannerContainerDto? = null,
    val subjects: List<SubjectDto>? = null
)

@JsonClass(generateAdapter = true)
data class BannerContainerDto(
    val items: List<BannerItemDto>? = null
)

@JsonClass(generateAdapter = true)
data class BannerItemDto(
    val id: String? = null,
    val title: String? = null,
    val image: ImageCoverDto? = null,
    val subjectId: String? = null,
    val subjectType: Int? = null,
    val subject: SubjectDto? = null
)

@JsonClass(generateAdapter = true)
data class SubjectDto(
    val subjectId: String = "",
    val subjectType: Int = 1, // 1 = Movie, 2 = TV Series / Anime
    val title: String? = null,
    val description: String? = null,
    val releaseDate: String? = null,
    val duration: Long? = null,
    val genre: String? = null,
    val cover: ImageCoverDto? = null,
    val countryName: String? = null,
    val imdbRatingValue: String? = null,
    val hasResource: Boolean? = null,
    val trailer: TrailerDto? = null,
    val detailPath: String? = null,
    val staffList: List<StaffDto>? = null,
    val stills: List<ImageCoverDto>? = null,
    val season: Int? = null,
    val dubs: List<String>? = null,
    val corner: String? = null,
    val postTitle: String? = null
)

@JsonClass(generateAdapter = true)
data class ImageCoverDto(
    val url: String = "",
    val width: Int? = null,
    val height: Int? = null,
    val blurHash: String? = null,
    val format: String? = null
)

@JsonClass(generateAdapter = true)
data class TrailerDto(
    val videoAddress: VideoAddressDto? = null,
    val cover: ImageCoverDto? = null
)

@JsonClass(generateAdapter = true)
data class VideoAddressDto(
    val videoId: String? = null,
    val url: String = "",
    val duration: Long? = null,
    val width: Int? = null,
    val height: Int? = null,
    val definition: String? = null
)

@JsonClass(generateAdapter = true)
data class StaffDto(
    val staffId: String = "",
    val name: String = "",
    val role: String = "",
    val avatar: ImageCoverDto? = null
)

@JsonClass(generateAdapter = true)
data class SearchRequest(
    val keyword: String,
    val page: Int = 1,
    val perPage: Int = 20,
    val subjectType: Int? = null
)

@JsonClass(generateAdapter = true)
data class SearchResponseData(
    val items: List<SubjectDto>? = null,
    val pager: PagerDto? = null
)

@JsonClass(generateAdapter = true)
data class FilterRequest(
    val page: Int = 1,
    val perPage: Int = 20,
    val subjectType: Int? = null,
    val genre: String? = null
)

@JsonClass(generateAdapter = true)
data class FilterResponseData(
    val items: List<SubjectDto>? = null,
    val pager: PagerDto? = null
)

@JsonClass(generateAdapter = true)
data class TrendingResponseData(
    val subjectList: List<SubjectDto>? = null,
    val pager: PagerDto? = null
)

@JsonClass(generateAdapter = true)
data class EveryoneSearchResponseData(
    val everyoneSearch: List<HotKeywordDto>? = null,
    val hot: List<HotCategoryDto>? = null
)

@JsonClass(generateAdapter = true)
data class HotKeywordDto(
    val keyword: String? = null,
    val title: String? = null
)

@JsonClass(generateAdapter = true)
data class HotCategoryDto(
    val title: String? = null,
    val subjects: List<SubjectDto>? = null
)

@JsonClass(generateAdapter = true)
data class SearchSuggestRequest(
    val keyword: String,
    val perPage: Int = 10
)

@JsonClass(generateAdapter = true)
data class SearchSuggestResponseData(
    val items: List<SearchSuggestItemDto>? = null
)

@JsonClass(generateAdapter = true)
data class SearchSuggestItemDto(
    val word: String? = null,
    val type: Int? = null
)

@JsonClass(generateAdapter = true)
data class DetailRecResponseData(
    val items: List<SubjectDto>? = null
)

@JsonClass(generateAdapter = true)
data class PagerDto(
    val hasMore: Boolean? = null,
    val page: String? = null,
    val perPage: Int? = null,
    val totalCount: Int? = null
)

@JsonClass(generateAdapter = true)
data class SubjectDetailResponseData(
    val subject: SubjectDto? = null,
    val stars: List<StaffDto>? = null,
    val resource: ResourceContainerDto? = null
)

@JsonClass(generateAdapter = true)
data class ResourceContainerDto(
    val seasons: List<SeasonInfoDto>? = null,
    val source: String? = null
)

@JsonClass(generateAdapter = true)
data class SeasonInfoDto(
    val se: Int? = null,
    val maxEp: Int? = null,
    val allEp: String? = null,
    val resolutions: List<ResolutionInfoDto>? = null
)

@JsonClass(generateAdapter = true)
data class ResolutionInfoDto(
    val resolution: Int? = null,
    val epNum: Int? = null
)

@JsonClass(generateAdapter = true)
data class PlayResponseData(
    val streams: List<VideoStreamItemDto>? = null,
    val hls: List<VideoStreamItemDto>? = null,
    val dash: List<VideoStreamItemDto>? = null,
    val hasResource: Boolean? = null,
    val limited: Boolean? = null,
    val vipLocked: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class VideoStreamItemDto(
    val id: String? = null,
    val url: String = "",
    val resolutions: String? = null,
    val format: String? = null,
    val size: Long? = null,
    val codecName: String? = null,
    val playbackType: String? = null
)
