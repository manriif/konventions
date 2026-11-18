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
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import konventions.catalogVersion
import konventions.environments
import konventions.local
import org.gradle.kotlin.dsl.getByName
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinWasmJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinWasmWasiTargetDsl
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

///////////////////////////////////////////////////////////////////////////
// Groups
///////////////////////////////////////////////////////////////////////////

/**
 * Adds every supported target to `this` [KotlinMultiplatformExtension] and returns them.
 *
 * The module must apply the `com.android.kotlin.multiplatform.library` plugin. Each flag leaves
 * targets out, for a module with a dependency that does not support them.
 *
 * @param androidNative Whether to add the Android Native targets.
 * @param browser Whether JS and Wasm JS run in a browser.
 * @param linuxArm Whether to add the `linuxArm64` target.
 * @param node Whether JS and Wasm JS run in Node.js.
 * @param wasmWasi Whether to add the Wasm WASI target.
 * @param watchosArm32 Whether to add the `watchosArm32` target.
 * @param watchosDeviceArm64 Whether to add the `watchosDeviceArm64` target.
 * @param windows Whether to add the Windows targets.
 */
public fun KotlinMultiplatformExtension.allTargets(
    androidNative: Boolean = true,
    browser: Boolean = true,
    linuxArm: Boolean = true,
    node: Boolean = true,
    wasmWasi: Boolean = true,
    watchosArm32: Boolean = true,
    watchosDeviceArm64: Boolean = true,
    windows: Boolean = true,
): List<KotlinTarget> = buildList {
    addAll(androidJvmTargets())
    addAll(jvmTargets())

    val native = nativeTargets(
        androidNative = androidNative,
        linuxArm = linuxArm,
        watchosArm32 = watchosArm32,
        watchosDeviceArm64 = watchosDeviceArm64,
        windows = windows,
    )

    addAll(native)
    addAll(webTargets(browser = browser, node = node))

    if (wasmWasi) {
        addAll(wasmWasiTargets())
    }
}

/**
 * Adds the Native targets to `this` [KotlinMultiplatformExtension] and returns them.
 *
 * @param androidNative Whether to add the Android Native targets.
 * @param linuxArm Whether to add the `linuxArm64` target.
 * @param watchosArm32 Whether to add the `watchosArm32` target.
 * @param watchosDeviceArm64 Whether to add the `watchosDeviceArm64` target.
 * @param windows Whether to add the Windows targets.
 */
public fun KotlinMultiplatformExtension.nativeTargets(
    androidNative: Boolean = true,
    linuxArm: Boolean = true,
    watchosArm32: Boolean = true,
    watchosDeviceArm64: Boolean = true,
    windows: Boolean = true,
): List<KotlinNativeTarget> = buildList {
    if (androidNative) {
        addAll(androidNativeTargets())
    }

    addAll(appleTargets(watchosArm32 = watchosArm32, watchosDeviceArm64 = watchosDeviceArm64))
    addAll(linuxTargets(arm = linuxArm))

    if (windows) {
        addAll(windowsTargets())
    }
}

/**
 * Adds the JS and Wasm JS targets to `this` [KotlinMultiplatformExtension] and returns them.
 *
 * Wasm WASI is not one of them, since many libraries do not support it. [wasmWasiTargets] adds it.
 *
 * @param browser Whether the targets run in a browser.
 * @param node Whether the targets run in Node.js.
 */
public fun KotlinMultiplatformExtension.webTargets(
    browser: Boolean = true,
    node: Boolean = true,
): List<KotlinJsTargetDsl> = buildList {
    addAll(jsTargets(browser = browser, node = node))
    addAll(wasmJsTargets(browser = browser, node = node))
}

///////////////////////////////////////////////////////////////////////////
// Families
///////////////////////////////////////////////////////////////////////////

/**
 * Adds the Android JVM target to `this` [KotlinMultiplatformExtension] and returns it in a list.
 *
 * The module must apply the `com.android.kotlin.multiplatform.library` plugin and set
 * `local.namespace`. The catalog gives the SDK versions, as `android-sdk-compile` and
 * `android-sdk-min`.
 */
