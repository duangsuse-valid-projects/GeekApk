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
 * Lime symbol is a string identifier reference to variable
 *
 * @see String wrapped identifier value
 * @author duangsuse
 * @since 1.0
 * @param id identifier string
 */
class Symbol(private val id: String): Any(), CharSequence, Comparable<Symbol> {
  /**
   * Convert to human-readable string
   *
   * @return {@code :$id}
   */
  override fun toString() = ":$id"

  /**
   * Proxy from id field
   *
   * @see id real field
   * @return true if objects value equal
   */
  override operator fun equals(other: Any?): Boolean {
    if (other == null || other !is Symbol)
      return false
    return id == other.id
  }

  /**
   * Is this symbol same to the string
   *
   * @see id real string field
   * @return true if equals
   */
  fun equals(str: String): Boolean {
    return id == str
  }

  /**
   * Proxy from id hashcode
   *
   * @see id real field
   * @return id hashcode
   */
  override fun hashCode(): Int {
    return id.hashCode()
  }

  override val length: Int = id.length
  operator fun plus(other: Any?): String = id.plus(other)
  override fun get(index: Int): Char = id[index]
  override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = id.subSequence(startIndex, endIndex)
  override operator fun compareTo(other: Symbol): Int = id.compareTo(other.id)
}
