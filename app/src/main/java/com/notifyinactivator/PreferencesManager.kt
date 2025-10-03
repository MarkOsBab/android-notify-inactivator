package com.notifyinactivator

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("NotifyInactivatorPrefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_FAVORITES = "favorites"
        private const val KEY_NOTIFICATIONS_PREFIX = "notifications_"
    }
    
    // Guardar favoritos
    fun saveFavorites(favorites: Set<String>) {
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply()
    }
    
    // Obtener favoritos
    fun getFavorites(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }
    
    // Agregar a favoritos
    fun addFavorite(packageName: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.add(packageName)
        saveFavorites(favorites)
    }
    
    // Remover de favoritos
    fun removeFavorite(packageName: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.remove(packageName)
        saveFavorites(favorites)
    }
    
    // Verificar si es favorito
    fun isFavorite(packageName: String): Boolean {
        return getFavorites().contains(packageName)
    }
    
    // Guardar estado de notificaciones
    fun saveNotificationState(packageName: String, enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_PREFIX + packageName, enabled).apply()
    }
    
    // Obtener estado de notificaciones
    fun getNotificationState(packageName: String, defaultValue: Boolean = true): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_PREFIX + packageName, defaultValue)
    }
}
