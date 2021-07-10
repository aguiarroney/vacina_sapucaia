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
import com.example.vacinasapucaia.utils.DATABASE_ITEM_DESCRIPTION_BOLETIM
import com.example.vacinasapucaia.utils.DATABASE_ITEM_DESCRIPTION_CALENDAR
import com.example.vacinasapucaia.utils.asDataBaseModel
import com.example.vacinasapucaia.utils.getCurrentTime
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _repository = Repository(application)

    private val _roomRespository = RoomRepository(getDatabase(application))

    private var _controlList = arrayOf<Calendar>(Calendar(0, "", "", ""), Calendar(0, "", "", ""))

    private var _mainLayoutItems = MutableLiveData<Array<Calendar>>()
    val mainLayoutItems: LiveData<Array<Calendar>> = _mainLayoutItems


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
                checkIfIsNewCalendar(response, DATABASE_ITEM_DESCRIPTION_CALENDAR)
                val currentTime: String = getCurrentTime()
                val calendar = Calendar(
                    0,
                    response,
                    currentTime,
                    DATABASE_ITEM_DESCRIPTION_CALENDAR
                )
                _controlList[0] = calendar
                _mainLayoutItems.value = _controlList

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

            checkIfIsNewCalendar(response, DATABASE_ITEM_DESCRIPTION_BOLETIM)
            val currentTime: String = getCurrentTime()
            val boletim = Calendar(
                0,
                response,
                currentTime,
                DATABASE_ITEM_DESCRIPTION_BOLETIM
            )

            _controlList[1] = boletim
            _mainLayoutItems.value = _controlList

            Log.i("boletim", "salva boletim")
            saveToDataStore(
                boletim
            )
        } else {
            Log.i("boletim", "não pegou o boletim")
        }
    }

    private fun checkIfIsNewCalendar(url: String, dataDescription: String) {
        viewModelScope.launch {
            val lastCalendarObject = _roomRespository.getLastCalendarInsertion(
                dataDescription
            )

            lastCalendarObject?.let {
                val lastCalendar = lastCalendarObject.calendarUrl
                if (lastCalendar != url) {
                    callNotification()
                }
            }

        }
    }

    private fun saveToDataStore(calendarModel: Calendar) {
        viewModelScope.launch {
            _roomRespository.insertObjectToDatabase(calendarModel.asDataBaseModel())
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

        lateinit var calendar: Calendar
        lateinit var boletim: Calendar

        val resCalendar =
            _roomRespository.getLastCalendarInsertion(DATABASE_ITEM_DESCRIPTION_CALENDAR)

        val resBoletim =
            _roomRespository.getLastCalendarInsertion(DATABASE_ITEM_DESCRIPTION_BOLETIM)

        Log.i("boletim", "from db $resBoletim")
        Log.i("calendar", "from db $resCalendar")

        if (resCalendar != null) {
            if (!resCalendar.calendarUrl.isEmpty()) {
                calendar = Calendar(
                    resCalendar.id,
                    resCalendar.calendarUrl,
                    resCalendar.refreshDate,
                    resCalendar.description
                )
            }
        }

        if (resBoletim != null) {
            if (!resBoletim.calendarUrl.isEmpty()) {
                boletim = Calendar(
                    resBoletim.id,
                    resBoletim.calendarUrl,
                    resBoletim.refreshDate,
                    resBoletim.description
                )
            }
        }

        if (!calendar.calendarUrl.isNullOrEmpty() && !boletim.calendarUrl.isNullOrEmpty()) {
            _controlList[0] = calendar
            _controlList[1] = boletim
            Log.i("array", "${_controlList[0]}, ${_controlList[1]}")
            _mainLayoutItems.value = _controlList

        } else {
            getCalendar()
            getBoletim()
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