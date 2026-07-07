package com.wangzhenman.notifysentinel

import android.Manifest
import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wangzhenman.notifysentinel.ui.AppViewModel
import com.wangzhenman.notifysentinel.ui.AppViewModelFactory
import com.wangzhenman.notifysentinel.ui.NotifySentinelApp
import com.wangzhenman.notifysentinel.ui.theme.NotifySentinelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NotifySentinelTheme {
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = {},
                )

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                val viewModel: AppViewModel = viewModel(
                    factory = AppViewModelFactory(applicationContext),
                )

                NotifySentinelApp(viewModel = viewModel)
            }
        }
    }
}
