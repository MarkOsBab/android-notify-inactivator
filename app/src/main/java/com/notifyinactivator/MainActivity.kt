package com.notifyinactivator

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.notifyinactivator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var appAdapter: AppAdapter
    private lateinit var prefsManager: PreferencesManager
    private lateinit var notificationHelper: NotificationHelper
    private var allApps = mutableListOf<AppInfo>()
    private var currentTab = TAB_FAVORITES // Iniciar en Favoritos
    
    companion object {
        const val TAB_FAVORITES = 0
        const val TAB_ALL = 1
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            prefsManager = PreferencesManager(this)
            notificationHelper = NotificationHelper(this)
            
            setupRecyclerView()
            setupTabs()
            updateServiceStatus()
            checkNotificationPermission()
            loadInstalledApps()
            setupSearchFilter()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al iniciar la app: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun updateServiceStatus() {
        val isActive = notificationHelper.isNotificationServiceEnabled()
        val statusMessage = if (isActive) {
            "✅ Servicio Activo"
        } else {
            "⚠️ Servicio Inactivo - Toca para activar"
        }
        
        binding.toolbar.subtitle = statusMessage
        
        // Hacer que el toolbar sea clickeable para abrir configuración
        if (!isActive) {
            binding.toolbar.setOnClickListener {
                checkNotificationPermission()
            }
        } else {
            binding.toolbar.setOnClickListener(null)
        }
    }
    
    private fun setupRecyclerView() {
        appAdapter = AppAdapter(
            apps = mutableListOf(),
            onToggleNotification = { appInfo, isEnabled ->
                toggleNotifications(appInfo, isEnabled)
            },
            onToggleFavorite = { appInfo ->
                toggleFavorite(appInfo)
            }
        )
        binding.recyclerViewApps.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = appAdapter
        }
    }
    
    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: TAB_FAVORITES
                filterAndDisplayApps()
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        
        // El TabLayout ya inicia en posición 0 (Favoritas) por defecto
        // Forzar el filtrado inicial
        filterAndDisplayApps()
    }
    
    private fun checkNotificationPermission() {
        // Verificar si tenemos el permiso de NotificationListenerService
        if (!notificationHelper.isNotificationServiceEnabled()) {
            // Mostrar diálogo explicativo
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("🔔 Permiso Crítico Requerido")
                .setMessage(
                    "Para que esta app pueda BLOQUEAR notificaciones automáticamente, " +
                    "necesitas otorgar el permiso de 'Acceso a Notificaciones'.\n\n" +
                    "📱 En la siguiente pantalla:\n" +
                    "1. Busca 'NotifyInactivator'\n" +
                    "2. Activa el interruptor\n" +
                    "3. Acepta el permiso\n\n" +
                    "⚠️ Sin este permiso, la app NO podrá bloquear notificaciones."
                )
                .setPositiveButton("Abrir Configuración") { _, _ ->
                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    startActivity(intent)
                }
                .setNegativeButton("Más tarde", null)
                .setCancelable(false)
                .show()
        } else {
            // Permiso otorgado, mostrar mensaje de éxito
            Toast.makeText(
                this,
                "✅ Servicio de bloqueo activo",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    private fun loadInstalledApps() {
        binding.progressBar.visibility = android.view.View.VISIBLE
        
        // Ejecutar en un hilo secundario
        Thread {
            try {
                val pm = packageManager
                val packages = pm.getInstalledApplications(0)
                val favorites = prefsManager.getFavorites()
                
                allApps.clear()
                
                for (packageInfo in packages) {
                    try {
                        // Filtrar solo apps con lanzador (apps visibles)
                        val launchIntent = pm.getLaunchIntentForPackage(packageInfo.packageName)
                        if (launchIntent != null) {
                            val appName = packageInfo.loadLabel(pm).toString()
                            val icon = packageInfo.loadIcon(pm)
                            
                            // Obtener estado guardado o usar valor por defecto
                            val areNotificationsEnabled = prefsManager.getNotificationState(
                                packageInfo.packageName,
                                true
                            )
                            
                            val isFavorite = favorites.contains(packageInfo.packageName)
                            
                            allApps.add(
                                AppInfo(
                                    packageName = packageInfo.packageName,
                                    appName = appName,
                                    icon = icon,
                                    notificationsEnabled = areNotificationsEnabled,
                                    isFavorite = isFavorite
                                )
                            )
                        }
                    } catch (e: Exception) {
                        // Ignorar apps que causan errores
                        e.printStackTrace()
                    }
                }
                
                // Ordenar: favoritas primero, luego alfabéticamente
                // Usamos sortedWith en lugar de sortWith para evitar el error "comparison violates its general contract"
                allApps = allApps.sortedWith(
                    compareBy<AppInfo> { !it.isFavorite } // false (favoritos) primero, luego true (no favoritos)
                        .thenBy { it.appName.lowercase() }
                ).toMutableList()
                
                // Actualizar UI en el hilo principal
                runOnUiThread {
                    binding.progressBar.visibility = android.view.View.GONE
                    filterAndDisplayApps()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(
                        this@MainActivity,
                        "Error al cargar aplicaciones: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }
    
    private fun setupSearchFilter() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAndDisplayApps()
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun filterAndDisplayApps() {
        val query = binding.editTextSearch.text.toString()
        
        // Filtrar por query
        var filteredList = if (query.isEmpty()) {
            allApps
        } else {
            allApps.filter { 
                it.appName.contains(query, ignoreCase = true) || 
                it.packageName.contains(query, ignoreCase = true)
            }
        }
        
        // Filtrar por tab
        filteredList = when (currentTab) {
            TAB_FAVORITES -> filteredList.filter { it.isFavorite }
            else -> filteredList
        }
        
        appAdapter.updateApps(filteredList)
        
        // Actualizar contador
        val count = filteredList.size
        binding.textViewAppCount.text = when (currentTab) {
            TAB_FAVORITES -> "Apps favoritas: $count"
            else -> if (query.isEmpty()) "Total de apps: $count" else "Apps encontradas: $count"
        }
    }
    
    private fun toggleNotifications(appInfo: AppInfo, isEnabled: Boolean) {
        // Guardar el estado
        prefsManager.saveNotificationState(appInfo.packageName, isEnabled)
        appInfo.notificationsEnabled = isEnabled
        
        if (!isEnabled) {
            // Verificar si el servicio está activo
            if (notificationHelper.isNotificationServiceEnabled()) {
                // El servicio bloqueará automáticamente las notificaciones
                Toast.makeText(
                    this,
                    "🚫 ${appInfo.appName} bloqueada\nLas notificaciones se cancelarán automáticamente",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Servicio no activo, solicitar permiso
                Toast.makeText(
                    this,
                    "⚠️ Necesitas activar el permiso primero\nVe a Configuración",
                    Toast.LENGTH_LONG
                ).show()
                checkNotificationPermission()
            }
        } else {
            Toast.makeText(
                this,
                "✅ ${appInfo.appName} desbloqueada\nLas notificaciones llegarán normalmente",
                Toast.LENGTH_SHORT
            ).show()
        }
        
        // Actualizar la vista
        appAdapter.notifyDataSetChanged()
    }
    
    private fun toggleFavorite(appInfo: AppInfo) {
        appInfo.isFavorite = !appInfo.isFavorite
        
        if (appInfo.isFavorite) {
            prefsManager.addFavorite(appInfo.packageName)
            Toast.makeText(this, "${appInfo.appName} añadida a favoritas ⭐", Toast.LENGTH_SHORT).show()
        } else {
            prefsManager.removeFavorite(appInfo.packageName)
            Toast.makeText(this, "${appInfo.appName} eliminada de favoritas", Toast.LENGTH_SHORT).show()
        }
        
        // Re-ordenar usando sortedWith para evitar errores de comparación
        allApps = allApps.sortedWith(
            compareBy<AppInfo> { !it.isFavorite }
                .thenBy { it.appName.lowercase() }
        ).toMutableList()
        
        filterAndDisplayApps()
    }
    
    override fun onResume() {
        super.onResume()
        // Actualizar el estado del servicio cuando volvemos a la app
        updateServiceStatus()
        // NO recargar apps aquí, solo la primera vez
        // loadInstalledApps() - COMENTADO para no perder favoritos
    }
}
