package com.wangzhenman.notifysentinel.data

import com.wangzhenman.notifysentinel.model.Device
import com.wangzhenman.notifysentinel.model.DeviceRegistrationRequest
import com.wangzhenman.notifysentinel.model.EventItem
import com.wangzhenman.notifysentinel.model.HealthResponse
import com.wangzhenman.notifysentinel.network.RetrofitClient
import retrofit2.HttpException

class NotifySentinelRepository {
    suspend fun testConnection(baseUrl: String): HealthResponse {
        return RetrofitClient.create(baseUrl).health()
    }

    suspend fun fetchEvents(baseUrl: String): List<EventItem> {
        return RetrofitClient.create(baseUrl).events()
    }

    suspend fun registerDevice(
        baseUrl: String,
        request: DeviceRegistrationRequest,
    ): Device {
        return RetrofitClient.create(baseUrl).register(request)
    }

    suspend fun fetchRegisteredDevice(
        baseUrl: String,
        token: String,
    ): Device? {
        return try {
            RetrofitClient.create(baseUrl).currentDevice(token = token)
        } catch (error: HttpException) {
            if (error.code() == 404) {
                null
            } else {
                throw error
            }
        }
    }
}
