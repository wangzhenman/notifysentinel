package com.wangzhenman.notifysentinel.model

data class DeviceRegistrationRequest(
    val name: String,
    val platform: String,
    val token: String,
)
