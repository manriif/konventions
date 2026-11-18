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
package konventions

import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.HasConfigurableKotlinCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl

private const val ANDROID_NATIVE_GROUP = "androidNative"
private const val APPLE_GROUP = "apple"
private const val CONCURRENT_GROUP = "concurrent"
private const val JAVA_GROUP = "java"
private const val LINUX_GROUP = "linux"
private const val NATIVE_GROUP = "native"
private const val NON_ANDROID_GROUP = "nonAndroid"
private const val NON_CONCURRENT_GROUP = "nonConcurrent"
private const val NON_JAVA_GROUP = "nonJava"
private const val NON_WASI_GROUP = "nonWasi"
private const val NON_WEB_GROUP = "nonWeb"
private const val UNIX_GROUP = "unix"
private const val WEB_GROUP = "web"

///////////////////////////////////////////////////////////////////////////
// Compilation
///////////////////////////////////////////////////////////////////////////

/**
 * Configures the Kotlin compilation that the multiplatform and the JVM modules share.
 */
internal fun <E> E.configureKotlin()
        where E : KotlinProjectExtension,
              E : HasConfigurableKotlinCompilerOptions<*> {

    explicitApi()

    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_4
        apiVersion = KotlinVersion.KOTLIN_2_4
        allWarningsAsErrors = true

        freeCompilerArgs.run {
            add("-Xreturn-value-checker=full")
            add("-Xcontext-sensitive-resolution")
            add("-Xexpect-actual-classes")
        }
    }

    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(project.catalogVersion("jvm-toolchain"))
    }
}

///////////////////////////////////////////////////////////////////////////
// Hierarchy
///////////////////////////////////////////////////////////////////////////

/**
 * Applies the default hierarchy of Kotlin, with the groups of source sets that projects share.
 */
@OptIn(ExperimentalKotlinGradlePluginApi::class)
internal fun KotlinMultiplatformExtension.applyHierarchy() {
    applyDefaultHierarchyTemplate {
        common {
            group(CONCURRENT_GROUP) {
                group(JAVA_GROUP) {
                    withJvm()
                    withCompilations { it.target.platformType == KotlinPlatformType.androidJvm }
                }

                group(NATIVE_GROUP)
            }

            group(NON_CONCURRENT_GROUP) {
                group(WEB_GROUP)
                withWasmWasi()
            }

            group(NON_ANDROID_GROUP) {
                withJvm()
                group(NATIVE_GROUP)
                group(WEB_GROUP)
                withWasmWasi()
            }

            group(NON_JAVA_GROUP) {
                group(NATIVE_GROUP)
                group(WEB_GROUP)
                withWasmWasi()
            }

            group(NON_WASI_GROUP) {
                group(JAVA_GROUP)
                group(NATIVE_GROUP)
                group(WEB_GROUP)
            }

            group(NON_WEB_GROUP) {
                group(JAVA_GROUP)
                group(NATIVE_GROUP)
                withWasmWasi()
            }

            group(NATIVE_GROUP) {
                group(UNIX_GROUP) {
                    group(ANDROID_NATIVE_GROUP)
                    group(APPLE_GROUP)
                    group(LINUX_GROUP)
                }
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////
// Targets
///////////////////////////////////////////////////////////////////////////

/**
 * Makes `this` web target run in a browser, in Node.js, or in both.
 */
internal fun KotlinJsTargetDsl.environments(browser: Boolean, node: Boolean) {
    if (browser) {
        browser()
    }

    if (node) {
        nodejs()
    }
}
