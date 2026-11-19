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
@file:JvmName("KonventionsProjects")

import org.gradle.api.initialization.Settings

/**
 * Includes the project in the [folder] of the root directory, or in its [subfolders], and names
 * it after all of these folders.
 *
 * `includeProject("cache", "core")` includes the project of the `cache/core` folder, named
 * `cache-core`. Gradle builds a path from the names, so its path is `:cache:cache-core`. Unlike
 * [include], the folders take no leading colon. The name stays unique when several projects end
 * with the same folder, since it is also the name of the published artifact.
 */
public fun Settings.includeProject(folder: String, vararg subfolders: String) {
    val folders = listOf(folder, *subfolders)
    val projectPath = folders.joinToString(":", prefix = ":")
    include(projectPath)
    project(projectPath).name = folders.joinToString("-")
}
