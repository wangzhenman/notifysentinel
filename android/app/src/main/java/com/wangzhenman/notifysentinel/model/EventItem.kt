package com.wangzhenman.notifysentinel.model

import com.google.gson.annotations.SerializedName

data class EventItem(
    val id: Long,
    val source: String,
    val level: String,
    val title: String,
    val message: String,
    @SerializedName("created_at")
    val createdAt: String? = null,
)
