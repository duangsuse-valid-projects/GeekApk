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

import lime.Lime
import lime.type.Macro
import lime.type.SexpList
import lime.type.Symbol
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests for macro-applying capabilities
 *
 * @see Macro tested class
 */
class MacroFillingTest {
  @Test
  fun itWorks() {
    val sexp = Lime.parse("(a b c)").first()!! as SexpList
    val args = List(2) {
      when (it) {
        0 -> Symbol("a")
        1 -> Symbol("b")
        else -> Symbol("ca")
      }
    }
    val filling = List<Any>(2) {
      when (it) {
        0 -> 2
        1 -> 3
        else -> 1
      }
    }
    val m = Macro(args, sexp)
    val result = m.fill(filling)
    assertEquals(result[0], 2)
    assertEquals(result[1], 3)
    assertEquals(result[2], Symbol("c"))
  }

  @Test(expected = RuntimeException::class)
  fun throwsArgSizeMismatch() {
    val sexp = Lime.parse("(a b c)")
    val args = List(2) {
      when (it) {
        0 -> Symbol("a")
        1 -> Symbol("b")
        else -> Symbol("ca")
      }
    }
    val filling = List<Any>(4) {
      when (it) {
        0 -> 2
        1 -> 3
        2 -> 4
        3 -> 4
        else -> 1
      }
    }
    val m = Macro(args, sexp)
    m.fill(filling)
  }
}
