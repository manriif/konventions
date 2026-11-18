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
import konventions.DEVELOPER_NAME
import konventions.local
import konventions.repositoryUrl
import konventions.shared
import org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet.Companion.COMMON_MAIN_SOURCE_SET_NAME

plugins {
    id("io.github.manriif.konventions.local")
    org.jetbrains.dokka
}

dokka {
    dokkaPublications.html {
        moduleName = local.name
        failOnWarning = true

        if (projectDir == rootDir) {
            outputDirectory = layout.projectDirectory.dir("docs/api").dir(moduleVersion)
        }
    }

    dokkaSourceSets.configureEach {
        val readme = projectDir.resolve("README.md")
        val main = name == COMMON_MAIN_SOURCE_SET_NAME || name == MAIN_SOURCE_SET_NAME

        if (main && readme.exists()) {
            includes.from(readme)
        }

        val moduleDir = projectDir.relativeTo(rootDir).invariantSeparatorsPath

        sourceLink {
            localDirectory.set(projectDir.resolve("src"))
            remoteUrl(repositoryUrl.map { "$it/tree/main/$moduleDir/src" })
            remoteLineSuffix.set("#L")
        }
    }

    pluginsConfiguration.html {
        footerMessage = shared.inceptionYear.map { "© $it $DEVELOPER_NAME" }
    }
}
