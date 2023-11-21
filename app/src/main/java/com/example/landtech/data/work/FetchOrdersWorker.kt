package com.example.landtech.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.landtech.data.repository.LandtechRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

@HiltWorker
class FetchOrdersWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: LandtechRepository
) : CoroutineWorker(appContext, workerParams) {
    companion object {
        const val WORK_NAME = "FetchOrdersWorker"
        const val TAG = "FetchOrdersWorkerTag"
    }

    override suspend fun doWork(): Result {

        val result = try {
            repository.fetchOrdersRemote()
        } catch (e: HttpException) {
            e.printStackTrace()
            true
        }

        if (result) {
            val workRequest = OneTimeWorkRequestBuilder<FetchOrdersWorker>()
                .setInitialDelay(2, TimeUnit.MINUTES)
                .addTag(TAG)
                .build()
            WorkManager.getInstance(applicationContext)
                .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.APPEND, workRequest)
        }

        return if (result) Result.success() else Result.failure()
    }


}