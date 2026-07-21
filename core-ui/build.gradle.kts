plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    // AGP 9 has Kotlin support built in — applying kotlin.android is rejected as legacy (Kotlin 2.4).
    // :core-ui has no Kotlin-specific JVM target override; JVM 11 from java compile options is enough.
}

android {
    namespace = "com.solstice.prayers.core.ui"
    compileSdk { version = release(37) }

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
