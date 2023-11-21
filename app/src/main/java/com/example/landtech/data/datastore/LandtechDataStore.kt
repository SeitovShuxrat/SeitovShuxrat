package com.example.landtech.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val LAND_PREFERENCES_NAME = "landtech_preferences"

// Create a DataStore instance using the preferencesDataStore delegate, with the Context as
// receiver.
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = LAND_PREFERENCES_NAME
)

class LandtechDataStore(private val preferenceDatastore: DataStore<Preferences>) {
    private val USER_KEY = stringPreferencesKey("user")
    private val PASSWORD_KEY = stringPreferencesKey("password")
    private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    private val USER_TOKEN = stringPreferencesKey("USER_TOKEN")

    val user: Flow<String?> = getPreferenceValue(preferenceDatastore, USER_KEY)
    val password: Flow<String?> = getPreferenceValue(preferenceDatastore, PASSWORD_KEY)
    val isLoggedIn: Flow<Boolean?> = getPreferenceValue(preferenceDatastore, IS_LOGGED_IN_KEY)
    val token: Flow<String?> = getPreferenceValue(preferenceDatastore, USER_TOKEN)

    suspend fun saveUserCredentials(user: String, password: String) {
        preferenceDatastore.edit { preferences ->
            preferences[USER_KEY] = user
            preferences[PASSWORD_KEY] = password
        }
    }

    suspend fun saveUserLoggedIn(value: Boolean) {
        preferenceDatastore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = value
        }
    }

    suspend fun saveUserToken(value: String) {
        preferenceDatastore.edit { preferences ->
            preferences[USER_TOKEN] = value
        }
    }

    private fun <T> getPreferenceValue(
        preferenceDS: DataStore<Preferences>,
        key: Preferences.Key<T>
    ): Flow<T?> {
        return preferenceDS.data
            .catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }
            .map { preferences ->
                preferences[key]
            }
    }
}