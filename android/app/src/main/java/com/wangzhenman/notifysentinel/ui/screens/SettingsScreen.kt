package com.wangzhenman.notifysentinel.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
fun SettingsScreen(
    state: AppUiState,
    onServerUrlChanged: (String) -> Unit,
    onTestConnection: () -> Unit,
    onSave: () -> Unit,
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
            text = "服务器设置",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = state.serverUrlDraft,
                    onValueChange = onServerUrlChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Server Address") },
                    placeholder = { Text("http://192.168.1.10:8080") },
                    singleLine = true,
                )

                Text(
                    text = "真机通常填写 NAS 或服务器的局域网地址；模拟器访问本机可用 10.0.2.2。",
                    style = MaterialTheme.typography.bodyMedium,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        onClick = onTestConnection,
                        enabled = !state.isCheckingConnection && state.serverUrlDraft.isNotBlank(),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(if (state.isCheckingConnection) "测试中..." else "测试连接")
                    }

                    Button(
                        onClick = onSave,
                        enabled = state.serverUrlDraft.isNotBlank(),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("保存")
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
                    text = "连接状态",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(text = state.connectionStatus)
                Text(text = "已保存地址: ${state.savedServerUrl.ifBlank { "未保存" }}")
            }
        }
    }
}
