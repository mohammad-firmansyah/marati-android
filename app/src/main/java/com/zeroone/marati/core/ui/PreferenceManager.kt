package com.zeroone.marati.core.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

class PreferenceManager private constructor(private val dataStore: DataStore<Preferences>){

    suspend fun clearDataStore() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    fun getToken(): kotlinx.coroutines.flow.Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN] ?: ""
        }
    }

    suspend fun setToken(token:String?) {
        dataStore.edit { preference ->
            preference[ACCESS_TOKEN] = token ?: ""
        }
    }






    companion object {
        @Volatile
        private var INSTANCE: PreferenceManager? = null

        private val ACCESS_TOKEN = stringPreferencesKey("access_token")


        fun getInstance(dataStore: DataStore<Preferences>): PreferenceManager {
            return INSTANCE ?: synchronized(this) {
                val instance = PreferenceManager(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}