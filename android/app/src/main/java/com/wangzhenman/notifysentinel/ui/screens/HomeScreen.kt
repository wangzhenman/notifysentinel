package com.wangzhenman.notifysentinel.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wangzhenman.notifysentinel.model.EventItem
import com.wangzhenman.notifysentinel.ui.AppUiState

@Composable
fun HomeScreen(
    state: AppUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "NotifySentinel",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(text = "服务器: ${state.savedServerUrl.ifBlank { "未设置" }}")
                Text(text = "连接状态: ${state.connectionStatus}")

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onRefresh,
                        enabled = !state.isLoadingEvents && state.savedServerUrl.isNotBlank(),
                    ) {
                        Text(if (state.isLoadingEvents) "刷新中..." else "刷新事件")
                    }
                }
            }
        }

        state.eventsError?.let { error ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (state.events.isEmpty() && !state.isLoadingEvents) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "暂无事件，先在设置页连通服务器，再刷新历史记录。",
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }

            items(state.events, key = { event -> event.id }) { event ->
                EventCard(event = event)
            }
        }
    }
}

@Composable
private fun EventCard(event: EventItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(text = "来源: ${event.source}")
            Text(text = "级别: ${event.level}")
            Text(text = event.message.ifBlank { "无详细信息" })
            event.createdAt?.let { createdAt ->
                Text(
                    text = "时间: ${createdAt.replace('T', ' ').removeSuffix("Z")}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
