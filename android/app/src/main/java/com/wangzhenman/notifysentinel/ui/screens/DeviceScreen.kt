package com.wangzhenman.notifysentinel.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wangzhenman.notifysentinel.ui.AppUiState

@Composable
fun DeviceScreen(
    state: AppUiState,
    onDeviceNameChanged: (String) -> Unit,
    onRegister: () -> Unit,
    onRefreshStatus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "设备注册",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = state.deviceName,
                    onValueChange = onDeviceNameChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("设备名称") },
                    singleLine = true,
                )

                OutlinedTextField(
                    value = state.pushPlatform,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("平台") },
                    readOnly = true,
                    singleLine = true,
                )

                OutlinedTextField(
                    value = state.generatedToken,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(if (state.pushPlatform == "mipush") "MiPush RegID" else "临时 Token") },
                    readOnly = true,
                    minLines = 2,
                )

                Text(
                    text = if (state.pushPlatform == "mipush") {
                        "MiPush 已初始化，当前使用 RegID 作为设备推送 Token。"
                    } else {
                        "等待 MiPush SDK 返回 RegID；在此之前会保留临时 Token。"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        onClick = onRegister,
                        enabled = !state.isRegistering && state.savedServerUrl.isNotBlank(),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(if (state.isRegistering) "注册中..." else "注册设备")
                    }

                    Button(
                        onClick = onRefreshStatus,
                        enabled = !state.isRefreshingRegistration && state.savedServerUrl.isNotBlank(),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(if (state.isRefreshingRegistration) "同步中..." else "同步状态")
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "当前状态",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(text = state.registrationStatus)
                state.registeredDevice?.let { device ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "设备 ID: ${device.id}")
                    Text(text = "名称: ${device.name}")
                    Text(text = "启用: ${if (device.enabled) "是" else "否"}")
                }
            }
        }
    }
}
