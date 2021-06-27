package com.example.vacinasapucaia.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("hh:mm dd/M/yyyy")
    return sdf.format(Date())
}