package com.example.vacinasapucaia.views

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.vacinasapucaia.local.getDatabase
import com.example.vacinasapucaia.models.Calendar
import com.example.vacinasapucaia.repository.Repository
import com.example.vacinasapucaia.repository.RoomRepository
import com.example.vacinasapucaia.utils.asDataBaseModel
import com.example.vacinasapucaia.utils.getCurrentTime
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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
            val response = _repository.geCalendar()
            if (response.isEmpty())
                _snackBarControll.value = true
            else {
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
            if (!res.calendarUrl.isEmpty()) {
                _mainCalendar.value = res.calendarUrl
                _refreshTime.value = res.refreshDate
            } else {
                getCalendar()
            }
        }
    }

}