package com.example.vacinasapucaia.work

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.provider.SyncStateContract
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.vacinasapucaia.R
import com.example.vacinasapucaia.local.getDatabase
import com.example.vacinasapucaia.models.Calendar
import com.example.vacinasapucaia.repository.Repository
import com.example.vacinasapucaia.repository.RoomRepository
import com.example.vacinasapucaia.repository.sendNotification
import com.example.vacinasapucaia.utils.DATABASE_ITEM_DESCRIPTION_CALENDAR
import com.example.vacinasapucaia.utils.asDataBaseModel
import com.example.vacinasapucaia.utils.getCurrentTime
import kotlinx.coroutines.launch
import java.lang.Exception

class UpdateCalendarWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    private lateinit var repository: Repository
    private lateinit var roomRepository: RoomRepository

    companion object {
        const val WORK_NAME = "updateCalendarWorker"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun doWork(): Result {
        repository = Repository(applicationContext)
        roomRepository = RoomRepository(getDatabase(applicationContext))

        return try {
            val calendarUrl = repository.getCalendar()
            val calendar = Calendar(0, calendarUrl, getCurrentTime(), DATABASE_ITEM_DESCRIPTION_CALENDAR)

            val lastCalendar = roomRepository.getLastCalendarInsertion().calendarUrl

            if (lastCalendar != calendarUrl) {
                callNotification()
            }

            roomRepository.insertCalendar(calendar.asDataBaseModel())
            Result.success()

        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun callNotification() {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(
            applicationContext.getString(R.string.notification_body),
            applicationContext
        )
    }
}