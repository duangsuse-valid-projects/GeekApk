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

/** Items in the list maybe null */
typealias SexpressionList = ArrayList<Any?>

/**
 * Lime S-expression list type
 * <br>
 * Lime simply go through each nullable element of a s-exp list to expand macro calls
 *
 * @see ArrayList wrapped type of Maybe<Any>
 * @author duangsuse
 * @since 1.0
 */
class SexpList : SexpressionList()

/**
 * Escape a string to output
 *
 * @return escaped string
 */
private fun String.escape(): String {
  val ret = StringBuilder().append('"')

  // escape each character
  this.forEach {
    when (it) {
      '\b' -> ret.append("\\b")
      '\t' -> ret.append("\\t")
      '\n' -> ret.append("\\n")
      '\r' -> ret.append("\\r")
      '\"' -> ret.append("\\\"")
      '\\' -> ret.append("\\\\")
      else -> ret.append(it)
    }
  }

  // result
  return ret.append('"').toString()
}

/**
 * To lime expression source
 *
 * @return lime expression source
 */
fun SexpList.toSource(): String {
  val builder = StringBuilder().append('(')
  forEach {
    when (it) {
      is SexpList -> builder.append(it.toSource()).append(' ')
      is String -> builder.append(it.escape()).append(' ')
      else -> builder.append(it).append(' ')
    }
  }
  if (builder.isNotEmpty())
    builder.deleteCharAt(builder.lastIndex)
  return builder.append(')').toString()
}
