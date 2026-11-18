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
@file:Suppress("UnstableApiUsage")

package konventions

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.resolve.RepositoriesMode
import org.gradle.api.provider.Provider
import org.gradle.api.reflect.TypeOf
import org.gradle.kotlin.dsl.add

private const val PLUGIN_GROUP = "io.github.manriif.konventions"
private const val SHARED_EXTENSION = "konventionsOfBuild"
private const val CATALOG = "gradle/libs.versions.toml"
private const val INHERITED_EXTENSION = "konventionsOfIncludingBuild"

private const val REPOSITORY = "repository"
private const val INCEPTION_YEAR = "inceptionYear"
private const val LICENSE = "license"

private val Conventions = listOf("common", "dokka", "jvm", "kmp", "local", "publish")
private val InheritedType = object : TypeOf<Provider<Map<String, String>>>() {}

internal fun Settings.applyKonventions(extension: KonventionsExtension) {
    enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
    inheritFromIncludingBuild(extension)
    declareRepositories()
    importCatalogOfFolderAbove()
    declareConventions()
    shareWithProjects(extension)
}

///////////////////////////////////////////////////////////////////////////
// Included builds
///////////////////////////////////////////////////////////////////////////

// An included build takes what the build that includes it says. Each build loads this plugin on
// its own, so only types of Gradle and of the JDK go from one build to the other.
private fun Settings.inheritFromIncludingBuild(extension: KonventionsExtension) {
    val inherited = gradle.parent?.extensions?.findByType(InheritedType)

    if (inherited != null) {
        extension.repository.convention(inherited.map { it.getValue(REPOSITORY) })
        extension.inceptionYear.convention(inherited.map { it.getValue(INCEPTION_YEAR).toInt() })
        extension.license.convention(inherited.map { License.valueOf(it.getValue(LICENSE)) })
    }

    val shared = providers.provider {
        mapOf(
            REPOSITORY to extension.repository.get(),
            INCEPTION_YEAR to extension.inceptionYear.get().toString(),
            LICENSE to extension.license.get().name,
        )
    }

    gradle.extensions.add(InheritedType, INHERITED_EXTENSION, shared)
}

// A build without a catalog takes the one of the folder above. This is how the builds of a
// composite share one catalog.
private fun Settings.importCatalogOfFolderAbove() {
    if (rootDir.resolve(CATALOG).exists() || !rootDir.resolve("../$CATALOG").exists()) {
        return
    }

    dependencyResolutionManagement.versionCatalogs.create("libs") {
        from(layout.rootDirectory.files("../$CATALOG"))
    }
}

///////////////////////////////////////////////////////////////////////////
// Repositories
///////////////////////////////////////////////////////////////////////////

private fun Settings.declareRepositories() {
    pluginManagement.repositories.run {
        gradlePluginPortal()
        mavenCentral()
        google()
    }

    dependencyResolutionManagement.run {
        repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

        repositories.run {
            mavenCentral()
            google()

            // The Kotlin plugin downloads Node.js, Yarn and V8 from these
            distribution(
                url = "https://nodejs.org/dist",
                pattern = "v[revision]/[artifact](-v[revision]-[classifier]).[ext]",
                group = "org.nodejs",
                module = "node",
            )

            distribution(
                url = "https://github.com/yarnpkg/yarn/releases/download",
                pattern = "v[revision]/[artifact](-v[revision]).[ext]",
                group = "com.yarnpkg",
                module = "yarn",
            )

            distribution(
                url = "https://storage.googleapis.com/chromium-v8/official/canary",
                pattern = "[artifact]-[revision].[ext]",
                group = "google.d8",
                module = "v8",
            )
        }
    }
}

private fun RepositoryHandler.distribution(
    url: String,
    pattern: String,
    group: String,
    module: String,
) {
    ivy {
        setUrl(url)

        patternLayout {
            artifact(pattern)
        }

        metadataSources {
            artifact()
        }

        content {
            includeModule(group, module)
        }
    }
}

///////////////////////////////////////////////////////////////////////////
// Conventions of the projects
///////////////////////////////////////////////////////////////////////////

// Gives the projects a catalog of the conventions, at the version of this plugin. The version
// is unknown when the plugin comes from an included build, which needs none.
private fun Settings.declareConventions() {
    val version = KonventionsExtension::class.java.`package`.implementationVersion ?: return

    dependencyResolutionManagement.versionCatalogs.create("konventionsLibs") {
        Conventions.forEach { plugin(it, "$PLUGIN_GROUP.$it").version(version) }
    }
}

// Gives each project what the settings say. The action only captures values that Gradle can
// isolate, so that it works with Isolated Projects.
private fun Settings.shareWithProjects(extension: KonventionsExtension) {
    val repository = extension.repository
    val inceptionYear = extension.inceptionYear
    val license = extension.license

    gradle.lifecycle.beforeProject {
        val shared = SharedKonventions(repository, inceptionYear, license)
        extensions.add<KonventionsOfBuild>(SHARED_EXTENSION, shared)
    }
}

private class SharedKonventions(
    override val repository: Provider<String>,
    override val inceptionYear: Provider<Int>,
    override val license: Provider<License>,
) : KonventionsOfBuild
