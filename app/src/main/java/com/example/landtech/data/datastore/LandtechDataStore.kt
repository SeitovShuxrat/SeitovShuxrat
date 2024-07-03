package com.example.landtech.data.datastore

import KalmanLatLong
import android.content.Context
import android.location.Location
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.landtech.domain.utils.LocationSerializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
    private val SERVER_KEY = stringPreferencesKey("server")
    private val START_LOCATION_KEY = stringPreferencesKey("start_location_lat")
    private val END_LOCATION_KEY = stringPreferencesKey("end_location")
    private val KALMAN_KEY = stringPreferencesKey("kalman")
    private val LOCATION_ORDER_KEY = stringPreferencesKey("location_order")
    private val DISTANCE_KEY = doublePreferencesKey("location_distance")

    val user: Flow<String?> = getPreferenceValue(preferenceDatastore, USER_KEY)
    val password: Flow<String?> = getPreferenceValue(preferenceDatastore, PASSWORD_KEY)
    val isLoggedIn: Flow<Boolean?> = getPreferenceValue(preferenceDatastore, IS_LOGGED_IN_KEY)
    val token: Flow<String?> = getPreferenceValue(preferenceDatastore, USER_TOKEN)
    val server: Flow<String?> = getPreferenceValue(preferenceDatastore, SERVER_KEY)
    val startLocation: Flow<Location?> = getLocation(START_LOCATION_KEY)
    val endLocation: Flow<Location?> = getLocation(END_LOCATION_KEY)
    val kalman: Flow<KalmanLatLong?> = getKalman(KALMAN_KEY)
    val distance = getPreferenceValue(preferenceDatastore, DISTANCE_KEY)
    val locationOrderId: Flow<String?> = getOrderId()

    suspend fun saveUserCredentials(user: String, password: String, server: String) {
        preferenceDatastore.edit { preferences ->
            preferences[USER_KEY] = user
            preferences[PASSWORD_KEY] = password
            preferences[SERVER_KEY] = server
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

    suspend fun saveLocationDetails(
        startLocation: Location?,
        endLocation: Location?,
        kalmanLatLong: KalmanLatLong?,
        distance: Double,
        orderId: String?
    ) {
        saveLocation(startLocation, START_LOCATION_KEY)
        saveLocation(endLocation, END_LOCATION_KEY)
        saveKalman(kalmanLatLong, KALMAN_KEY)

        preferenceDatastore.edit { preferences ->
            preferences[DISTANCE_KEY] = distance
            preferences[LOCATION_ORDER_KEY] = orderId ?: ""
        }
    }

    private fun getOrderId(): Flow<String?> {
        return preferenceDatastore.data
            .catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }
            .map { preferences ->
                val orderId = preferences[LOCATION_ORDER_KEY]
                if (orderId == "") null else orderId
            }
    }

    private fun getLocation(key: Preferences.Key<String>): Flow<Location?> {
        return preferenceDatastore.data.map { preferences ->
            val locationJson = preferences[key]

            if (locationJson == "") {
                null
            } else {
                val gson = GsonBuilder()
                    .registerTypeAdapter(Location::class.java, LocationSerializer())
                    .create()
                gson.fromJson(locationJson, Location::class.java)
            }

        }
    }

    private suspend fun saveLocation(location: Location?, key: Preferences.Key<String>) {
        val locationJSON =
            if (location != null) {
                val gson = GsonBuilder()
                    .registerTypeAdapter(Location::class.java, LocationSerializer())
                    .create()
                gson.toJson(location)
            } else {
                ""
            }


        preferenceDatastore.edit { preferences ->
            preferences[key] = locationJSON
        }
    }

    private fun getKalman(key: Preferences.Key<String>): Flow<KalmanLatLong?> {
        return preferenceDatastore.data.map { preferences ->
            val locationJson = preferences[key]
            if (locationJson == "") {
                null
            } else {
                val gson = Gson()
                gson.fromJson(locationJson, KalmanLatLong::class.java)
            }
        }
    }

    private suspend fun saveKalman(kalmanLatLong: KalmanLatLong?, key: Preferences.Key<String>) {
        val kalmanJSON = if (kalmanLatLong != null) {
            val gson = Gson()
            gson.toJson(kalmanLatLong)
        } else {
            ""
        }

        preferenceDatastore.edit { preferences ->
            preferences[key] = kalmanJSON
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