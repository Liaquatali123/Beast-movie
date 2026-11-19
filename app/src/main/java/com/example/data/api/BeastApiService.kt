package com.example.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * StreamX is a public, keyless metadata API.
 * It does not expose movie/episode video streams or a playback resolver.
 */
interface BeastApiService {
    @GET("health")
    suspend fun health(): Map<String, Any?>

    @GET("catalog/{type}/{catalog}")
    suspend fun catalog(
        @Path("type") type: String,
        @Path("catalog") catalog: String = "top",
        @Query("genre") genre: String? = null,
        @Query("skip") skip: Int = 0,
        @Query("search") search: String? = null
    ): Map<String, Any?>

    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("type") type: String,
        @Query("skip") skip: Int = 0
    ): Map<String, Any?>

    @GET("anime/search")
    suspend fun searchAnime(@Query("q") query: String): Map<String, Any?>

    @GET("imdb/search")
    suspend fun searchImdb(@Query("q") query: String): Map<String, Any?>

    @GET("meta/{type}/{id}")
    suspend fun metadata(
        @Path("type") type: String,
        @Path("id") id: String,
        @Query("enrich") enrich: Int = 1
    ): Map<String, Any?>

    @GET("similar/{type}/{id}")
    suspend fun similar(
        @Path("type") type: String,
        @Path("id") id: String
    ): Map<String, Any?>

    @GET("trending/{type}")
    suspend fun trending(
        @Path("type") type: String,
        @Query("limit") limit: Int = 20
    ): Map<String, Any?>

    @GET("streaming/{type}/{id}")
    suspend fun streaming(
        @Path("type") type: String,
        @Path("id") id: String,
        @Query("country") country: String = "PK"
    ): Map<String, Any?>
}
