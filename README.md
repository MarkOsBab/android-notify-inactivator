# Notify Inactivator - Android App

Aplicación Android para gestionar las notificaciones de todas tus aplicaciones instaladas de forma centralizada.

---

## ⚠️ INICIO RÁPIDO

```bash
# 1. Verificar tu entorno
./verificar_entorno.sh

# 2. Abrir en Android Studio
# File → Open → Selecciona esta carpeta

# 3. Esperar a que Gradle sincronice

# 4. Click en Run (▶️)
```

📖 **¿Primera vez con Android?** Lee [`INICIO_RAPIDO.md`](INICIO_RAPIDO.md)

---

## 🚀 Características

- **Lista de todas las aplicaciones**: Visualiza todas las apps instaladas con icono y nombre
- **Sistema de favoritos**: Marca tus apps más importantes como favoritas con un botón de estrella
- **Pestaña de favoritos**: Acceso rápido a tus apps favoritas en una pestaña dedicada
- **Buscador integrado**: Encuentra rápidamente cualquier aplicación por nombre o paquete
- **Switches individuales**: Activa o desactiva las notificaciones de cada app con un simple switch
- **Toggle directo**: Las notificaciones se activan/desactivan directamente sin abrir configuración
- **Persistencia de datos**: Los estados de notificaciones y favoritos se guardan automáticamente
- **Interfaz moderna**: Diseño Material Design con cards, tabs y animaciones
- **Contador de apps**: Visualiza cuántas aplicaciones tienes instaladas y filtradas
- **Cancelación automática**: Al desactivar notificaciones, se cancelan las notificaciones activas

## 📋 Requisitos

- Android Studio Arctic Fox o superior
- Android SDK 24 (Android 7.0) o superior
- Kotlin 1.9.0
- JDK 8 o superior

## 🛠️ Instalación

⚠️ **IMPORTANTE: Este proyecto debe abrirse en Android Studio**

### Paso a Paso:

1. **Descarga/Clona el proyecto** en tu computadora
2. **Abre Android Studio**
3. **File → Open** y selecciona la carpeta del proyecto
4. **Espera a que Gradle sincronice** (Android Studio instalará todo automáticamente)
5. **Conecta un dispositivo** o inicia un emulador
6. **Click en Run (▶️)** o presiona `Cmd+R`

📖 **¿Problemas con Gradle?** Consulta [`INICIO_RAPIDO.md`](INICIO_RAPIDO.md) para soluciones

## ⚙️ Configuración

Al iniciar la aplicación por primera vez, se te pedirá que otorgues permisos para:

- **Acceso a política de notificaciones**: Necesario para poder gestionar las notificaciones de otras apps

La app te guiará automáticamente a la configuración correspondiente.

## 📱 Uso

1. **Buscar apps**: Usa el buscador en la parte superior para filtrar aplicaciones
2. **Marcar favoritas**: Toca el icono de estrella ⭐ en cualquier app para marcarla como favorita
3. **Ver favoritas**: Usa la pestaña "Favoritas" para ver solo tus apps más importantes
4. **Activar/Desactivar notificaciones**: 
   - Toca el switch de cualquier app para cambiar su estado
   - El estado se guarda automáticamente
   - Al desactivar, se cancelan las notificaciones activas de esa app
5. **Toggle rápido**: Toca en cualquier parte del card de una app para cambiar su switch
6. **Información detallada**: Cada app muestra su nombre y nombre del paquete

## 🔒 Permisos

La aplicación requiere:
- `POST_NOTIFICATIONS`: Para gestionar notificaciones (Android 13+)
- Acceso a la política de notificaciones del sistema

## 📂 Estructura del Proyecto

```
app/
├── src/main/
│   ├── java/com/notifyinactivator/
│   │   ├── MainActivity.kt          # Actividad principal con tabs
│   │   ├── AppAdapter.kt           # Adaptador del RecyclerView
│   │   ├── AppInfo.kt              # Modelo de datos
│   │   ├── PreferencesManager.kt   # Gestión de favoritos y estados
│   │   └── NotificationHelper.kt   # Helper para notificaciones
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml   # Layout con tabs y buscador
│   │   │   └── item_app.xml        # Layout con estrella y switch
│   │   ├── values/
│   │   │   ├── colors.xml          # Colores
│   │   │   ├── strings.xml         # Textos
│   │   │   └── themes.xml          # Temas
│   │   └── mipmap/                 # Iconos de la app
│   └── AndroidManifest.xml
├── build.gradle
└── proguard-rules.pro
```

## 🎨 Tecnologías Utilizadas

- **Kotlin**: Lenguaje de programación
- **Material Design 3**: Componentes UI modernos (Tabs, Cards, Switches)
- **ViewBinding**: Vinculación de vistas type-safe
- **RecyclerView**: Lista eficiente de aplicaciones
- **SharedPreferences**: Persistencia de favoritos y estados
- **NotificationManager**: Gestión directa de notificaciones
- **AndroidX**: Librerías modernas de Android

## ⚠️ Nota Importante

**Gestión Directa de Notificaciones**: Esta app implementa un sistema de toggle directo que:

1. **Guarda el estado** de las notificaciones en preferencias locales
2. **Cancela notificaciones activas** cuando desactivas una app
3. **Persiste tus preferencias** entre sesiones de la app
4. **Gestiona favoritos** para acceso rápido

**Importante**: Aunque la app no puede bloquear físicamente las notificaciones a nivel del sistema (por restricciones de Android), mantiene un registro de tus preferencias y cancela automáticamente las notificaciones activas cuando las "desactivas".

## 🐛 Solución de Problemas

**La app no carga las aplicaciones:**
- Verifica que hayas otorgado los permisos necesarios
- Reinicia la aplicación

**Los switches no reflejan el estado real:**
- La app guarda tus preferencias localmente
- El estado mostrado es el que has configurado en la app

**Las notificaciones siguen llegando:**
- La app cancela las notificaciones activas cuando las desactivas
- Para bloqueo total, usa la configuración del sistema de Android

## 📄 Licencia

Este proyecto es de código abierto y está disponible para uso personal y educativo.

## 👨‍💻 Desarrollo

Para compilar el proyecto:

```bash
./gradlew build
```

Para instalar en un dispositivo:

```bash
./gradlew installDebug
```

## 🔄 Actualizaciones Futuras

Posibles mejoras:
- Widget para acceso rápido a favoritos
- Perfiles de notificaciones (trabajo, casa, dormir)
- Estadísticas de notificaciones bloqueadas
- Modo automático por horario
- Temas personalizables
- Grupos de apps personalizados
- Exportar/importar configuraciones
- Integración con DND (Do Not Disturb)

---

¡Disfruta de una gestión más sencilla de tus notificaciones! 📱✨
