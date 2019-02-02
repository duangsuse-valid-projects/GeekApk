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

package lime.text

/**
 * Lime token class
 * <br>
 * Including token type, str value, line and column information
 *
 * @author duangsuse
 * @since 1.0
 * @see Lexer lexer class
 * @see TokenType token types
 * @property type Token Type
 * @property strValue string value
 * @property line token is found at line
 * @property col token is found at col
 */
class Token constructor(val type: TokenType, val strValue: String, val line: Int, val col: Int) {
  /**
   * Lime token types
   *
   * @author duangsuse
   * @since 1.0
   * @see Token token class
   * @see Lexer lime lexer
   */
  enum class TokenType {
    /** #t token */
    TRUE,
    /** #f token */
    FALSE,
    /** () token */
    UNIT,
    /** number token */
    NUMBER,
    /** 2333N token */
    BIG_INT,
    /** 233D token */
    BIG_DECIMAL,
    /** 4F token */
    FLOAT,
    /** 56L token */
    LONG,
    /** 5B token */
    BYTE,
    /** ( token */
    PAREN,
    /** ) token */
    PAREN_CLOSING,
    /** " token */
    STRING,
    /** ' token */
    SINGLE_STRING,
    /** identifier token */
    IDENTIFIER
  }

  /**
   * Escape a string to output
   *
   * @return escaped string
   */
  private fun String.escape(): String {
    val ret = StringBuilder()

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
    return ret.toString()
  }

  /**
   * To a human-readable String
   *
   * @return human readable string
   */
  override fun toString(): String {
    return when (type) {
      TokenType.BIG_INT -> "${strValue}N"
      TokenType.BIG_DECIMAL -> "${strValue}D"
      TokenType.FLOAT -> "${strValue}F"
      TokenType.LONG -> "${strValue}L"
      TokenType.BYTE -> "${strValue}B"
      TokenType.STRING -> "\"${strValue.escape()}\""
      TokenType.SINGLE_STRING -> "'$strValue'"
      else -> strValue
    }
  }
}
