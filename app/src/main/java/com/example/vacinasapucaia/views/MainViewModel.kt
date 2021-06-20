package com.example.vacinasapucaia.views

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

    fun getCalendar() {
        viewModelScope.launch(IO) {
            _mainCalendar.postValue(repository.geCalendar())
        }
    }

}