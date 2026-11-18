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

import org.gradle.api.provider.Property

/**
 * Describes the project in the settings script of its build, in the `konventions` block.
 *
 * An included build takes these values from the build that includes it, unless it sets its own.
 */
public interface KonventionsExtension {

    /**
     * The name of the GitHub repository of the project.
     *
     * The modules take their group from it, as `io.github.manriif.<repository>`. Their version is
     * the one that the catalog names after the repository.
     */
    public val repository: Property<String>

    /**
     * The year the project started. The POM and the footer of the API reference show it.
     */
    public val inceptionYear: Property<Int>

    /**
     * The license of the project.
     */
    public val license: Property<License>

    /**
     * The [License.Mit] license, so that a settings script names it without an import.
     */
    public val Mit: License
        get() = License.Mit

    /**
     * The [License.Apache2] license, so that a settings script names it without an import.
     */
    public val Apache2: License
        get() = License.Apache2
}

/**
 * A license under which a project is published.
 *
 * @property title The name of the license in the POM.
 */
public enum class License(public val title: String) {

    /**
     * The MIT License.
     */
    Mit("MIT License"),

    /**
     * The Apache License 2.0.
     */
    Apache2("Apache 2.0 License"),
}
