package com.example.vacinasapucaia.work

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.vacinasapucaia.repository.Repository
import java.lang.Exception

class UpdateCalendarWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "updateCalendarWorker"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun doWork(): Result {
        val repository = Repository(applicationContext)

        return try {
            repository.geCalendar()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}