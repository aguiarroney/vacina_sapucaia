package com.example.vacinasapucaia.views

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vacinasapucaia.repository.DataStoreRespository
import com.example.vacinasapucaia.repository.Repository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _repository = Repository(application)
    private val _dataStoreRepository = DataStoreRespository(application)

    private val _mainCalendar = MutableLiveData<String>()
    val mainCalendar: LiveData<String> = _mainCalendar

    private val _snackBarControll = MutableLiveData<Boolean>()
    val snackBarControll: LiveData<Boolean> = _snackBarControll

    @RequiresApi(Build.VERSION_CODES.M)
    fun getCalendar() {
        viewModelScope.launch {
            val response = _repository.geCalendar()
            if (response.isEmpty())
                _snackBarControll.value = true
            else {
                _mainCalendar.value = response

            }
        }
    }

    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm dd/M/yyyy")
        return sdf.format(Date())
    }

    fun readFromDataStore(url: String) {
        viewModelScope.launch {

            _dataStoreRepository.readFromDataStore.collect {
                Log.i("readDataStore", it)
                //todo trigger notifications
            }
        }
    }

    fun saveToDataStore(url: String) {
        viewModelScope.launch {
            _dataStoreRepository.saveToDataStore(url)
        }
    }

    fun restoreSnackBarState() {
        _snackBarControll.value = false
    }

}