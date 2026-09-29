pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.fabricmc.net/") }
    }
}

plugins {
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.fabricmc.net/") }
    }
}

rootProject.name = "ZalithLauncher"
include(":ZalithLauncher")
include(":LWJGL")
include(":LayerController")
include(":ColorPicker")
include(":Terracotta")
include(":InputMap")
