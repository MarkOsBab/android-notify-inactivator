package com.notifyinactivator

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

/**
 * Servicio que escucha todas las notificaciones del sistema
 * y cancela automáticamente las de apps que el usuario ha desactivado
 */
class NotificationListener : NotificationListenerService() {

    private lateinit var prefsManager: PreferencesManager

    companion object {
        private const val TAG = "NotificationListener"
        var isServiceRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        prefsManager = PreferencesManager(this)
        isServiceRunning = true
        Log.d(TAG, "NotificationListener iniciado")
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        Log.d(TAG, "NotificationListener detenido")
    }

    /**
     * Se llama cuando llega una nueva notificación
     */
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        
        sbn?.let {
            val packageName = it.packageName
            
            // Verificar si el usuario ha desactivado las notificaciones para esta app
            val isEnabled = prefsManager.getNotificationState(packageName)
            
            if (!isEnabled) {
                // Cancelar la notificación inmediatamente
                cancelNotification(it.key)
                Log.d(TAG, "Notificación cancelada de: $packageName")
            } else {
                Log.d(TAG, "Notificación permitida de: $packageName")
            }
        }
    }

    /**
     * Se llama cuando se elimina una notificación
     */
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        // Opcional: puedes registrar cuando se eliminan notificaciones
    }

    /**
     * Se llama cuando el servicio se conecta al sistema
     */
    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Listener conectado al sistema")
        
        // Opcional: cancelar notificaciones existentes al iniciar
        try {
            activeNotifications?.forEach { sbn ->
                val packageName = sbn.packageName
                val isEnabled = prefsManager.getNotificationState(packageName)
                
                if (!isEnabled) {
                    cancelNotification(sbn.key)
                    Log.d(TAG, "Notificación existente cancelada de: $packageName")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al procesar notificaciones existentes", e)
        }
    }

    /**
     * Se llama cuando el servicio se desconecta del sistema
     */
    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "Listener desconectado del sistema")
    }
}
