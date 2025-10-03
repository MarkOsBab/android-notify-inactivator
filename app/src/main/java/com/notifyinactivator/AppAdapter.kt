package com.notifyinactivator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.notifyinactivator.databinding.ItemAppBinding

class AppAdapter(
    private var apps: MutableList<AppInfo>,
    private val onToggleNotification: (AppInfo, Boolean) -> Unit,
    private val onToggleFavorite: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {
    
    inner class AppViewHolder(private val binding: ItemAppBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(appInfo: AppInfo) {
            binding.apply {
                imageViewAppIcon.setImageDrawable(appInfo.icon)
                textViewAppName.text = appInfo.appName
                textViewPackageName.text = appInfo.packageName
                
                // Actualizar botón de favorito
                updateFavoriteButton(appInfo.isFavorite)
                
                // Remover listener anterior para evitar llamadas duplicadas
                switchNotifications.setOnCheckedChangeListener(null)
                
                // Establecer estado actual
                switchNotifications.isChecked = appInfo.notificationsEnabled
                
                // Agregar nuevo listener para el switch
                switchNotifications.setOnCheckedChangeListener { _, isChecked ->
                    appInfo.notificationsEnabled = isChecked
                    onToggleNotification(appInfo, isChecked)
                }
                
                // Listener para el botón de favorito
                buttonFavorite.setOnClickListener {
                    onToggleFavorite(appInfo)
                    updateFavoriteButton(appInfo.isFavorite)
                }
                
                // Click en el item completo para cambiar el switch
                root.setOnClickListener {
                    switchNotifications.isChecked = !switchNotifications.isChecked
                }
            }
        }
        
        private fun updateFavoriteButton(isFavorite: Boolean) {
            binding.buttonFavorite.setImageResource(
                if (isFavorite) android.R.drawable.star_big_on
                else android.R.drawable.star_big_off
            )
            
            // Cambiar color del tint
            binding.buttonFavorite.imageTintList = android.content.res.ColorStateList.valueOf(
                if (isFavorite) 
                    binding.root.context.getColor(R.color.accent)
                else 
                    binding.root.context.getColor(R.color.text_secondary)
            )
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(apps[position])
    }
    
    override fun getItemCount(): Int = apps.size
    
    fun updateApps(newApps: List<AppInfo>) {
        apps.clear()
        apps.addAll(newApps)
        notifyDataSetChanged()
    }
}
