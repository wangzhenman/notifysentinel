package com.wangzhenman.notifysentinel.network

import com.wangzhenman.notifysentinel.model.Device
import com.wangzhenman.notifysentinel.model.DeviceRegistrationRequest
import com.wangzhenman.notifysentinel.model.EventItem
import com.wangzhenman.notifysentinel.model.HealthResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("health")
    suspend fun health(): HealthResponse

    @GET("api/events")
    suspend fun events(): List<EventItem>

    @GET("api/devices/me")
    suspend fun currentDevice(
        @Query("token") token: String,
    ): Device

    @POST("api/devices/register")
    suspend fun register(
        @Body request: DeviceRegistrationRequest,
    ): Device
}
