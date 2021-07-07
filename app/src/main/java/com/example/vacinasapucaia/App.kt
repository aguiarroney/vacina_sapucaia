package com.example.vacinasapucaia

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.work.*
import com.example.vacinasapucaia.work.UpdateCalendarWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class App : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresBatteryNotLow(true)
        .apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setRequiresDeviceIdle(true)
            }
        }.build()

    private fun delayedInit() {
        applicationScope.launch {
            setUpRecurringWork()
        }
    }

    private fun setUpRecurringWork() {
        val repeatingRequest =
            PeriodicWorkRequestBuilder<UpdateCalendarWorker>(2, TimeUnit.DAYS).setConstraints(
                constraints
            ).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            UpdateCalendarWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }

    override fun onCreate() {
        super.onCreate()
        delayedInit()
        Log.i("app", "passou no app")
    }
}