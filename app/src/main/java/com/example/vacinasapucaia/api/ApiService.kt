package com.example.vacinasapucaia.api

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("comunicado/2ccee441-4256-483b-9898-0307284a3ad9/")
    suspend fun getPage(): Response<String>

    @GET("boletim/covid-19/")
    suspend fun getPage2(): Response<String>
}