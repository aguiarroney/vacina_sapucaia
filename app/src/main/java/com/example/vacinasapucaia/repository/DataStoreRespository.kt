package com.example.vacinasapucaia.repository

import android.content.Context
import android.util.Log
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreRespository(context: Context) {

    companion object {
        const val PREFERENCE_NAME = "preference"
    }

    private object PreferencesKeys {
        val name = preferencesKey<String>(name = "my_name")
    }

    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = PREFERENCE_NAME
    )

    suspend fun saveToDataStore(url: String) {
        dataStore.edit { it ->
            it[PreferencesKeys.name] = url
        }
    }

    val readFromDataStore: Flow<String> = dataStore.data.catch { exception ->
        if (exception is IOException) {
            Log.i("DataStore", exception.message.toString())
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { it ->
        val myName: String = it[PreferencesKeys.name] ?: ""
        myName
    }
}