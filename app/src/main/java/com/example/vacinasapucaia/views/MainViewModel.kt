package com.example.vacinasapucaia.views

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vacinasapucaia.repository.Repository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {


    private val _mainCalendar = MutableLiveData<String>()
    val mainCalendar: LiveData<String> = _mainCalendar

    private val _snackBarControll = MutableLiveData<Boolean>()
    val snackBarControll: LiveData<Boolean> = _snackBarControll

    @RequiresApi(Build.VERSION_CODES.M)
    fun getCalendar() {
        viewModelScope.launch {
            val response = repository.geCalendar()
            if (response.isEmpty())
                _snackBarControll.value = true
            else
                _mainCalendar.value = response
        }
    }

    fun restoreSnackBarState(){
        _snackBarControll.value = false
    }

}