public fun KotlinMultiplatformExtension.androidJvmTargets(): List<KotlinMultiplatformAndroidLibraryTarget> {
    val android = extensions.getByName<KotlinMultiplatformAndroidLibraryTarget>("android").apply {
        namespace = project.local.namespace.get()

        compileSdk {
            version = release(project.catalogVersion("android-sdk-compile").toInt())
        }

        minSdk {
            version = release(project.catalogVersion("android-sdk-min").toInt())
        }

        withHostTest {}
    }

    return listOf(android)
}

/**
 * Adds the Android Native targets to `this` [KotlinMultiplatformExtension] and returns them.
 */
public fun KotlinMultiplatformExtension.androidNativeTargets(): List<KotlinNativeTarget> = listOf(
    androidNativeArm32(),
    androidNativeArm64(),
    androidNativeX86(),
    androidNativeX64(),
)

/**
 * Adds the Apple targets to `this` [KotlinMultiplatformExtension] and returns them.
 *
 * @param watchosArm32 Whether to add the `watchosArm32` target.
 * @param watchosDeviceArm64 Whether to add the `watchosDeviceArm64` target.
 */
@Suppress("DEPRECATION")
public fun KotlinMultiplatformExtension.appleTargets(
    watchosArm32: Boolean = true,
    watchosDeviceArm64: Boolean = true,
): List<KotlinNativeTarget> = buildList {
    add(macosX64())
    add(macosArm64())

    add(iosX64())
    add(iosArm64())
    add(iosSimulatorArm64())

    add(tvosX64())
    add(tvosArm64())
    add(tvosSimulatorArm64())

    add(watchosX64())
    add(watchosArm64())
    add(watchosSimulatorArm64())

    if (watchosArm32) {
        add(watchosArm32())
    }

    if (watchosDeviceArm64) {
        add(watchosDeviceArm64())
    }
}

/**
 * Adds the JS target to `this` [KotlinMultiplatformExtension] and returns it in a list.
 *
 * @param browser Whether the target runs in a browser.
 * @param node Whether the target runs in Node.js.
 */
public fun KotlinMultiplatformExtension.jsTargets(
    browser: Boolean = true,
    node: Boolean = true,
): List<KotlinJsTargetDsl> = listOf(
    js {
        environments(browser = browser, node = node)
    }
)

/**
 * Adds the JVM target to `this` [KotlinMultiplatformExtension] and returns it in a list.
 */
public fun KotlinMultiplatformExtension.jvmTargets(): List<KotlinJvmTarget> = listOf(jvm())

/**
 * Adds the Linux targets to `this` [KotlinMultiplatformExtension] and returns them.
 *
 * @param arm Whether to add the `linuxArm64` target.
 */
public fun KotlinMultiplatformExtension.linuxTargets(
    arm: Boolean = true,
): List<KotlinNativeTarget> = buildList {
    add(linuxX64())

    if (arm) {
        add(linuxArm64())
    }
}

/**
 * Adds the Wasm JS target to `this` [KotlinMultiplatformExtension] and returns it in a list.
 *
 * @param browser Whether the target runs in a browser.
 * @param node Whether the target runs in Node.js.
 */
@OptIn(ExperimentalWasmDsl::class)
public fun KotlinMultiplatformExtension.wasmJsTargets(
    browser: Boolean = true,
    node: Boolean = true,
): List<KotlinWasmJsTargetDsl> = listOf(
    wasmJs {
        environments(browser = browser, node = node)
    }
)

/**
 * Adds the Wasm WASI target to `this` [KotlinMultiplatformExtension] and returns it in a list.
 *
 * The target runs in Node.js.
 */
@OptIn(ExperimentalWasmDsl::class)
public fun KotlinMultiplatformExtension.wasmWasiTargets(): List<KotlinWasmWasiTargetDsl> = listOf(
    wasmWasi {
        nodejs()
    }
)

/**
 * Adds the Windows targets to `this` [KotlinMultiplatformExtension] and returns them.
 */
public fun KotlinMultiplatformExtension.windowsTargets(): List<KotlinNativeTargetWithHostTests> =
    listOf(mingwX64())
