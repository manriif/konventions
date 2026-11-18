# Gradle plugins

A module that publishes Gradle plugins to the Gradle Plugin Portal applies the Plugin Publish
plugin, 2.2.1 or later, next to the conventions.

```kotlin
plugins {
    `kotlin-dsl`
    alias(libs.plugins.gradle.pluginPublish)
    alias(konventionsLibs.plugins.common)
    alias(konventionsLibs.plugins.jvm)
}

local {
    name = "Example"
    description = "An example Gradle plugin."
    tags = setOf("example")
}
```

The `publish` convention then fills the details that the Portal asks for:

- The website and the VCS URL are the GitHub repository.
- Each plugin takes its display name, its description and its tags from `local`, unless it sets
  its own.
- Each plugin declares that it supports the configuration cache and Isolated Projects.

In a build with several projects, the root declares the Plugin Publish plugin with `apply false`,
like the Kotlin plugin. The conventions use its classes without bringing it.

`./gradlew publishPlugins` publishes the plugins to the Portal, next to `publishToMavenCentral`.

A module built with `kotlin-dsl` keeps the language and API version that `kotlin-dsl` sets, the
one of the Kotlin that Gradle runs.
