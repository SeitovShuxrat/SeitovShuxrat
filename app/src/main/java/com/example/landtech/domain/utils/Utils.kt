package com.example.landtech.domain.utils

import android.Manifest
import android.app.ActivityManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.landtech.LandtechApp
import com.example.landtech.R
import com.example.landtech.data.common.Constants
import com.example.landtech.presentation.MainActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.makeString(): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    return formatter.format(this)
}

fun String.toDate(): Date? {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    return try {
        formatter.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun String.toDateOnly(): Date? {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return try {
        formatter.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun ContentResolver.getFileName(uri: Uri): String {
    var name = ""
    val cursor = query(uri, null, null, null, null)
    cursor?.use {
        it.moveToFirst()

        name = cursor.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
    }
    return name
}

fun getBitmap(context: Context, imageUri: Uri): Bitmap? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(
                context.contentResolver,
                imageUri
            )
        )
    } else {
        context
            .contentResolver
            .openInputStream(imageUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
    }
}

fun showOrderCreatedNotification(orderNumber: String, applicationContext: Context) {
    val intent = Intent(applicationContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent: PendingIntent = PendingIntent
        .getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val builder = NotificationCompat.Builder(applicationContext, LandtechApp.CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("Новый заказ!")
        .setContentText("Создан новый заказ: $orderNumber")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(applicationContext)) {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notify(Constants.ORDER_CREATED_NOTIFICATION_ID, builder.build())
        }
    }
}

class PairLiveData<A, B>(first: LiveData<A>, second: LiveData<B>) : MediatorLiveData<Pair<A?, B?>>() {
    init {
        addSource(first) { value = it to second.value }
        addSource(second) { value = first.value to it }
    }
}

fun <A, B> LiveData<A>.combine(other: LiveData<B>): PairLiveData<A, B> {
    return PairLiveData(this, other)
}

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}

fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val services = manager.getRunningServices(Integer.MAX_VALUE)
    for (service in services) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

fun getFile(uri: Uri, context: Context, uriString: String): File? {
    return try {
        if (uri.scheme == "content") {
            val fd =
                context.contentResolver.openFileDescriptor(uri, "r", null)
                    ?: return null
            val file =
                File(context.cacheDir, context.contentResolver.getFileName(uri))
            val inputStream = FileInputStream(fd.fileDescriptor)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            fd.close()
            file
        } else {
            uri.toFile()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}