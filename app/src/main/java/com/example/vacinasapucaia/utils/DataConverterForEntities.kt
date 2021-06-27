package com.example.vacinasapucaia.utils

import com.example.vacinasapucaia.local.DatabaseEntities
import com.example.vacinasapucaia.models.Calendar

fun Calendar.asDataBaseModel(): DatabaseEntities {

    return DatabaseEntities(
        id = this.id,
        calendarUrl = this.calendarUrl,
        refreshDate = this.refreshDate
    )
}