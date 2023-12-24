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
        google()
        mavenCentral()
    }
}

rootProject.name = "SocialMediaApplication"
include(":app")
include(":libNavAnnotation")
include(":libNavCompiler")
include(":libNetwork")
include(":libCommon")
