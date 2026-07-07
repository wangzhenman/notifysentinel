package com.wangzhenman.notifysentinel

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import android.util.Log
import com.xiaomi.channel.commonutils.logger.LoggerInterface
import com.xiaomi.mipush.sdk.Logger
import com.xiaomi.mipush.sdk.MiPushClient

class NotifySentinelApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Logger.setLogger(this, object : LoggerInterface {
            override fun setTag(tag: String?) = Unit

            override fun log(content: String?) {
                Log.d(MIPUSH_TAG, content ?: "")
            }

            override fun log(content: String?, t: Throwable?) {
                Log.d(MIPUSH_TAG, content ?: "", t)
            }
        })

        if (!isMainProcess()) {
            return
        }

        if (BuildConfig.MIPUSH_APP_ID.isBlank() || BuildConfig.MIPUSH_APP_KEY.isBlank()) {
            Log.w(MIPUSH_TAG, "MiPush disabled: missing MIPUSH_APP_ID or MIPUSH_APP_KEY")
            return
        }

        MiPushClient.registerPush(
            this,
            BuildConfig.MIPUSH_APP_ID,
            BuildConfig.MIPUSH_APP_KEY,
        )
    }

    private fun isMainProcess(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = activityManager.runningAppProcesses ?: return false
        val currentPid = Process.myPid()
        val mainProcessName = applicationInfo.processName

        return runningProcesses.any { processInfo ->
            processInfo.pid == currentPid && processInfo.processName == mainProcessName
        }
    }

    companion object {
        private const val MIPUSH_TAG = "NotifySentinelMiPush"
    }
}