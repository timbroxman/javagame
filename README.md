# Space Defender - LibGDX проект

## Что нужно установить

### Обязательно для всех платформ:

1. **Java Development Kit (JDK) 8 или выше**
   - Скачать можно с [Oracle](https://www.oracle.com/java/technologies/downloads/) или [OpenJDK](https://adoptium.net/)
   - Проверить установку: `java -version` (должна быть версия 8+)
   - Проверить компилятор: `javac -version`

2. **Gradle** (опционально, так как проект использует Gradle Wrapper)
   - Проект уже содержит Gradle Wrapper (gradlew/gradlew.bat)
   - Если хотите установить Gradle отдельно: [Gradle Downloads](https://gradle.org/releases/)
   - Версия проекта: Gradle 8.5

### Для запуска Desktop версии:

Достаточно только JDK 8+. Gradle Wrapper скачает все зависимости автоматически.

### Для запуска Android версии (дополнительно):

1. **Android SDK**
   - Установить через [Android Studio](https://developer.android.com/studio) (рекомендуется)
   - Или установить только [Android SDK Command Line Tools](https://developer.android.com/studio#command-tools)
   - Требуется Android SDK 34 (compileSdkVersion 34)
   - Требуется Build Tools 34.0.0

2. **Переменная окружения ANDROID_HOME**
   - Установить путь к Android SDK
   - Windows: `setx ANDROID_HOME "C:\Users\ВашеИмя\AppData\Local\Android\Sdk"`
   - Или создать файл `local.properties` в корне проекта:
     ```
     sdk.dir=C:\\Users\\ВашеИмя\\AppData\\Local\\Android\\Sdk
     ```

## Как запустить проект

### Desktop версия (Windows):

```bash
# Сборка проекта
gradlew.bat desktop:build

# Запуск проекта
gradlew.bat desktop:run
```

### Android версия:

```bash
# Сборка APK
gradlew.bat android:assembleDebug

# Установка на подключенное устройство
gradlew.bat android:installDebug

# Запуск на устройстве
gradlew.bat android:run
```

## Структура проекта

- `core/` - основная логика игры (общая для всех платформ)
- `desktop/` - версия для Windows/Linux/macOS
- `android/` - версия для Android
- `assets/` - ресурсы игры (изображения, звуки и т.д.)

## Зависимости

Проект использует LibGDX версии 1.12.1 со следующими модулями:
- gdx-core - основной фреймворк
- gdx-box2d - физический движок Box2D
- gdx-freetype - поддержка шрифтов

Все зависимости будут автоматически загружены при первой сборке проекта.






