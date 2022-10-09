package com.example.gallerysample.data.network

import android.content.Context
import com.example.gallerysample.BuildConfig
import com.example.gallerysample.GalleryApp
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

object NetworkBuilder {

    private const val CONNECT_TIMEOUT: Long = 10

    private var token: String? = null

    fun buildOkhttp(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
            .addInterceptor { chain ->
                val response = chain.proceed(createAuthorizedRequest(chain.request()))
                if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    throw OAuthError
                }
                response
            }
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
        val oAuthToken = token ?: run {
            token = GalleryApp.appContext.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
                .getString(NetworkConstants.TOKEN_KEY, "").orEmpty()
            token
        }
        val requestBuilder = request.newBuilder()
            .header("Authorization", "Bearer $oAuthToken")
            .method(request.method, request.body)
        return requestBuilder.build()
    }

    object OAuthError : Exception()

}