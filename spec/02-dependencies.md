# 02 — Dependências e Configuração de Build

Configuração completa do Gradle para o projeto PeitoInfinity.

## gradle/libs.versions.toml

O agente codificador deve substituir o conteúdo atual por:

```toml
[versions]
agp = "9.3.0"
kotlin = "2.2.10"
ksp = "2.2.10-1.0.31"
composeBom = "2026.02.01"
coreKtx = "1.16.0"
lifecycleRuntimeKtx = "2.9.1"
activityCompose = "1.10.1"
navigationCompose = "2.9.8"
room3 = "3.0.0"
hilt = "2.56.2"
hiltNavigationCompose = "1.2.0"
datastorePreferences = "1.1.7"
kotlinxSerializationJson = "1.8.1"
kotlinxCoroutines = "1.10.2"
litertlm = "0.13.1"
genaiKotlin = "0.1.0"
junit = "4.13.2"
junitVersion = "1.2.1"
espressoCore = "3.6.1"

[libraries]
# Core
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycleRuntimeKtx" }

# Compose
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
androidx-compose-animation = { group = "androidx.compose.animation", name = "animation" }

# Navigation
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }

# Room 3
androidx-room3-runtime = { group = "androidx.room3", name = "room3-runtime", version.ref = "room3" }
androidx-room3-compiler = { group = "androidx.room3", name = "room3-compiler", version.ref = "room3" }
androidx-room3-ktx = { group = "androidx.room3", name = "room3-ktx", version.ref = "room3" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-android-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }

# DataStore
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastorePreferences" }

# Serialization
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerializationJson" }

# Coroutines
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "kotlinxCoroutines" }

# AI - LiteRT-LM (on-device)
litertlm-android = { group = "com.google.ai.edge.litertlm", name = "litertlm-android", version.ref = "litertlm" }

# AI - Gemini API (remote)
google-genai-kotlin = { group = "com.google.genai", name = "google-genai-kotlin", version.ref = "genaiKotlin" }

# Testing
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt-android = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
room3 = { id = "androidx.room3", version.ref = "room3" }
```

## build.gradle.kts (raiz do projeto)

```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.room3) apply false
}
```

## app/build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.room3)
}

android {
    namespace = "com.example.peitoinfinity"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.peitoinfinity"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Chave API do Gemini embutida via BuildConfig
        // O agente codificador deve configurar isso via local.properties ou secrets.properties
        buildConfigField("String", "GEMINI_API_KEY", "\"YOUR_GEMINI_API_KEY_HERE\"")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// Configuração Room 3 — export de schemas
room3 {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.animation)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Room 3
    implementation(libs.androidx.room3.runtime)
    implementation(libs.androidx.room3.ktx)
    ksp(libs.androidx.room3.compiler)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // AI - LiteRT-LM (on-device)
    implementation(libs.litertlm.android)

    // AI - Gemini API (remote)
    implementation(libs.google.genai.kotlin)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
```

## AndroidManifest.xml — Permissões necessárias

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- Necessário para API Gemini remota -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:name=".PeitoInfinityApplication"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.PeitoInfinity">
        <activity
            android:name=".presentation.MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.PeitoInfinity"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

## Configuração da Chave API Gemini

A chave API do Gemini **não deve ser exposta ao usuário**. Deve ser embutida via `BuildConfig`:

### Opção 1: via `local.properties` (recomendada para desenvolvimento)

```properties
# local.properties (NÃO commitado no git)
GEMINI_API_KEY=AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXX
```

E no `build.gradle.kts`:
```kotlin
val localProperties = java.util.Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    defaultConfig {
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${localProperties.getProperty("GEMINI_API_KEY", "")}\""
        )
    }
}
```

### Opção 2: via `secrets.properties` (para produção)

Usar o Secrets Gradle Plugin do Google.

## Notas para o Agente Codificador

1. **IMPORTANTE**: Room 3.0.0 usa namespace `androidx.room3`, NÃO `androidx.room`. Todos os imports devem usar `androidx.room3.*`.
2. KSP é obrigatório — NÃO usar KAPT em nenhum momento.
3. Hilt também usa KSP (não KAPT) nesta versão.
4. A chave API Gemini deve ser carregada de `local.properties` via BuildConfig.
5. Verificar se as versões no `libs.versions.toml` são compatíveis com Kotlin 2.2.10 antes de compilar.
6. Se alguma dependência não resolver, consultar o Maven Repository para a versão mais recente compatível.
7. O plugin `room3` precisa ser aplicado para que o export de schemas funcione.
