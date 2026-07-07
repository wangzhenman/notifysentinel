package com.wangzhenman.notifysentinel.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wangzhenman.notifysentinel.ui.screens.DeviceScreen
import com.wangzhenman.notifysentinel.ui.screens.HomeScreen
import com.wangzhenman.notifysentinel.ui.screens.SettingsScreen

@Composable
fun NotifySentinelApp(
    viewModel: AppViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf(AppTab.HOME) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == AppTab.HOME,
                    onClick = { selectedTab = AppTab.HOME },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = AppTab.HOME.label) },
                    label = { Text(AppTab.HOME.label) },
                )
                NavigationBarItem(
                    selected = selectedTab == AppTab.SETTINGS,
                    onClick = { selectedTab = AppTab.SETTINGS },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = AppTab.SETTINGS.label) },
                    label = { Text(AppTab.SETTINGS.label) },
                )
                NavigationBarItem(
                    selected = selectedTab == AppTab.DEVICE,
                    onClick = { selectedTab = AppTab.DEVICE },
                    icon = { Icon(Icons.Filled.Devices, contentDescription = AppTab.DEVICE.label) },
                    label = { Text(AppTab.DEVICE.label) },
                )
            }
        },
    ) { innerPadding ->
        when (selectedTab) {
            AppTab.HOME -> HomeScreen(
                state = uiState,
                onRefresh = viewModel::refreshEvents,
                modifier = Modifier.padding(innerPadding),
            )

            AppTab.SETTINGS -> SettingsScreen(
                state = uiState,
                onServerUrlChanged = viewModel::updateServerUrlDraft,
                onTestConnection = viewModel::testConnection,
                onSave = viewModel::saveServerUrl,
                modifier = Modifier.padding(innerPadding),
            )

            AppTab.DEVICE -> DeviceScreen(
                state = uiState,
                onDeviceNameChanged = viewModel::updateDeviceName,
                onRegister = viewModel::registerDevice,
                onRefreshStatus = viewModel::refreshRegistrationStatus,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
