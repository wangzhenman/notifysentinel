package com.wangzhenman.notifysentinel.util

fun normalizeServerUrl(input: String): String {
    val trimmed = input.trim()

    val withScheme = when {
        trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
        trimmed.isBlank() -> "http://192.168.1.10:8080"
        else -> "http://$trimmed"
    }

    return if (withScheme.endsWith('/')) withScheme else "$withScheme/"
}
