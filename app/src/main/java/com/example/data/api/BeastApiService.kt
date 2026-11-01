package com.example.data.api

import com.example.data.api.dto.ApiResponse
import com.example.data.api.dto.DetailRecResponseData
import com.example.data.api.dto.EveryoneSearchResponseData
import com.example.data.api.dto.FilterRequest
import com.example.data.api.dto.FilterResponseData
import com.example.data.api.dto.HomeResponseData
import com.example.data.api.dto.SearchRequest
import com.example.data.api.dto.SearchResponseData
import com.example.data.api.dto.SearchSuggestRequest
import com.example.data.api.dto.SearchSuggestResponseData
import com.example.data.api.dto.TrendingResponseData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BeastApiService {

    @GET("wefeed-h5api-bff/home")
    suspend fun getHome(
        @Query("host") host: String = ApiConfig.host
    ): ApiResponse<HomeResponseData>

    @GET("wefeed-h5api-bff/subject/trending")
    suspend fun getTrending(
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = 18,
        @Query("host") host: String = ApiConfig.host
    ): ApiResponse<TrendingResponseData>

    @GET("wefeed-h5api-bff/subject/everyone-search")
    suspend fun getEveryoneSearch(
        @Query("host") host: String = ApiConfig.host
    ): ApiResponse<EveryoneSearchResponseData>

    @POST("wefeed-h5api-bff/subject/search")
    suspend fun search(
        @Body request: SearchRequest,
        @Query("host") host: String = ApiConfig.host
    ): ApiResponse<SearchResponseData>

    @POST("wefeed-h5api-bff/subject/filter")
    suspend fun filter(
        @Body request: FilterRequest,
        @Query("host") host: String = ApiConfig.host
    ): ApiResponse<FilterResponseData>

    @POST("wefeed-h5api-bff/subject/search-suggest")
    suspend fun searchSuggest(
        @Body request: SearchSuggestRequest,
        @Query("host") host: String = ApiConfig.host
    ): ApiResponse<SearchSuggestResponseData>

    @GET("wefeed-h5api-bff/subject/detail-rec")
    suspend fun getDetailRecommendations(
        @Query("subjectId") subjectId: String,
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = 12,
        @Query("host") host: String = ApiConfig.host
    ): ApiResponse<DetailRecResponseData>

    @GET("wefeed-h5api-bff/tab-operating")
    suspend fun getTabOperating(
        @Query("tabId") tabId: String? = null,
        @Query("host") host: String = ApiConfig.host
    ): ApiResponse<HomeResponseData>
}
