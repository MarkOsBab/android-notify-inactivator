package com.notifyinactivator

import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.provider.Settings
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationManagerCompat

class NotificationHelper(private val context: Context) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    /**
     * Verifica si nuestra app tiene permiso de NotificationListenerService
     * Este es el permiso CRÍTICO para cancelar notificaciones de otras apps
     */
    fun isNotificationServiceEnabled(): Boolean {
        val packageName = context.packageName
        val flat = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        
        if (flat.isNullOrEmpty()) {
            return false
        }
        
        val names = flat.split(":")
        for (name in names) {
            val componentName = ComponentName.unflattenFromString(name)
            if (componentName != null) {
                if (packageName == componentName.packageName) {
                    return true
                }
            }
        }
        return false
    }
    
    /**
     * Verifica si las notificaciones están habilitadas para un paquete específico
     */
    fun areNotificationsEnabled(packageName: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManagerCompat = NotificationManagerCompat.from(context)
                notificationManagerCompat.areNotificationsEnabled()
            } else {
                true
            }
        } catch (e: Exception) {
            true
        }
    }
    
    /**
     * Intenta cancelar todas las notificaciones de un paquete
     * Esto es lo más cercano a "desactivar" notificaciones que podemos hacer
     */
    fun cancelAllNotifications(packageName: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNotifications = notificationManager.activeNotifications
                for (notification in activeNotifications) {
                    if (notification.packageName == packageName) {
                        notificationManager.cancel(notification.id)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Obtiene el conteo de notificaciones activas para un paquete
     */
    fun getNotificationCount(packageName: String): Int {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager.activeNotifications.count { 
                    it.packageName == packageName 
                }
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Verifica si tenemos acceso a las notificaciones del sistema
     */
    fun hasNotificationListenerPermission(): Boolean {
        return try {
            notificationManager.isNotificationPolicyAccessGranted
        } catch (e: Exception) {
            false
        }
    }
}
