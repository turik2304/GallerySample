package com.example.gallerysample.data.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkBuilder {

    private const val CONNECT_TIMEOUT: Long = 10

    private const val TOKEN =
        "sl.BQxoqfeV2A7bCcqiw7WKJiDNCLYjcYAJb7wsNG2AJxp-OzEmBYIk6wV4G6vD1UOxticpqyAIzZMpsqOZIxv-kKYmG5I4b_dsfxZVJqpAClrvL-chVY9rbUGWajw34LIy9cCg29WE"

    fun buildOkhttp(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
            .addInterceptor { chain -> chain.proceed(createAuthorizedRequest(chain.request())) }
            .build()
    }

    fun buildRetrofit(client: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun createAuthorizedRequest(request: Request): Request {
        val requestBuilder = request.newBuilder()
            .header("Authorization", "Bearer $TOKEN")
            .method(request.method, request.body)
        return requestBuilder.build()
    }

}