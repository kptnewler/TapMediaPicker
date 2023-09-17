plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("info.hellovass.h-publish-plugin")
}

android {
    compileSdk = 33
    namespace ="com.yalantis.ucrop"
    buildToolsVersion = "28.0.3"

    defaultConfig {
        minSdk = 24
        targetSdk = 33
//        version = 1
//        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    lintOptions {
        isAbortOnError = false
    }

    resourcePrefix("ucrop_")
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.exifinterface:exifinterface:1.3.3")
    implementation("androidx.transition:transition:1.4.1")
    implementation("com.squareup.okhttp3:okhttp:3.12.13")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
}
