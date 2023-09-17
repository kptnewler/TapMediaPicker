// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.0.2" apply false
    id("com.android.library") version "7.0.2" apply  false
    id("org.jetbrains.kotlin.android") version "1.6.21" apply false
    id("info.hellovass.h-publish-plugin") version "1.2.0-alpha.1" apply false
}

subprojects {
    this.group = rootProject.group
    this.version = rootProject.version
}

group = "com.taptap.intl"
version = "1.0.0-alpha.1"


