package com.example.vacinasapucaia.views

import android.app.Application
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.example.vacinasapucaia.R
import com.example.vacinasapucaia.local.getDatabase
import com.example.vacinasapucaia.models.Calendar
import com.example.vacinasapucaia.repository.Repository
import com.example.vacinasapucaia.repository.RoomRepository
import com.example.vacinasapucaia.repository.sendNotification
import com.example.vacinasapucaia.utils.asDataBaseModel
import com.example.vacinasapucaia.utils.getCurrentTime
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _repository = Repository(application)

    private val _roomRespository = RoomRepository(getDatabase(application))

    private var _mainCalendar = MutableLiveData<String>()
    val mainCalendar: LiveData<String> = _mainCalendar

    private val _snackBarControll = MutableLiveData<Boolean>()
    val snackBarControll: LiveData<Boolean> = _snackBarControll

    private val _refreshTime = MutableLiveData<String>()
    val refreshTime: LiveData<String> = _refreshTime

    @RequiresApi(Build.VERSION_CODES.M)
    fun getCalendar() {
        viewModelScope.launch {
            val response = _repository.getCalendar()
            if (response.isEmpty())
                _snackBarControll.value = true
            else {
                checkIfIsNewCalendar(response)
                val currentTime = getCurrentTime()
                _mainCalendar.value = response
                _refreshTime.value = currentTime
                saveToDataStore(
                    Calendar(
                        0,
                        response,
                        currentTime
                    )
                )
            }
        }
    }

    private fun checkIfIsNewCalendar(url: String) {
        viewModelScope.launch {
            val lastCalendar = _roomRespository.getLastCalendarInsertion().calendarUrl
            if (lastCalendar != url) {
                callNotification()
            }
        }
    }

    private fun saveToDataStore(calendarModel: Calendar) {
        viewModelScope.launch {
            _roomRespository.insertCalendar(calendarModel.asDataBaseModel())
            val res = _roomRespository.getDatabeseSize()
            Log.i("room size", "${res}")
        }
    }

    fun restoreSnackBarState() {
        _snackBarControll.value = false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun inflateMainCalendar() {
        viewModelScope.launch {
            val res = _roomRespository.getLastCalendarInsertion()
            if (res != null) {
                if(!res.calendarUrl.isEmpty()){
                    _mainCalendar.value = res.calendarUrl
                    _refreshTime.value = res.refreshDate
                }
            } else {
                getCalendar()
            }
        }
    }

    private fun callNotification() {
        val notificationManager = ContextCompat.getSystemService(
            getApplication(),
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(
            getApplication<Application>().getString(R.string.notification_body),
            getApplication()
        )
    }
}