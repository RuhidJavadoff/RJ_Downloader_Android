plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "com.ruhidjavadoff.rjdownloader"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ruhidjavadoff.rjdownloader"
        minSdk = 22
        targetSdk = 35
        versionCode = 1
        versionName = "Beta 1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") // Ən son stabil versiyanı yoxlayın
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3") // Ən son stabil versiyanı yoxlayın

    // Lifecycle-aware coroutine scopes (məsələn, lifecycleScope)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0") // Ən son stabil versiyanı yoxlayın
    implementation("com.github.TeamNewPipe:NewPipeExtractor:v0.24.6") // Konkret versiya ilə yoxlayın
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // OkHttp versiyası da stabil olmalıdır
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs_nio:2.0.4")
    implementation("com.google.code.gson:gson:2.10.1") // Ən son versiyanı yoxlayın
}