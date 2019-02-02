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

import lime.text.Token.TokenType
import lime.type.SexpList
import lime.type.Symbol
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

/**
 * The lite recursive "recursive descent" parser
 *
 * @author duangsuse
 * @since 1.0
 * @see Lexer lexer
 */
class Parser(private val tokens: TokenList) {
  /** Current token index */
  private var i = 0

  /**
   * Parse the token list to sexp list, converting all value token to value object
   * <br>
   * Accepts {@code DEBUG} env, if set to YES, print parse recursion
   *
   * @throws RuntimeException if parse fails
   * @throws NumberFormatException if number parse fails
   * @return parsed sexp list
   */
  fun parse(): SexpList {
    // debug mode
    if (System.getenv("DEBUG") == "YES")
      println("Parsing $tokens")

    val sexp = SexpList()
    while (i < tokens.size) {
      val current = tokens[i]
      when (current.type) {
        TokenType.TRUE -> sexp.add(true)
        TokenType.FALSE -> sexp.add(false)
        TokenType.UNIT -> sexp.add(null)
        TokenType.NUMBER -> sexp.add(parseNum(current.strValue))
        TokenType.BIG_INT -> sexp.add(BigInteger.valueOf(parseNum(current.strValue).toLong()))
        TokenType.BIG_DECIMAL -> sexp.add(BigDecimal.valueOf(parseNum(current.strValue).toDouble()))
        TokenType.FLOAT -> sexp.add(parseNum(current.strValue).toFloat())
        TokenType.LONG -> sexp.add(parseNum(current.strValue).toLong())
        TokenType.BYTE -> sexp.add(parseNum(current.strValue).toByte())
        TokenType.PAREN -> {
          val result = recursiveParse(tokens, i)
          // add parsed ary
          sexp.add(result.first)
          i = result.second - 1
        }
        TokenType.PAREN_CLOSING -> sexp.add("BAD")
        TokenType.STRING -> sexp.add(current.strValue)
        TokenType.SINGLE_STRING -> sexp.add(current.strValue)
        TokenType.IDENTIFIER -> sexp.add(Symbol(current.strValue))
      }
      ++i
    }

    // debug print result
    if (System.getenv("DEBUG") == "YES")
      println("Parsed $sexp")
    return sexp
  }

  /**
   * Sexp parse recursion
   *
   * @param tokens tokens to parse
   * @param beginIndex ( keyword beginning
   * @return pair of s-expression list and last element index
   */
  private fun recursiveParse(tokens: TokenList, beginIndex: Int): Pair<SexpList, Int> {
    // where is the closing bracket?
    var depth = 0
    var index = beginIndex

    // find paren closing
    while (index <= tokens.lastIndex) {
      val current = tokens[index]
      if (current.type == TokenType.PAREN)
        depth++
      else if (current.type == TokenType.PAREN_CLOSING)
        depth--

      index++
      if (depth == 0)
        break
    }

    // unclosed paren detected
    if (depth != 0)
      throw RuntimeException("Unclosed paren from $beginIndex")

    // Parse that fuck recursive
    return Pair(Parser(TokenList(tokens.subList(beginIndex + 1, index - 1))).parse(), index)
  }

  /**
   * Parse a number, maybe 0x 0b 0o number
   *
   * @see BigDecimal parsing util
   * @see BigInteger parsing util
   * @throws NumberFormatException if parse fails
   * @param s numeric string
   * @return number parsed
   */
  private fun parseNum(s: String): Number {
    val str = s.replace("_", "")
    return when {
      str.contains('x') -> BigInteger(str.replace("x", ""), 16)
      str.contains('b') -> BigInteger(str.replace("b", ""), 2)
      str.contains('o') -> BigInteger(str.replace("o", ""), 8)
      else -> if (str.contains('.')) BigDecimal(str) else BigInteger(str)
    }
  }

  /**
   * Main class for parser debugging
   *
   * @author duangsuse
   * @since 1.0
   * @see Parser
   */
  object Main {
    /**
     * Main function, read all tokens from input and print out tokens with debug info
     *
     * @param args System arg ary
     */
    @JvmStatic
    fun main(args: Array<String>) {
      val scan = Scanner(System.`in`)
      val buf = StringBuilder()

      // scan system input
      while (scan.hasNextLine())
        buf.append(scan.nextLine()).append('\n')

      // lexical analyze
      val lex = Lexer(buf.toString())
      lex.lex()
      val result = lex.fetch()

      // print lexer output
      result.forEach {
        println("${it.line}:${it.col} = $it")
      }

      // parse and print parsing result
      val parserResult = Parser(result).parse()
      printAry(parserResult)
    }

    /**
     * Print all objects in a sexp list, arrays printed with ( )
     *
     * @param list list to print
     */
    private fun printAry(list: SexpList) {
      list.forEach {
        if (it is SexpList) {
          println('(')
          printAry(it)
          println(')')
        } else {
          println(it)
        }
      }
    }
  }
}
