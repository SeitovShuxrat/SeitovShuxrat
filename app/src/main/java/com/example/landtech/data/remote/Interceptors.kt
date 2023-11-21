package com.example.landtech.data.remote

import com.example.landtech.data.datastore.LandtechDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class BasicAuthInterceptor(
    private val dataStore: LandtechDataStore
//    private val deviceId: String
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val username = runBlocking { dataStore.user.first() ?: "" }
        val password = runBlocking { dataStore.password.first() ?: "" }
        val token = runBlocking { dataStore.token.first() ?: "" }

        val credentials = Credentials.basic(username, password, charset = Charsets.UTF_8)

        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .header("Authorization", credentials)
            .header("token", token)
            .build()
        return chain.proceed(authenticatedRequest)
    }

}
