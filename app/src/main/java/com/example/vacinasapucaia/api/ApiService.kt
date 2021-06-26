package com.example.vacinasapucaia.api

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET(".")
    suspend fun getPage(): Response<String>
}