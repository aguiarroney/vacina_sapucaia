package com.example.vacinasapucaia.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar")
data class DatabaseEntities(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val calendarUrl: String,
    val refreshDate: String,
    val description: String
)