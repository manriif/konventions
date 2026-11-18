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

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

///////////////////////////////////////////////////////////////////////////
// Developer
///////////////////////////////////////////////////////////////////////////

/**
 * The GitHub account of the developer. It names the group and the URLs of every project.
 */
internal const val DEVELOPER_ID = "manriif"

/**
 * The name of the developer, in the POMs and in the footer of the API reference.
 */
internal const val DEVELOPER_NAME = "Maanrifa Bacar Ali"

private const val GITHUB_PATH = "github.com/$DEVELOPER_ID"

/**
 * The GitHub page of the developer.
 */
internal const val DEVELOPER_URL = "https://$GITHUB_PATH"

///////////////////////////////////////////////////////////////////////////
// Project
///////////////////////////////////////////////////////////////////////////

/**
 * What the `konventions` block of the settings says about the project.
 */
internal val Project.shared: KonventionsOfBuild
    get() = extensions.getByType()

/**
 * The `local` block of the module. Gradle generates no accessor for it inside the conventions.
 */
internal val Project.local: LocalExtension
    get() = extensions.getByType()

/**
 * The repository of the project on GitHub, like `github.com/manriif/konventions`.
 */
internal val Project.repositoryPath: Provider<String>
    get() = shared.repository.map { "$GITHUB_PATH/$it" }

/**
 * The URL of the repository of the project on GitHub.
 */
internal val Project.repositoryUrl: Provider<String>
    get() = shared.repository.map { "$DEVELOPER_URL/$it" }

/**
 * Reads the version named [alias] in the `libs` catalog of the build.
 *
 * The conventions are compiled without that catalog, so its typed accessors are out of reach.
 *
 * @throws GradleException When the catalog has no version named [alias].
 */
internal fun Project.catalogVersion(alias: String): String {
    val version = extensions.getByType<VersionCatalogsExtension>().named("libs").findVersion(alias)

    return version
        .orElseThrow { GradleException("The catalog has no version named `$alias`") }
        .requiredVersion
}
