# 🎧 BeatFlow DJ - Live Companion (Android Tablet App)

**BeatFlow DJ** es una aplicación Android profesional orientada a tablets (pantallas de 10" o superiores en modo horizontal / landscape), diseñada para servir como pantalla de apoyo en vivo para el DJ durante actuaciones en booth con poca luz.

---

## 🚀 Características Principales

### 1. 🎛️ Panel Superior (Control de Energía en Vivo)
- **Slider Táctil de Energía (1 a 10)**: Ajusta dinámicamente la respuesta del público.
- **Selector de Intención del Set**:
  - `[Sostener Energía]`: Mantiene la pista balanceada.
  - `[Subir Intensidad / Peak]`: Incrementa la energía (+2 niveles) orientando el set a un climax.
  - `[Bajar / Transición de Descanso]`: Modula suavemente hacia un tempo/energía menor (-2 niveles).

### 2. 💽 Panel Izquierdo (Deck Sonando / Current Track)
- **Arte de Vinilo Animado**: Giratorio durante la reproducción.
- **Badge Camelot & BPM**: Identificación visual instantánea de la clave tonal (ej: `8A / Am`) y BPM.
- **Progreso en Vivo & Temporizador Doble**: Muestra tiempo transcurrido y restante.
- **Alerta de Mezcla / Outro**: Destaca en color magenta neón cuando el tiempo de reproducción alcanza el compás óptimo de salida.

### 3. ⚡ Panel Derecho (Top 3 Sugerencias Smart)
- **Matriz de Armonía de Camelot**: Rueda armónica de 1A-12A y 1B-12B con validación de variaciones $\pm 5\%$ de BPM.
- **Match Score %**: Porcentaje de compatibilidad armónica, de tempo y nivel de energía.
- **Punto de Mezcla Sugerido (Crossover)**: Indica el minuto/segundo exacto y la cantidad de compases de frase para soltar la canción recomendada (ej: *"Mezclar en min 03:15 / Outro de 16 compases"*).
- **Botón "Cargar a Deck"**: Carga la recomendación como pista activa inmediatamente.

### 4. 📁 Panel Inferior (Gestión de Biblioteca)
- **Escáner de Archivos Locales (SAF)**: Importación directa de archivos MP3, WAV y FLAC desde la memoria interna o tarjetas SD.
- **Generador ID3 & Fallback**: Lectura de metadatos ID3v2 de título, artista, BPM (`TBPM`) y clave (`TKEY`), con estimador automático para archivos no etiquetados.
- **Modo Demo Precargado**: Biblioteca electrónica precargada para probar la aplicación inmediatamente sin archivos locales.

---

## 🛠️ Tecnologías y Requisitos

- **Lenguaje**: Kotlin 1.9
- **UI Framework**: Jetpack Compose + Material 3 (DJ Booth Dark Theme)
- **SDK Mínimo**: Android 10 (API 29+)
- **SDK Objetivo**: Android 14 (API 34)
- **Orientación**: Landscape (Horizontal) forzado
- **Gradle**: 8.2

---

## 📂 Estructura del Proyecto

```
DJBoothAssistant/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew.bat
├── app/
│   ├── build.gradle.kts
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           └── java/com/djbooth/assistant/
│               ├── MainActivity.kt
│               ├── data/
│               │   ├── model/ (Track, SetIntent, RecommendationResult)
│               │   └── scanner/ (MediaMetadataScanner, DemoLibraryProvider)
│               ├── domain/
│               │   ├── CamelotEngine.kt
│               │   ├── RecommendationEngine.kt
│               │   └── MixPointCalculator.kt
│               └── ui/
│                   ├── theme/ (Color, Type, Theme)
│                   ├── components/ (TopEnergyPanel, LeftNowPlayingPanel, RightRecommendationsPanel, BottomLibraryBar, CamelotKeyBadge)
│                   ├── viewmodel/ (DJDeckViewModel)
│                   └── screen/ (DJMainScreen)
```

---

## ⚙️ Instrucciones de Compilación y Ejecución

1. Abrir **Android Studio** (Iguana, Jellyfish o posterior).
2. Seleccionar `Open...` y elegir la carpeta `DJBoothAssistant`.
3. Sincronizar Gradle (`Sync Project with Gradle Files`).
4. Conectar una tablet Android (o lanzar un emulador de Tablet en modo Landscape, ej: Pixel Tablet).
5. Ejecutar mediante `Run 'app'` (Shift + F10) o compilar el APK usando:
   ```powershell
   .\gradlew.bat assembleDebug
   ```
   El archivo APK compilado se generará en `app/build/outputs/apk/debug/app-debug.apk`.
