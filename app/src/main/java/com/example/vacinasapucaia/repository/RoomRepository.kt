package com.example.vacinasapucaia.repository

import com.example.vacinasapucaia.local.CalendarDatabase
import com.example.vacinasapucaia.local.DatabaseEntities
import com.example.vacinasapucaia.utils.DATABASE_ITEM_DESCRIPTION_CALENDAR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomRepository(private val database: CalendarDatabase) {

    suspend fun insertObjectToDatabase(calendarEntities: DatabaseEntities) {
        withContext(Dispatchers.IO) {
            if (database.calendarDAO.getDatabaseSize() >= MAX_SIZE)
                database.calendarDAO.clearRoom()

            database.calendarDAO.insertObject(calendarEntities)
        }
    }

    suspend fun getLastCalendarInsertion(dataDescription: String): DatabaseEntities {
        return withContext(Dispatchers.IO) {
            database.calendarDAO.getLastInsertion(dataDescription)
        }
    }

    suspend fun getDatabeseSize(): Int {
        return withContext(Dispatchers.IO) {
            database.calendarDAO.getDatabaseSize()
        }
    }

    companion object {
        private const val MAX_SIZE = 10
    }

}