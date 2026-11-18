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
import org.gradle.api.provider.SetProperty

/**
 * Describes a module in its build script, in the `local` block.
 */
public interface LocalExtension {

    /**
     * The display name of the module. The POM and the API reference show it.
     */
    public val name: Property<String>

    /**
     * The description of the module. The POM shows it.
     */
    public val description: Property<String>

    /**
     * The namespace of the Android target, a package name like `example.core`.
     */
    public val namespace: Property<String>

    /**
     * The tags of the Gradle plugins of the module on the Gradle Plugin Portal.
     */
    public val tags: SetProperty<String>
}
