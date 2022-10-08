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
        "sl.BQyjxm0Wqy5jpuFhr2g5ipn_WcVMlMGs5csIn6DkRZYnZ4NRyqM_6VPx3dt48Ky2cwKLaIs8-A8HmMKxvqHF_2goDGapj2MhtAryDuHTckAOAc3NJTSycBSYBlzNs6MPu-bprTnT"

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