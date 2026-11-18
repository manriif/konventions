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
import konventions.DEVELOPER_ID
import konventions.DEVELOPER_NAME
import konventions.DEVELOPER_URL
import konventions.local
import konventions.repositoryPath
import konventions.repositoryUrl
import konventions.shared
import org.gradle.plugin.compatibility.compatibility
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension

plugins {
    id("io.github.manriif.konventions.local")
    `maven-publish`
    com.vanniktech.maven.publish
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    pom {
        name = local.name
        description = local.description
        url = repositoryUrl
        inceptionYear = shared.inceptionYear.map { it.toString() }

        licenses {
            license {
                name = shared.license.map { it.title }
                url = repositoryUrl.map { "$it/blob/main/LICENSE" }
            }
        }

        developers {
            developer {
                id = DEVELOPER_ID
                name = DEVELOPER_NAME
                url = DEVELOPER_URL
            }
        }

        scm {
            url = repositoryUrl
            connection = repositoryPath.map { "scm:git:git://$it.git" }
            developerConnection = repositoryPath.map { "scm:git:ssh://git@$it.git" }
        }
    }
}

pluginManager.withPlugin("com.gradle.plugin-publish") {
    val gradlePlugin = extensions.getByType<GradlePluginDevelopmentExtension>()

    gradlePlugin.website.convention(repositoryUrl)
    gradlePlugin.vcsUrl.convention(repositoryUrl)

    gradlePlugin.plugins.configureEach {
        tags.convention(local.tags)

        compatibility {
            features {
                configurationCache.convention(true)
                isolatedProjects.convention(true)
            }
        }
    }

    // A declaration takes its name and description as strings, so they wait for the `local` block
    afterEvaluate {
        gradlePlugin.plugins.configureEach {
            displayName = displayName ?: local.name.get()
            description = description ?: local.description.get()
        }
    }
}
