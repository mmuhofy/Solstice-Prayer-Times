plugins {
    alias(libs.plugins.android.library)
    // AGP 9 has Kotlin support built in — applying the kotlin.android plugin is intentionally
    // not done here (Kotlin 2.4 + AGP 9 rejects it as legacy). The Compose compiler plugin is
    // still available via alias(libs.plugins.kotlin.compose) when needed for Compose sources.
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.solstice.prayers.core.data"
    compileSdk { version = release(37) }

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.tencent.mmkv)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.batoulapps.adhan)
    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)
}
