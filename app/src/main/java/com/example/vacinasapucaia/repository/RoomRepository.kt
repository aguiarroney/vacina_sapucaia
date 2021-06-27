package com.example.vacinasapucaia.repository

import com.example.vacinasapucaia.local.CalendarDatabase
import com.example.vacinasapucaia.local.DatabaseEntities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomRepository(private val database: CalendarDatabase) {

    suspend fun insertCalendar(calendarEntities: DatabaseEntities) {
        withContext(Dispatchers.IO) {
            database.calendarDAO.insertCalendar(calendarEntities)
        }
    }

    suspend fun getLastCalendarInsertion(): DatabaseEntities {
        return withContext(Dispatchers.IO) {
            database.calendarDAO.getLastInsertion()
        }
    }

}