package com.wangzhenman.notifysentinel.ui

import com.wangzhenman.notifysentinel.model.Device
import com.wangzhenman.notifysentinel.model.EventItem

enum class AppTab(val label: String) {
    HOME("首页"),
    SETTINGS("设置"),
    DEVICE("设备"),
}

data class AppUiState(
    val serverUrlDraft: String = "",
    val savedServerUrl: String = "",
    val connectionStatus: String = "未连接",
    val isCheckingConnection: Boolean = false,
    val events: List<EventItem> = emptyList(),
    val isLoadingEvents: Boolean = false,
    val eventsError: String? = null,
    val deviceName: String = "",
    val generatedToken: String = "",
    val pushPlatform: String = "android",
    val isRegistering: Boolean = false,
    val isRefreshingRegistration: Boolean = false,
    val registrationStatus: String = "未注册",
    val registeredDevice: Device? = null,
)
