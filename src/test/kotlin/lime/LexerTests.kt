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

import lime.text.Lexer
import lime.text.Token
import org.junit.Assert
import org.junit.Test

/**
 * Black box tests for the lime lexer
 *
 * @since 1.0
 * @author duangsuse
 * @see Lexer test target
 */
class LexerTests {
  private fun assertEquals(a: Any?, b: Any?, msg: String = "Failed to assert $a == $b") = Assert.assertEquals(msg, a, b)

  @Test
  fun worksBasically() {
    assertLexEquals(lex("123 123 345 ;ruby is good\n '' jit ruby #t #f () ( ) 2N"), "123 123 345 '' jit ruby #t #f () ( ) 2N")
    assertLex("(# () a b c)", "( # () a b c )")
    assertLex("123 445 332\n123 345 211", "123 445 332 123 345 211")
  }

  @Test
  fun addsDebug() {
    val result = lex("a b c \"ss\" '' \"\" as up ;233\n () )")
    assertEquals(1, result[0].line)
    assertEquals(1, result[0].col)
    // "ss" token
    assertEquals(1, result[3].line)
    assertEquals(7, result[3].col)
    // '' token
    assertEquals(12, result[4].col)
    // () token
    assertEquals(2, result[8].col)
    // ) token
    assertEquals(2, result[9].line)
    assertEquals(5, result[9].col)
  }

  @Test
  fun lexFragment() = assertLex("#t #f #t #f\n #t #f #t#f#f#t", "#t #f #t #f #t #f #t #f #f #t")

  @Test
  fun lexLispKwd() = assertLex("() (( )) ())( )( (", "() ( ( ) ) () ) ( ) ( (")

  @Test
  fun lexNumber() = assertLex("123 321 12.32 43.2L 32323B 54D 0x233 0b233 0N 0F", "123 321 12.32 43.2L 32323B 54D 0x233 0b233 0N 0F")

  @Test
  fun lexString() = assertLex("'' \"\" 233 \"'\"", "'' \"\" 233 \"'\"")

  @Test
  fun escapesString() = assertLex("\"\\r\\n\\b\\t\\\"\"", "\"\\r\\n\\b\\t\\\"\"")

  @Test
  fun lexIdentifier() = assertLex("indent \"\" 表示", "indent \"\" 表示")

  @Test
  fun dropsComments() = assertLex("ass;\nb", "ass b")

  @Test
  fun itsGoodLexicalSyntax() = assertLex("123() \"\"'' 233", "123 () \"\" '' 233")

  @Test(expected = RuntimeException::class)
  fun singleThrowsWhenUnterminated() {
    lex("ass 123 ' 32s \n ' as 1 '")
  }

  @Test(expected = RuntimeException::class)
  fun doubleThrowsWhenUnterminated() {
    lex("\" \n\"")
    lex("\" ass we can")
  }

  @Test(expected = RuntimeException::class)
  fun doubleThrowsWhenBadEscaped() {
    lex("\"\\u\"")
  }

  @Test(expected = RuntimeException::class)
  fun numberThrowsBadTypeId() {
    lex("122D 3234x")
  }

  @Test(expected = RuntimeException::class)
  fun fragmentThrowsBadFragment() {
    lex("#x")
  }

  @Test
  fun fragmentNotThrowingBadSingleFragment() {
    lex("# ")
  }

  /**
   * Sort alias for {@code assertLexEquals(lex(code), expected)}
   *
   * @see assertLexEquals wrapped fun
   */
  private fun assertLex(code: String, expected: String) = assertLexEquals(lex(code), expected)

  /**
   * Assert tokens {@code toString} concat equals
   *
   * @throws AssertionError if src and expected not equals
   */
  private fun assertLexEquals(src: List<Token>, expected: String) {
    val sb = StringBuilder()
    src.forEach {
      sb.append(it.toString()).append(' ')
    }
    sb.deleteCharAt(sb.lastIndex)
    assertEquals(sb.toString(), expected, "Failed to assert lex equals")
  }

  /**
   * Lex code, returning result
   *
   * @return lex result
   */
  private fun lex(code: String): List<Token> {
    val lexer = Lexer(code)
    lexer.lex()
    return lexer.fetch()
  }
}
