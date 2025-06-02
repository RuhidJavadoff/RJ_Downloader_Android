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
    }
}
// settings.gradle.kts
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // Bu sətir sizdə fərqli ola bilər
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // ƏSAS BUDUR
    }
}

rootProject.name = "RJ Downloader - Music & Video"
include(":app")
