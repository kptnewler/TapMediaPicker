pluginManagement {
    repositories {
        maven { url = uri("https://nexus.xmxdev.com/repository/maven-public/") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        maven { url = uri("https://nexus.xmxdev.com/repository/maven-public/") }
        google()
        mavenCentral()
    }
}


rootProject.name = "TapMediaPicker"
include(":app")
//include(":tap-miedia-picker")
include(":ucrop")
include(":tap-selector")
//include(":tap-miedia-picker")
