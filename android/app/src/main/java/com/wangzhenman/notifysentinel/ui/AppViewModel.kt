package com.wangzhenman.notifysentinel.ui

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wangzhenman.notifysentinel.data.NotifySentinelRepository
import com.wangzhenman.notifysentinel.data.SettingsStore
import com.wangzhenman.notifysentinel.model.DeviceRegistrationRequest
import com.wangzhenman.notifysentinel.util.normalizeServerUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    appContext: Context,
) : ViewModel() {
    private val repository = NotifySentinelRepository()
    private val settingsStore = SettingsStore(appContext)
    private val fallbackToken = buildFallbackToken(appContext)

    private val _uiState = MutableStateFlow(
        AppUiState(
            deviceName = buildDefaultDeviceName(),
            generatedToken = fallbackToken,
        ),
    )
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsStore.serverUrl.collect { serverUrl ->
                _uiState.update { current ->
                    current.copy(
                        serverUrlDraft = serverUrl,
                        savedServerUrl = serverUrl,
                    )
                }

                refreshConnectionStatus(serverUrl)
                refreshRegistrationStatus(serverUrl)
            }
        }

        viewModelScope.launch {
            settingsStore.miPushRegId.collect { regId ->
                _uiState.update { current ->
                    val hasMiPush = regId.isNotBlank()
                    current.copy(
                        generatedToken = if (hasMiPush) regId else fallbackToken,
                        pushPlatform = if (hasMiPush) "mipush" else "android",
                        registrationStatus = when {
                            hasMiPush && current.registeredDevice?.token != regId -> "MiPush 已就绪，请重新注册设备"
                            !hasMiPush && current.registeredDevice == null -> current.registrationStatus
                            else -> current.registrationStatus
                        },
                    )
                }

                refreshRegistrationStatus(
                    uiState.value.savedServerUrl.ifBlank { uiState.value.serverUrlDraft },
                )
            }
        }
    }

    fun updateServerUrlDraft(value: String) {
        _uiState.update { current ->
            current.copy(serverUrlDraft = value)
        }
    }

    fun updateDeviceName(value: String) {
        _uiState.update { current ->
            current.copy(deviceName = value)
        }
    }

    fun saveServerUrl() {
        val normalizedUrl = normalizeServerUrl(uiState.value.serverUrlDraft)
        val savedUrl = normalizedUrl.removeSuffix("/")

        viewModelScope.launch {
            settingsStore.saveServerUrl(savedUrl)
            _uiState.update { current ->
                current.copy(
                    serverUrlDraft = savedUrl,
                    savedServerUrl = savedUrl,
                    connectionStatus = "服务器地址已保存",
                )
            }

            refreshConnectionStatus(savedUrl)
            refreshRegistrationStatus(savedUrl)
        }
    }

    fun testConnection() {
        refreshConnectionStatus(
            serverUrl = uiState.value.serverUrlDraft,
            showLoading = true,
        )
    }

    fun refreshEvents() {
        val serverUrl = uiState.value.savedServerUrl.ifBlank { uiState.value.serverUrlDraft }

        _uiState.update { current ->
            current.copy(
                isLoadingEvents = true,
                eventsError = null,
            )
        }

        viewModelScope.launch {
            runCatching {
                repository.fetchEvents(serverUrl)
            }.onSuccess { events ->
                _uiState.update { current ->
                    current.copy(
                        isLoadingEvents = false,
                        events = events,
                        connectionStatus = if (current.savedServerUrl.isNotBlank() || current.serverUrlDraft.isNotBlank()) {
                            "已连接"
                        } else {
                            current.connectionStatus
                        },
                    )
                }
            }.onFailure { throwable ->
                _uiState.update { current ->
                    current.copy(
                        isLoadingEvents = false,
                        eventsError = throwable.message ?: "拉取事件失败",
                    )
                }
            }
        }
    }

    private fun refreshConnectionStatus(
        serverUrl: String,
        showLoading: Boolean = false,
    ) {
        if (serverUrl.isBlank()) {
            _uiState.update { current ->
                current.copy(
                    isCheckingConnection = false,
                    connectionStatus = "未连接",
                )
            }
            return
        }

        if (showLoading) {
            _uiState.update { current ->
                current.copy(isCheckingConnection = true)
            }
        }

        viewModelScope.launch {
            runCatching {
                repository.testConnection(serverUrl)
            }.onSuccess { response ->
                _uiState.update { current ->
                    current.copy(
                        isCheckingConnection = false,
                        connectionStatus = "已连接: ${response.name} (${response.status})",
                    )
                }
            }.onFailure { throwable ->
                _uiState.update { current ->
                    current.copy(
                        isCheckingConnection = false,
                        connectionStatus = throwable.message ?: "连接失败",
                    )
                }
            }
        }
    }

    fun registerDevice() {
        val snapshot = uiState.value
        val serverUrl = snapshot.savedServerUrl.ifBlank { snapshot.serverUrlDraft }

        _uiState.update { current ->
            current.copy(isRegistering = true)
        }

        viewModelScope.launch {
            runCatching {
                repository.registerDevice(
                    baseUrl = serverUrl,
                    request = DeviceRegistrationRequest(
                        name = snapshot.deviceName.ifBlank { buildDefaultDeviceName() },
                        platform = snapshot.pushPlatform,
                        token = snapshot.generatedToken,
                    ),
                )
            }.onSuccess { device ->
                _uiState.update { current ->
                    current.copy(
                        isRegistering = false,
                        registrationStatus = "已注册",
                        registeredDevice = device,
                        deviceName = device.name,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update { current ->
                    current.copy(
                        isRegistering = false,
                        registrationStatus = throwable.message ?: "注册失败",
                    )
                }
            }
        }
    }

    fun refreshRegistrationStatus() {
        refreshRegistrationStatus(
            uiState.value.savedServerUrl.ifBlank { uiState.value.serverUrlDraft },
        )
    }

    private fun refreshRegistrationStatus(serverUrl: String) {
        if (serverUrl.isBlank()) {
            _uiState.update { current ->
                current.copy(
                    isRefreshingRegistration = false,
                    registrationStatus = "未注册",
                    registeredDevice = null,
                )
            }
            return
        }

        val token = uiState.value.generatedToken

        _uiState.update { current ->
            current.copy(
                isRefreshingRegistration = true,
                registrationStatus = if (current.registeredDevice == null) "检查注册状态中..." else current.registrationStatus,
            )
        }

        viewModelScope.launch {
            runCatching {
                repository.fetchRegisteredDevice(serverUrl, token)
            }.onSuccess { device ->
                _uiState.update { current ->
                    current.copy(
                        isRefreshingRegistration = false,
                        registrationStatus = if (device != null) "已注册" else "未注册",
                        registeredDevice = device,
                        deviceName = device?.name ?: current.deviceName,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update { current ->
                    current.copy(
                        isRefreshingRegistration = false,
                        registrationStatus = throwable.message ?: "注册状态同步失败",
                    )
                }
            }
        }
    }

    private fun buildDefaultDeviceName(): String {
        val manufacturer = Build.MANUFACTURER?.trim().orEmpty()
        val model = Build.MODEL?.trim().orEmpty()

        return listOf(manufacturer, model)
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString(" ")
            .ifBlank { "Android Device" }
    }

    private fun buildFallbackToken(context: Context): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID,
        )

        return "android-${androidId ?: "temp-token"}"
    }
}
