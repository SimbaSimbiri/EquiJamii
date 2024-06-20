package com.simbiri.equityjamii.services

import com.simbiri.equityjamii.data.model.YouTubeResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface YouTubeApiService {
    @GET("search")
    suspend fun getYoutubeVideos(
        @Query("key") apiKey: String,
        @Query("channelId") channelId: String,
        @Query("part") part: String,
        @Query("order") order: String,
        @Query("maxResults") maxResults: Int,
        @QueryMap params: Map<String, String>
    ): YouTubeResponse

    @GET("videos")
    suspend fun getYoutubeVideoDetails(
        @Query("id") videoId: String,
        @Query("key") apiKey: String,
        @Query("part") part: String
    ): YouTubeResponse
}

object RetrofitClient {
    private const val YOU_TUBE_BASE_URL = "https://www.googleapis.com/youtube/v3/"

    private val logging = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val httpClient = OkHttpClient.Builder().apply {
        addInterceptor(logging)
    }.build()

    val instance: YouTubeApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(YOU_TUBE_BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(YouTubeApiService::class.java)
    }
}