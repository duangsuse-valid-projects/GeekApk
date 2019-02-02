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

package lime.type

/**
 * Lime applicable macro class
 *
 * @since 1.0
 * @author duangsuse
 */
class Macro(private val args: List<Symbol>, private val body: SexpList, val append: Boolean = false) {
  /** Vararg identifier name */
  private val varargId = Symbol("vararg")

  /**
   * Fill arguments
   *
   * @args arg given arguments
   * @return filled s-expression list
   */
  fun fill(arg: List<Any?>): SexpList {
    return if (args.isNotEmpty() && args.first() == varargId) {
      fullFill(body, varargId, arg)
    } else {
      if (arg.size != args.size)
        throw RuntimeException("Arity mismatch: ${args.size} expected but ${arg.size} given")
      var i = 0
      // recursively fill macro body
      var ret = body
      while (i <= args.lastIndex) {
        ret = fullFill(ret, args[i], arg[i])
        i++
      }
      return ret
    }
  }

  /**
   * Replace all symbol with an id to obj
   *
   * @param list list to replace
   * @param sym src symbol
   * @param obj dst object
   * @return replaced list
   */
  private fun fullFill(list: SexpList, sym: Symbol, obj: Any?): SexpList {
    val newList = SexpList()
    for (i in list) {
      when (i) {
        is SexpList -> newList.add(fullFill(i, sym, obj))
        sym -> newList.add(obj)
        else -> newList.add(i)
      }
    }
    return newList
  }

  /**
   * List to item-sep with whitespace
   *
   * @return elements sep with whitespaces
   */
  private fun List<Any>.toWhiteSpaceString(): String {
    val sb = StringBuilder()

    forEach {
      sb.append(it).append(' ')
    }

    if (sb.isNotEmpty())
      sb.deleteCharAt(sb.lastIndex)
    return sb.toString()
  }

  /**
   * Convert macro to a human-readable format
   *
   * @return Macro<args, body>
   * @since 1.0
   */
  override fun toString(): String {
    val macro = if (append) "^#" else "#"
    return "($macro (${args.toWhiteSpaceString()}) ${body.toSource()})"
  }
}
