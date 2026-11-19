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

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.nio.file.Path
import kotlin.io.path.invariantSeparatorsPathString

private val Fence = Regex("""^ {0,3}(```|~~~)""")
private val InlineLink = Regex("""(!?)\[((?:[^\[\]]|\[[^\]]*])*)]\(([^)\s]+)""")
private val ReferenceDefinition = Regex("""^( {0,3}\[[^\]]+]:\s*)(\S+)""")
private val Scheme = Regex("""^[A-Za-z][A-Za-z0-9+.-]*:""")

/**
 * Turns the `README.md` of a module into the module page of its API reference.
 *
 * Dokka reads a link that isn't an absolute URL as a link to a declaration. So the relative links
 * and the anchors of the README point to the repository on GitHub. Its first title gives way to
 * the `# Module` header that Dokka expects.
 */
@CacheableTask
internal abstract class ModuleDocumentation : DefaultTask() {

    /**
     * The README of the module.
     */
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val readme: RegularFileProperty

    /**
     * The name of the module in the API reference.
     */
    @get:Input
    abstract val moduleName: Property<String>

    /**
     * The URL of the repository on GitHub.
     */
    @get:Input
    abstract val repositoryUrl: Property<String>

    /**
     * The directory of the module in the repository, empty for the root project.
     */
    @get:Input
    abstract val moduleDirectory: Property<String>

    /**
     * The module page that Dokka includes.
     */
    @get:OutputFile
    abstract val documentation: RegularFileProperty

    @TaskAction
    fun write() {
        val lines = rewrite(readme.get().asFile.readLines())
        documentation.get().asFile.writeText(lines.joinToString("\n", postfix = "\n"))
    }

    private fun rewrite(lines: List<String>): List<String> {
        val body = mutableListOf<String>()
        var fenced = false
        var titled = false

        for (line in lines) {
            when {
                Fence.containsMatchIn(line) -> {
                    fenced = !fenced
                    body += line
                }

                fenced -> body += line
                !titled && line.startsWith("# ") && !line.startsWith("# Package ") -> titled = true
                else -> body += absoluteLinks(line)
            }
        }

        return listOf("# Module ${moduleName.get()}", "") + body
    }

    private fun absoluteLinks(line: String): String {
        val inline = line.replace(InlineLink) { match ->
            val (image, text, destination) = match.destructured
            val target = absolute(destination, image = image.isNotEmpty())
            "$image[${absoluteLinks(text)}]($target"
        }

        return inline.replace(ReferenceDefinition) { match ->
            val (label, destination) = match.destructured
            label + absolute(destination, image = false)
        }
    }

    private fun absolute(destination: String, image: Boolean): String {
        if (Scheme.containsMatchIn(destination)) {
            return destination
        }

        val path = destination.substringBefore('#')
        val fragment = destination.removePrefix(path)

        val file = when {
            path.isEmpty() -> Path.of(moduleDirectory.get(), "README.md")
            path.startsWith("/") -> Path.of(path.removePrefix("/"))
            else -> Path.of(moduleDirectory.get(), path)
        }

        val kind = if (image) "raw" else "blob"
        val location = file.normalize().invariantSeparatorsPathString

        return "${repositoryUrl.get()}/$kind/main/$location$fragment"
    }
}
