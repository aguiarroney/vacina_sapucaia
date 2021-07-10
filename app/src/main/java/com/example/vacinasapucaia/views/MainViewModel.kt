package com.example.vacinasapucaia.views

import android.app.Application
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _repository = Repository(application)

    private val _roomRespository = RoomRepository(getDatabase(application))

    private var _mainCalendarModel = MutableLiveData<Calendar>()
    val mainCalendarModel: LiveData<Calendar> = _mainCalendarModel

    private var _mainBoletim = MutableLiveData<String>()
    val mainBoletim: LiveData<String> = _mainBoletim

    //todo think of a way to show snackbar
//    private val _snackBarControll = MutableLiveData<Boolean>()
//    val snackBarControll: LiveData<Boolean> = _snackBarControll

    private val _refreshTime = MutableLiveData<String>()
    val refreshTime: LiveData<String> = _refreshTime

    @RequiresApi(Build.VERSION_CODES.M)
    fun getCalendar() {
        viewModelScope.launch {
            val response = _repository.getCalendar()
            if (response.isEmpty())
                Log.i("getCalendar", "show snackbar")
//                _snackBarControll.value = true
            else {
                checkIfIsNewCalendar(response)
                val currentTime: String = getCurrentTime()
                val calendar = Calendar(
                    0,
                    response,
                    currentTime,
                    DATABASE_ITEM_DESCRIPTION_CALENDAR
                )
                _mainCalendarModel.value = calendar
                saveToDataStore(
                    calendar
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getBoletim() = viewModelScope.launch {
        val response = _repository.getBoletim()
        Log.i("boletim", "${response}")

        if (response.isNotEmpty()) {
            _mainBoletim.value = response
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
    //todo think of a way to show snackbar
//    fun restoreSnackBarState() {
//        _snackBarControll.value = false
//    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun inflateMainScreen() = viewModelScope.launch {

//        getBoletim()

        val res = _roomRespository.getLastCalendarInsertion()
        if (res != null) {
            if (!res.calendarUrl.isEmpty()) {
                val calendar = Calendar(res.id, res.calendarUrl, res.refreshDate, res.description)
                _mainCalendarModel.value = calendar
            }
        } else {
            getCalendar()
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