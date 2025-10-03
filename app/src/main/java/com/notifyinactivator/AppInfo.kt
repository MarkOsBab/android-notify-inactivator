package com.notifyinactivator

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable,
    var notificationsEnabled: Boolean,
    var isFavorite: Boolean = false
)
