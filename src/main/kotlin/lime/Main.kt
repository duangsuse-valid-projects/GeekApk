/*
 * Copyright (C) 2018, duangsuse. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package lime

import lime.text.Lexer
import lime.text.Parser
import java.io.File
import java.net.URLClassLoader
import java.util.*

object Main {
  private val scanner = Scanner(System.`in`)
  private val buf = StringBuilder()
  private val homePathOverride: String? = System.getProperty("lime.home")
  private val homePath: String? = System.getProperty("user.home")
  @Suppress("WeakerAccess")
  var interpreter: Lime? = null

  @JvmStatic
  fun main(args: Array<String>) {
    // scan input
    while (scanner.hasNextLine())
      buf.append(scanner.nextLine()).append('\n')

    // prepare
    val lexer = Lexer(buf.toString())
    lexer.lex()
    val parserResult = Parser(lexer.fetch()).parse()

    // initialize interpreter
    interpreter = Lime(parserResult)

    // load command line plugins
    args.forEach {
      loadPlugin(it)
    }

    // eval
    interpreter!!.run()
  }

  @Suppress("WeakerAccess")
  fun loadPlugin(label: String) {
    val pluginPath = "${getPluginDir()}${File.pathSeparator}$label"
    println("Installing plugin $label at $pluginPath.")

    val klass = loadClass(pluginPath)!!
    interpreter!!.loadExtClass(klass)
  }

  @Suppress("WeakerAccess")
  fun loadClass(path: String): Class<*>? {
    val file = File(path)
    var klass: Class<*>? = null

    try {
      val url = file.toURI().toURL()
      val urls = Array(1) { url }

      val cl = URLClassLoader(urls)
      klass = cl.loadClass(Lime.pluginClassName)

    } catch (e: Exception) {
      println("Failed to load plugin class: ${e.message}")
    }

    return klass
  }

  @Suppress("WeakerAccess")
  fun getPluginDir(): String {
    return homePathOverride ?: homePath.orEmpty()
  }
}
