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

        const val BASE_URL = "https://www.sapucaia.rj.gov.br/"

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
        return proccessResponseBody(1)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun getBoletim(): String {
        return proccessResponseBody(0)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun proccessResponseBody(action: Int): String {
        var returnString = ""

        if (isNetworkAvailable(context)) {
            val response = when (action) {
                0 -> apiService.getPage2()
                else -> apiService.getPage()
            }

            if (response.isSuccessful) {
                Log.i("response", "${response.body()}")

                returnString = when (action) {
                    0 -> proccesPageAndGetBoletim(response.body()!!)
                    1 -> proccesPageAndGetCalendar(response.body()!!)
                    else -> ""
                }

            } else {
                Log.i("response erro", "${response.errorBody()}")
            }

        } else {
            Log.i("redes", "internet desligada")
        }

        return returnString
    }

    private fun proccesPageAndGetBoletim(pageBody: String): String {
        val page = Jsoup.parse(pageBody)
        return page.getElementsByClass("article-info")[0].getElementsByTag("img").attr("src")

    }

    private fun proccesPageAndGetCalendar(pageBody: String): String {
        val page = Jsoup.parse(pageBody)
        return page.getElementsByClass("article-featured")[0].getElementsByTag("img").attr("src")
    }
}