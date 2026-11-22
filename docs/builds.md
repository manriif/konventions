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
catalog of the folder above. As an included build, it also gets the Gradle Plugin Portal, last, for
the plugins that its build logic depends on.

```kotlin
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("io.github.manriif.konventions") version "0.0.4"
}

rootProject.name = "build-logic"
```

A convention of the project reads the `konventions` block of the settings through the
`KonventionsOfBuild` extension of its project.

## Included builds

An included build takes the `konventions` block of the build that includes it, unless it declares
its own. A build with no catalog takes the one of the folder above. This is how the builds of one
repository share their settings and their catalog.

The API reference of the root can gather the modules of a build that the settings include outside
`pluginManagement`. The root build script names them by their coordinates in the catalog, like
`dokka(libs.example.annotations)`, and Gradle substitutes the projects of the included build. An
included build writes no `docs/api` of its own.
