/**
 * Copyright (C) 2026 Maanrifa Bacar Ali
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import konventions.applyHierarchy
import konventions.configureKotlin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

plugins {
    id("io.github.manriif.konventions.local")
}

pluginManager.apply("org.jetbrains.kotlin.multiplatform")

extensions.configure<KotlinMultiplatformExtension> {
    configureKotlin()

    if (providers.gradleProperty("kotlin.mpp.applyDefaultHierarchyTemplate").orNull != "false") {
        applyHierarchy()
    }

    sourceSets.commonTest.dependencies {
        implementation(kotlin("test"))
    }
}

// The stdlib gives two libraries named `kotlin-stdlib-common` to a native source set below one
// that native shares with the web only, like `nonJava`, and the compiler warns about it. Each
// target still compiles that code with every warning as an error.
tasks.withType<KotlinNativeCompile>().configureEach {
    if (name.endsWith("KotlinMetadata")) {
        compilerOptions.allWarningsAsErrors = false
    }
}