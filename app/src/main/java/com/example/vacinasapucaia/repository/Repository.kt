package com.example.vacinasapucaia.repository

import org.jsoup.Jsoup

class Repository {

    fun geCalendar(): String {
        val page = Jsoup.connect("http://transparencia.covid.sapucaia.rj.gov.br").get()
        return page.getElementById("carouselExampleIndicators").getElementsByTag("img").attr("src")
    }
}