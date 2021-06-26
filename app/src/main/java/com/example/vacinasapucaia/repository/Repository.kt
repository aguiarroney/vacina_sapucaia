package com.example.vacinasapucaia.repository

import android.util.Log
import com.example.vacinasapucaia.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


class Repository {

    companion object {
        const val BASE_URL = "http://transparencia.covid.sapucaia.rj.gov.br"

        private val okHTTPClient: OkHttpClient.Builder = OkHttpClient.Builder()
        private val loggingInterceptor = HttpLoggingInterceptor()

        private val retrofit = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(
                okHTTPClient.addInterceptor(loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
            )
            .baseUrl(BASE_URL)
            .build()

        val apiService = retrofit.create(ApiService::class.java)
    }

    suspend fun geCalendar(): String {
        var calendarUrl = ""
        val response = apiService.getPage()

        if (response.isSuccessful) {
            Log.i("response", "${response.body()}")

            if (response.body() != null) {
                calendarUrl = proccesPageAndGetCalendar(response.body()!!)
            }

        } else {
            Log.i("response erro", "${response.errorBody()}")
        }

        return calendarUrl
    }

    private fun proccesPageAndGetCalendar(pageBody: String): String {
        val page = Jsoup.parse(pageBody)
        return page.getElementById("carouselExampleIndicators").getElementsByTag("img").attr("src")
    }
}