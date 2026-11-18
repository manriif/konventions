# Builds

How Konventions works with the build logic of a project and with included builds.

## Project-specific build logic

A project keeps what only it needs in its own `build-logic` build. Its modules apply those
conventions next to the ones of Konventions.

```kotlin
pluginManagement {
    includeBuild("build-logic")
}
```

The settings plugin can be applied to `build-logic` too. It then gets the repositories, and the
catalog of the folder above.

```kotlin
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("io.github.manriif.konventions") version "0.0.2"
}

rootProject.name = "build-logic"
```

A convention of the project reads the `konventions` block of the settings through the
`KonventionsOfBuild` extension of its project.

Its Kotlin files without a package must not be named like the ones of Konventions, `Targets.kt`
and `Projects.kt`. The JVM loads one class per name, so the functions of the other file go
missing.

## Included builds

An included build takes the `konventions` block of the build that includes it, unless it declares
its own. A build with no catalog takes the one of the folder above. This is how the builds of one
repository share their settings and their catalog.
