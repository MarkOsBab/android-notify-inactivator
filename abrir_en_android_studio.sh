#!/bin/bash

# Script para abrir el proyecto en Android Studio correctamente

echo "🚀 Abriendo Notify Inactivator en Android Studio..."
echo ""

# Ruta a Android Studio
ANDROID_STUDIO="/Applications/Android Studio.app/Contents/MacOS/studio"

# Verificar que Android Studio está instalado
if [ ! -f "$ANDROID_STUDIO" ]; then
    echo "❌ Android Studio no encontrado en la ubicación estándar"
    echo "   Abre Android Studio manualmente:"
    echo "   1. Abre Android Studio"
    echo "   2. File → Open"
    echo "   3. Selecciona: $(pwd)"
    exit 1
fi

# Abrir el proyecto
echo "✅ Abriendo proyecto en Android Studio..."
open -a "Android Studio" "$(pwd)"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📋 PASOS EN ANDROID STUDIO:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "1. Espera a que Gradle sincronice (puede tardar unos minutos)"
echo "2. Si aparece 'Trust Project', click en 'Trust Project'"
echo "3. Espera a que termine la indexación"
echo "4. Conecta tu dispositivo Android por USB"
echo "5. Activa 'Depuración USB' en tu dispositivo"
echo "6. En Android Studio, selecciona tu dispositivo en el menú"
echo "7. Click en el botón Run (▶️) o presiona Cmd+R"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🐛 Para ver logs si la app crashea:"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "En Android Studio:"
echo "  View → Tool Windows → Logcat"
echo ""
echo "O desde terminal (después de instalar):"
echo "  \$ANDROID_HOME/platform-tools/adb logcat | grep 'notifyinactivator'"
echo ""
