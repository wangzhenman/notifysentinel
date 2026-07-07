package com.wangzhenman.notifysentinel.model

import com.google.gson.annotations.SerializedName

data class Device(
    val id: Long,
    val name: String,
    val platform: String,
    val token: String,
    val enabled: Boolean,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
)
