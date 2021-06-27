package com.example.vacinasapucaia.work

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.vacinasapucaia.local.getDatabase
import com.example.vacinasapucaia.models.Calendar
import com.example.vacinasapucaia.repository.Repository
import com.example.vacinasapucaia.repository.RoomRepository
import com.example.vacinasapucaia.utils.asDataBaseModel
import com.example.vacinasapucaia.utils.getCurrentTime
import java.lang.Exception

class UpdateCalendarWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "updateCalendarWorker"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun doWork(): Result {
        val repository = Repository(applicationContext)
        val roomRepository = RoomRepository(getDatabase(applicationContext))

        return try {
            val calendarUrl = repository.getCalendar()
            val calendar = Calendar(0, calendarUrl, getCurrentTime())
            roomRepository.insertCalendar(calendar.asDataBaseModel())
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}