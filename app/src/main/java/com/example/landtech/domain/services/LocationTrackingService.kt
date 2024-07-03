package com.example.landtech.domain.services

import KalmanLatLong
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.landtech.R
import com.example.landtech.data.datastore.LandtechDataStore
import com.example.landtech.domain.utils.DefaultLocationClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationTrackingService : Service() {

    @Inject
    lateinit var dataStore: LandtechDataStore

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: DefaultLocationClient
    private var startLocation: Location? = null
    private var endLocation: Location? = null
    private var previousLocation: Location? = null
    private var distance: Double = 0.0
    private var kalmanFilter = KalmanLatLong(5f)
    private var orderId: String = ""

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext, LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val orderIdExtra = intent?.getStringExtra("orderId")

        orderIdExtra?.let {
            when (intent.action) {
                ACTION_START -> start(orderIdExtra)
                ACTION_STOP -> stop(orderIdExtra)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(orderIdExtra: String) {
        orderId = orderIdExtra

        serviceScope.launch {
            dataStore.saveLocationDetails(null, null, null, 0.0, orderId)
        }

        val notification =
            NotificationCompat.Builder(this, "location").setContentTitle("UHM Landtech")
                .setContentText("Отслеживание локации...").setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)

        locationClient.getLocationUpdates(5000L).catch { e -> e.printStackTrace() }
            .onEach { location ->
                dataStore.startLocation.first()?.let { startLocation = it }
                dataStore.endLocation.first()?.let { previousLocation = it }
                dataStore.kalman.first()?.let { kalmanFilter = it }
                dataStore.distance.first()?.let { distance = it }
                dataStore.locationOrderId.first()?.let { orderId = it }

                if (startLocation == null || (startLocation?.latitude == 0.0 && startLocation?.longitude == 0.0)) {
                    if (location.latitude == 0.0 && location.longitude == 0.0) return@onEach
                    startLocation = location

                    val intent = Intent(BROADCAST_START_LOCATION)
                    intent.putExtra("lat", location.latitude)
                    intent.putExtra("lng", location.longitude)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                }
                calculateDistance(location)
                endLocation = location

                if (startLocation != null && previousLocation != null) {
                    dataStore.saveLocationDetails(
                        startLocation!!, previousLocation!!, kalmanFilter, distance, orderId
                    )
                }
            }.launchIn(serviceScope)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(999, notification.build())
        } else {
            startForeground(
                999, notification.build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        }

    }

    private fun stop(orderIdExtra: String) {
        if (orderIdExtra != orderId) return
        val intent = Intent(BROADCAST_END_LOCATION)
        intent.putExtra("lat", endLocation?.latitude ?: 0.0)
        intent.putExtra("lng", endLocation?.longitude ?: 0.0)
        intent.putExtra("startLat", startLocation?.latitude ?: 0.0)
        intent.putExtra("startLng", startLocation?.longitude ?: 0.0)
        intent.putExtra("distance", distance)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

        serviceScope.launch {
            dataStore.saveLocationDetails(null, null, null, 0.0, null)
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun calculateDistance(location: Location) {
        kalmanFilter.process(
            location.latitude, location.longitude, location.accuracy, location.time
        )
        val filteredLocation = Location("kalman")
        filteredLocation.latitude = kalmanFilter.get_lat()
        filteredLocation.longitude = kalmanFilter.get_lng()
        filteredLocation.accuracy = kalmanFilter.get_accuracy()

        if (previousLocation == null) {
            previousLocation = location
        } else {
            val twoPointDistance = previousLocation!!.distanceTo(filteredLocation).toDouble()
            if (twoPointDistance > 20) {
                distance += twoPointDistance
                previousLocation = filteredLocation
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val BROADCAST_START_LOCATION = "START_LOCATION"
        const val BROADCAST_END_LOCATION = "END_LOCATION"
    }


}