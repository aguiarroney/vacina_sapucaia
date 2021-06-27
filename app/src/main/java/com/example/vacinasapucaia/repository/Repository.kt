package com.example.vacinasapucaia.repository

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.vacinasapucaia.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


class Repository(private val context: Context) {

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


    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkAvailable(context: Context): Boolean {
        val cm: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return false

        if (cm.activeNetwork == null)
            return false

        return true
    }


    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun getCalendar(): String {
        var calendarUrl = ""

        if (isNetworkAvailable(context)) {
            val response = apiService.getPage()

            if (response.isSuccessful) {
                Log.i("response", "${response.body()}")

                if (response.body() != null) {
                    calendarUrl = proccesPageAndGetCalendar(response.body()!!)
                }

            } else {
                Log.i("response erro", "${response.errorBody()}")
            }

        } else {
            Log.i("redes", "internet desligada")
        }

        return calendarUrl
    }

    private fun proccesPageAndGetCalendar(pageBody: String): String {
        val page = Jsoup.parse(pageBody)
        return page.getElementById("carouselExampleIndicators").getElementsByTag("img").attr("src")
    }
}