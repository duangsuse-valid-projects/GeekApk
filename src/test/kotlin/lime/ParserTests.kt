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
import lime.type.SexpList
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Black box tests for lime parser
 *
 * @since 1.0
 * @author duangsuse
 * @see Parser test target
 */
class ParserTests {
  @Test
  fun basicallyWorks() {
    assertParse("(123 234 (43 45) (44) 65 a '' \"a\")", "(123 234 (43 45) (44) 65 :a  a)")
  }

  @Test
  fun valueWorks() {
    assertParse("#t #f ()", "true false null")
  }

  @Test
  fun numberWorks() {
    assertParse("12 34 32N 1B (3443545332232.21D 1F) 2L", "12 34 32 1 (3443545332232.21 1.0) 2")
  }

  @Test
  fun parenWorks() {
    assertParse("(()(()))", "(null (null))")
  }

  @Test
  fun badWorks() {
    assertParse("())", "null BAD")
  }

  @Test
  fun parsesFormatNumber() {
    assertParse("0x1AL 0b0000 0o1", "26 0 1")
  }

  @Test(expected = RuntimeException::class)
  fun throwsUnterminatedParen() {
    assertParse("(sa cds ( 12 )", "")
  }

  /**
   * Asserts parsed to string equals to expected
   *
   * @throws AssertionError if code and expected not match
   */
  private fun assertParse(code: String, expected: String) {
    val lex = Lexer(code)
    lex.lex()
    val parser = Parser(lex.fetch())
    val result = parser.parse()
    assertEquals(ppAry(result), expected)
  }

  /**
   * Convert sexp list to string
   *
   * @param list the sexp list
   * @return recursive string print
   */
  private fun ppAry(list: SexpList): String {
    val sb = StringBuilder()
    for (obj in list) {
      if (obj is SexpList) {
        sb.append('(').append(ppAry(obj)).append(") ")
      } else sb.append(obj).append(' ')
    }
    sb.deleteCharAt(sb.lastIndex)
    return sb.toString()
  }
}
