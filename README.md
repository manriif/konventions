# Konventions

![Stability](https://img.shields.io/badge/stability-experimental-orange.svg)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.manriif.konventions/konventions-settings?label=Maven%20Central&logo=apache-maven&color=teal)](https://central.sonatype.com/artifact/io.github.manriif.konventions/konventions-settings)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4%2B-purple.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org)

The Gradle conventions of the Kotlin libraries of [manriif](https://github.com/manriif):
repositories, compilation, targets, source sets, API reference and publication.

```kotlin
plugins {
    alias(libs.plugins.android.multiplatformLibrary)
    alias(konventionsLibs.plugins.common)
    alias(konventionsLibs.plugins.kmp)
}

local {
    name = "Example"
    description = "An example library."
    namespace = "example"
}

kotlin {
    allTargets()
}
```

> [!NOTE]
> Konventions is personal. It names manriif as the developer of every POM, and uses his GitHub
> account in every group and URL. It is not meant for other developers.

## Table of contents

- [Why Konventions](#why-konventions)
- [Conventions](#conventions)
- [Requirements](#requirements)
- [Getting started](#getting-started)
- [Usage](#usage)
- [Things to know](#things-to-know)
- [Project state](#project-state)
- [Modules](#modules)
- [Documentation](#documentation)
- [License](#license)

## Why Konventions

Each project of manriif used to copy the same `build-logic` folder, and the copies drifted apart.
They also read their project information from `gradle.properties`, which an included build does
not inherit. Konventions keeps the shared build logic in one place. Each project only describes
itself, in its settings and in its modules.

## Conventions

| Plugin                                   | What it does                                                                                                                                                                                                  |
|------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `io.github.manriif.konventions`          | The settings plugin. Declares the repositories, with the Node.js, Yarn and V8 distributions that the Kotlin plugin downloads. Applies the Java toolchain resolver, and turns on the type-safe project accessors. Gives the `konventionsLibs` catalog of the conventions, and the `includeProject` function. |
| `io.github.manriif.konventions.local`    | Adds the `local` block, where a module gives its name, its description, the namespace of its Android target and the tags of its Gradle plugins. Sets the group and the version of the module.             |
| `io.github.manriif.konventions.kmp`      | Applies the Kotlin Multiplatform plugin and `local`. Turns on explicit API mode, all warnings as errors, `-Xreturn-value-checker=full`, `-Xcontext-sensitive-resolution` and `-Xexpect-actual-classes`. Sets Kotlin 2.4 as the language and API version, sets the Java toolchain, adds groups of source sets, and adds `kotlin-test` to `commonTest`. |
| `io.github.manriif.konventions.jvm`      | Applies the Kotlin JVM plugin, with the compiler options and the Java toolchain of `kmp`. Adds `kotlin-test` to the tests, and runs them on the JUnit Platform.                                                                                    |
| `io.github.manriif.konventions.publish`  | Publishes to Maven Central with the vanniktech plugin, signs the publications and fills the POM.                                                                                                             |
| `io.github.manriif.konventions.dokka`    | Configures the API reference, with the `README.md` of the module as its first page. In the root project, writes it to `docs/api/<version>`, except in an included build. |
| `io.github.manriif.konventions.internal` | Applies `dokka` and `publish`, for a published module that only the other modules of the project use. Records no API. |
| `io.github.manriif.konventions.common`   | Applies `internal`. With `kmp` or `jvm`, checks the public API against the dump committed in the module. |

A public module applies `common` next to `kmp` or `jvm`. A published module that only the other
modules of the project use applies `internal` instead, since its public declarations are not the
API of the library. A module with tests only applies `kmp` or `jvm` alone. It is not published
and records no API.

## Requirements

- Kotlin 2.4 or later.
- Gradle 9.9 or later.
- The Android Gradle plugin 9 or later, for a module with the Android target.

Konventions does not bring the Kotlin and Android plugins. Each build applies its own, at the
versions it chooses.

## Getting started

### Settings

Konventions is on Maven Central. The Gradle Plugin Portal stays for the toolchain resolver, which
the settings plugin depends on.

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

rootProject.name = "example-project"

konventions {
    repository = "example"
    inceptionYear = 2026
    license = Mit
}

includeProject("example")
```

`repository` is the name of the GitHub repository. The group of the modules is
`io.github.manriif.<repository>`, and the POM and the source links point to the repository.

### Catalog

The conventions read these versions from the `libs` catalog of the build.

| Version               | Use                                                    |
|-----------------------|--------------------------------------------------------|
| `<repository>`        | The version of the modules, like `example = "0.0.1"`.  |
| `jvm-toolchain`       | The Java toolchain of the Kotlin compilations.         |
| `android-sdk-compile` | The compile SDK of the Android target.                 |
| `android-sdk-min`     | The minimum SDK of the Android target.                 |

### Root project

The root build script declares the Kotlin and Android plugins and the conventions that the modules
apply. It also gathers the API reference of the modules.

```kotlin
plugins {
    alias(libs.plugins.android.multiplatformLibrary) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(konventionsLibs.plugins.common) apply false
    alias(konventionsLibs.plugins.dokka)
    alias(konventionsLibs.plugins.kmp) apply false
}

local {
    name = "Example"
}

dependencies {
    dokka(projects.example)
}
```

A build with Kotlin JVM modules only declares the Kotlin JVM plugin instead. A build with a single
project applies the Kotlin plugin in its own `plugins` block, next to the conventions.

### Modules

A module applies its conventions and describes itself in `local`, as in the example at the top of
this page.

## Usage

### Projects

The settings plugin gives `includeProject`, which includes a project from its folders, at any
depth. Unlike `include`, the path takes no leading colon.

```kotlin
includeProject("example")
includeProject("example-extensions", "datetime")
```

The second line includes the project of the `example-extensions/datetime` folder, named
`example-extensions-datetime`. Gradle builds a path from the names, so its path is
`:example-extensions:example-extensions-datetime`. A project named after all its folders keeps a
unique name, and so a unique artifact, when another project ends with the same folder.

### Targets

The target functions extend the `kotlin` block. Each one adds targets and returns them, so that a
module can configure them further.

| Function                 | Targets                                                                                  |
|--------------------------|------------------------------------------------------------------------------------------|
| `allTargets()`           | All of the targets below, Wasm WASI included.                                            |
| `nativeTargets()`        | Android Native, Apple, Linux and Windows.                                                |
| `webTargets()`           | JS and Wasm JS.                                                                          |
| `androidJvmTargets()`    | Android. The module applies `com.android.kotlin.multiplatform.library`.                  |
| `androidNativeTargets()` | `androidNativeArm32`, `androidNativeArm64`, `androidNativeX86` and `androidNativeX64`.   |
| `appleTargets()`         | macOS, iOS, tvOS and watchOS.                                                            |
| `jsTargets()`            | JS.                                                                                      |
| `jvmTargets()`           | JVM.                                                                                     |
| `linuxTargets()`         | `linuxX64` and `linuxArm64`.                                                             |
| `wasmJsTargets()`        | Wasm JS.                                                                                 |
| `wasmWasiTargets()`      | Wasm WASI.                                                                               |
| `windowsTargets()`       | `mingwX64`.                                                                              |

`webTargets()` leaves Wasm WASI out, since many libraries do not support it. Flags leave out the
targets that a dependency does not support.

```kotlin
kotlin {
    allTargets(wasmWasi = false, windows = false)
}
```

JS and Wasm JS run in a browser and in Node.js. The `browser` and `node` flags of `allTargets()`
and `webTargets()` choose one of them. Wasm WASI only runs in Node.js.

### Source sets

The `kmp` convention adds groups to the default hierarchy of Kotlin, which already has `native`,
`apple`, `ios`, `linux`, `web` and the other families. Each group gives a source set like
`concurrentMain` and `concurrentTest`.

| Group           | Targets                              |
|-----------------|--------------------------------------|
| `java`          | JVM and Android.                     |
| `concurrent`    | `java` and Native.                   |
| `nonConcurrent` | JS, Wasm JS and Wasm WASI.           |
| `nonAndroid`    | Every target but Android.            |
| `nonJava`       | Native, JS, Wasm JS and Wasm WASI.   |
| `nonWasi`       | Every target but Wasm WASI.          |
| `nonWeb`        | Every target but JS and Wasm JS.     |
| `unix`          | The Native targets but Windows.      |

A module adds its own groups by extending this hierarchy, `KonventionsHierarchy`. A group like
`native` keeps its targets in the extension. A template of its own would see it empty.

```kotlin
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyHierarchyTemplate(KonventionsHierarchy) {
        common {
            group("nonAndroid") {
                group("jvmNative") {
                    withJvm()
                    group("native")
                }
            }
        }
    }
}
```

A project that wants none of these groups sets `kotlin.mpp.applyDefaultHierarchyTemplate=false` in
its `gradle.properties`, then applies its own template. That property also turns off the default
hierarchy of Kotlin.

### API reference

The `README.md` of a module, when it has one, becomes the first page of its reference. Its first
title gives way to the name of the module. Its relative links and anchors point to the repository
on GitHub, since Dokka reads other links as declarations. The root project gathers the modules it
depends on and writes them to `docs/api/<version>`, one folder per version.

```shell
./gradlew :dokkaGenerate
```

### Compatibility

Two settings keep a release from breaking the code of its users.

- The public API of a public module is recorded in its `api` folder. The build fails when the
  code no longer matches it. After an intended change, run `./gradlew updateKotlinAbi` and commit
  the new dump, so that the change shows in the review.
- The libraries are compiled with Kotlin 2.4 as the language and API version. A library built with
  a newer Kotlin plugin can still be used from Kotlin 2.4.

## Things to know

- The root build script declares the Kotlin and Android plugins with `apply false`. The
  conventions use their classes without bringing them, so the build fails without them.
- The root build script also declares each convention that a module applies, like `internal`,
  with `apply false`. Otherwise Gradle fails with "the plugin is already on the classpath with an
  unknown version".
- An included build that runs alone needs its own `konventions` block. Otherwise it takes the one
  of the build that includes it.
- Progressive mode is off. It fails the build when the language version is older than the compiler.
- A catalog version next to `jvm-toolchain`, like `jvm-toolchain-ffm`, turns
  `libs.versions.jvm.toolchain` into a group. A build script then reads it with `.asProvider()`.
- The type-safe project accessors are always on. A project can't be named like a member of a Gradle
  dependency, such as `group`, `name`, `path` or `version`, or Gradle fails to generate them.
- Kotlin deprecates `macosX64`, `tvosX64` and `watchosX64`. `appleTargets()` will stop working
  with the version of Kotlin that removes them.

## Project state

Konventions is experimental, and its conventions may change before version 1.0.

## Modules

| Module                                         | Description                                          |
|------------------------------------------------|------------------------------------------------------|
| [`konventions-settings`](konventions-settings) | The settings plugin.                                 |
| [`konventions-plugins`](konventions-plugins)   | The project conventions and the target functions.   |

## Documentation

- [Builds](docs/builds.md): the build logic of a project, and included builds.
- [Gradle plugins](docs/gradle-plugins.md): publishing Gradle plugins to the Gradle Plugin Portal.

## License

Konventions is released under the [MIT License](LICENSE).
