package com.wangzhenman.notifysentinel.push

import android.content.Context
import android.util.Log
import com.wangzhenman.notifysentinel.data.SettingsStore
import com.xiaomi.mipush.sdk.ErrorCode
import com.xiaomi.mipush.sdk.MiPushClient
import com.xiaomi.mipush.sdk.MiPushCommandMessage
import com.xiaomi.mipush.sdk.MiPushMessage
import com.xiaomi.mipush.sdk.PushMessageReceiver
import kotlinx.coroutines.runBlocking

class MiPushReceiver : PushMessageReceiver() {
    override fun onReceivePassThroughMessage(
        context: Context,
        message: MiPushMessage,
    ) {
        Log.d(TAG, "pass through message: ${message.content}")
    }

    override fun onNotificationMessageClicked(
        context: Context,
        message: MiPushMessage,
    ) {
        Log.d(TAG, "notification clicked: ${message.description}")
    }

    override fun onNotificationMessageArrived(
        context: Context,
        message: MiPushMessage,
    ) {
        Log.d(TAG, "notification arrived: ${message.description}")
    }

    override fun onCommandResult(
        context: Context,
        message: MiPushCommandMessage,
    ) {
        maybePersistRegId(context, message)
    }

    override fun onReceiveRegisterResult(
        context: Context,
        message: MiPushCommandMessage,
    ) {
        maybePersistRegId(context, message)
    }

    override fun onRequirePermissions(
        context: Context,
        permissions: Array<out String>,
    ) {
        Log.w(TAG, "missing MiPush permissions: ${permissions.joinToString()}")
    }

    private fun maybePersistRegId(
        context: Context,
        message: MiPushCommandMessage,
    ) {
        if (message.command != MiPushClient.COMMAND_REGISTER) {
            return
        }

        if (message.resultCode != ErrorCode.SUCCESS.toLong()) {
            Log.w(TAG, "MiPush register failed: code=${message.resultCode} reason=${message.reason}")
            return
        }

        val regId = message.commandArguments?.firstOrNull().orEmpty()
        if (regId.isBlank()) {
            Log.w(TAG, "MiPush register succeeded but regId is empty")
            return
        }

        runBlocking {
            SettingsStore(context).saveMiPushRegId(regId)
        }

        Log.i(TAG, "MiPush regId saved")
    }

    companion object {
        private const val TAG = "NotifySentinelMiPush"
    }
}