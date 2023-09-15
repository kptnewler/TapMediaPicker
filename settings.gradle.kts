pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
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
include(":selector")
