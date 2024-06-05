package com.example.landtech.data.remote

import com.example.landtech.data.common.Constants
import com.example.landtech.data.datastore.LandtechDataStore
import com.example.landtech.data.remote.dto.Authorization
import com.example.landtech.data.remote.dto.EngineerDto
import com.example.landtech.data.remote.dto.ExploitationObjectDto
import com.example.landtech.data.remote.dto.NewSparePartDto
import com.example.landtech.data.remote.dto.OrderDto
import com.example.landtech.data.remote.dto.OrderImages
import com.example.landtech.data.remote.dto.SparePartDto
import com.example.landtech.data.remote.dto.TransferOrderDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

interface LandtechServiceAPI {
    @GET("auth")
    suspend fun login(): Response<Authorization>

    @GET("engineers")
    suspend fun getEngineers(): List<EngineerDto>

    @GET("orders")
    suspend fun getOrders(): Response<List<OrderDto>>

    @GET("exploitation_objects")
    suspend fun getExploitationObjects(): List<ExploitationObjectDto>

    @GET("spare_parts")
    suspend fun getSpareParts(
        @Query("onlyRemainders") showOnlyRemainders: Boolean,
        @Query("orderId") orderId: String? = null
    ): List<SparePartDto>

    @POST("orders")
    suspend fun sendOrder(@Body order: OrderDto): Response<Unit>

    @POST("new_spare_parts")
    suspend fun sendNewSpareParts(@Body spareParts: List<NewSparePartDto>): Response<Unit>

    @GET("transfer_orders")
    suspend fun getTransferOrders(): List<TransferOrderDto>

    @POST("transfer_orders")
    suspend fun sendTransferOrders(@Body transferOrders: List<TransferOrderDto>): Response<Unit>

    @Multipart
    @POST("order_files")
    suspend fun uploadOrderFiles(
        @Part("order_id") id: RequestBody,
        @Part image: MultipartBody.Part?,
        @Part signImage: MultipartBody.Part?,
        @Part recording: MultipartBody.Part?
    ): Response<Unit>

    @POST("order_images")
    suspend fun sendOrderImagesList(@Body orderImages: OrderImages)
}

class LandtechAPI(dataStore: LandtechDataStore) {

    private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .addInterceptor(BasicAuthInterceptor(dataStore))  //interceptor to add basic auth according to the shared prefs

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl("http://localhost")
        .client(httpClient.build())
        .build()

    val retrofitService: LandtechServiceAPI by lazy {
        retrofit.create(LandtechServiceAPI::class.java)
    }
}