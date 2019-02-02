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

// This file contains source code of the lime lexer (a.k.a. scanner)

package lime.text

// required for data structures

// required for token operations
import lime.text.Token.TokenType
import java.util.*

// lexical analytics result
/** List of tokens */
typealias TokenList = LinkedList<Token>

// analyze a string
/**
 * Lime s-expression lexer
 *
 * @author duangsuse
 * @since 1.0
 * @param sourceCode code to lex
 * @throws RuntimeException failed to escape string
 */
class Lexer(sourceCode: String) {
  /**
   * Source code char array
   */
  private val sourceAry = sourceCode.toCharArray() // to forEach iterate next time

  /**
   * Running at line
   */
  private var line = 1 // lexing line

  /**
   * Running at column
   */
  private var col = 1 // lexing column

  /**
   * Char index
   */
  private var i = 0 // current character index

  /**
   * Token buffer
   */
  private var tokBuf = TokenList() // temp tokens

  /**
   * Main lex branch logic
   *
   * @throws RuntimeException if string escape fails
   */
  fun lex() { // perform analytics
    while (getChar() != '\u0000') { // while not EOF
      when {
        getChar() == ';' -> skipComment() // skip comments (to newline) if comment char seen
        getChar() == '#' -> yyFragment() // lex '#' syntax
        getChar() in blanks -> nextChar() // skip blanks
        getChar() in lispSymbols -> yyLispKwd() // sexpression paren expression
        getChar() in numericChars -> yyNumber() // numeric
        getChar() == '\'' -> yySingleString() // single-quoted string
        getChar() == '"' -> yyString() // double-quoted string
        else -> yyIdentifier() // else, identifier
      }
    }
  }

  /**
   * Fetch lexer output
   *
   * @return lexer output
   */
  fun fetch(): TokenList = TokenList(tokBuf.reversed()) // reversed since push to LIFO

  // Lexer logic
  // '#' Lexical rules
  private fun yyFragment() {
    nextChar(); col-- // skip '#', but shift column
    when {
      getChar() == 't' -> pushTok(TokenType.TRUE, "#t") // true literal
      getChar() == 'f' -> pushTok(TokenType.FALSE, "#f") // false literal
      getChar() == ' ' -> pushTok(TokenType.IDENTIFIER, "#") // just '#'
      getChar() == 'n' -> pushTok(TokenType.UNIT, "#n") // null literal B
      else -> throw RuntimeException("""Failed to lex fragment
                | '${getChar()}' at $line:$col: must be t/f/n""".trimMargin()) // error otherwise
    }
    nextChar(); col++ // skip #'.', shift right column
  }

  // '(' Lexical rules
  private fun yyLispKwd() {
    if (getChar() == '(') { // is start of sexp list
      if (lookAhead() == ')') { // just an unit!
        pushTok(TokenType.UNIT, "()")
        nextChar() // skip it, I will call nextChar() last
      } else { // is an application
        pushTok(TokenType.PAREN, "(")
      }
    } else if (getChar() == ')') { // is application end
      pushTok(TokenType.PAREN_CLOSING, ")")
    }
    nextChar() // auto skip
  }

  // Number lexical rules
  private fun yyNumber() {
    val sb = StringBuilder() // buffer
    val oldColumn = col // old number column
    val oldLine = line // old number start

    // until token eliminates
    while (!tokDelimiters.contains(getChar())) {
      // push to buffer till token ends
      sb.append(getChar()); nextChar()
    }
    val lastChar = sb.last() // 'type' bit of this string
    var shouldPopType = true // contains 'type' bit?

    val newColumn = col // new column
    val newLine = line // new line

    col = oldColumn; line = oldLine // reset for debug info
    // parse number type
    val type: TokenType = when (lastChar) {
      'N' -> TokenType.BIG_INT
      'D' -> TokenType.BIG_DECIMAL
      'F' -> TokenType.FLOAT
      'L' -> TokenType.LONG
      'B' -> TokenType.BYTE
      else -> if (lastChar in numericChars || lastChar in hexChars) {
        shouldPopType = false // type bit bot present
        TokenType.NUMBER // just hex / dec number
      } else throw RuntimeException("'$lastChar' not a valid number type at $line:$col")
    }

    if (shouldPopType) sb.deleteCharAt(sb.lastIndex) // remove type bit

    pushTok(type, sb.toString())

    col = newColumn; line = newLine // reset for iteration
  }

  // Lex string token
  private fun yyString() {
    val sb = StringBuilder() // buffer
    val oldColumn = col // old string column
    val oldLine = line // old string start

    nextChar() // skip '"' character

    // de-escape string
    while (true) {
      if (getChar() == '\\') { // found escape character
        nextChar() // skip it
        sb.append(when (getChar()) {
          'b' -> '\b' // break escape
          't' -> '\t' // tab escape
          'n' -> '\n' // newline escape
          'r' -> '\r' // return escape
          '"' -> '"' // double-quoted string ending
          '\\' -> '\\' // just '\'
          else -> throw RuntimeException("""Illegal escape
                        | character '${lookAhead()}' at $line:$col""".trimMargin()) // bad escape
        })
      } else {
        if (getChar() == '"') { // string ended
          val newColumn = col // new column
          val newLine = line // new line
          col = oldColumn; line = oldLine // reset for debug info
          pushTok(TokenType.STRING, sb.toString())
          col = newColumn; line = newLine // reset for iteration
          nextChar(); return // skip '"'
        } else if (getChar() == '\u0000' || getChar() == '\n') {
          throw RuntimeException("Unterminated string at $line:$col")
        }

        // not a special character (escape, string delimiter, newline)
        sb.append(getChar())
      }

      nextChar() // process next character, skip string delimiter
    }
  }

  // "'" Single quote string rules
  private fun yySingleString() {
    val sb = StringBuilder() // buffer
    val oldColumn = col // old string column
    val oldLine = line // old string start

    nextChar() // skip "'"

    // ignore newline
    while (getChar() != '\'') { // while not ended
      if (getChar() == '\u0000') // EOF seen
        throw RuntimeException("EOF scanning single string at $line:$col")
      sb.append(getChar()) // record
      nextChar() // next character
    }
    val newColumn = col // new column
    val newLine = line // new line

    col = oldColumn; line = oldLine // reset for debug info
    pushTok(TokenType.SINGLE_STRING, sb.toString()) // push string
    col = newColumn; line = newLine // reset for iteration

    nextChar() // skip ending
  }

  // Identifier lexical rules
  private fun yyIdentifier() {
    val sb = StringBuilder() // string buffer
    val oldColumn = col // old string column
    val oldLine = line // old string start

    // keep logging until token delimiter seen
    while (!tokDelimiters.contains(getChar())) {
      sb.append(getChar()); nextChar() // log character
    }
    val newColumn = col // new column
    val newLine = line // new line

    col = oldColumn; line = oldLine // reset for debug info
    pushTok(TokenType.IDENTIFIER, sb.toString()) // push identifier
    col = newColumn; line = newLine // reset for iteration
  }
  // End lexer logic

  /**
   * Push a token to buffer with debug information
   *
   * @see Lexer.tokBuf
   * @param type token type
   * @param str token string
   */
  private fun pushTok(type: TokenType, str: String) = tokBuf.push(Token(type, str, line, col))

  /**
   * Skip line comment
   */
  private fun skipComment() {
    while (getChar() != '\n' && getChar() != '\u0000') nextChar() // skip comments till newline
  }

  /**
   * Return current character
   *
   * @return current character or \u0000 if index out of range
   */
  private fun getChar() = if (i > sourceAry.lastIndex) '\u0000' else sourceAry[i]

  /**
   * Return next character
   *
   * @return next character or \u0000 if index out of range
   */
  private fun lookAhead() = if (i + 1 > sourceAry.lastIndex) '\u0000' else sourceAry[i + 1]

  /**
   * Skip one character, updates debug information as well
   */
  private fun nextChar() {
    if (getChar() == '\n') { // newline seen
      line++ // new line
      col = 1 // reset column
    } else col++ // add 1 to column
    i++ // ensure next character
  }

  /**
   * Static char sets
   */
  companion object {
    private const val lispSymbols = "()" // lisp keywords
    private const val numericChars = "0123456789" // decimal numbers
    @Suppress("SpellCheckingInspection")
    private const val hexChars = "abcdef" // hex numbers
    private const val blanks = " \t\n\r\u000c" // blanks
    private const val tokDelimiters = "$blanks$lispSymbols;\u0000" // token delimiters
  }

  /**
   * Main class for lexer debugging
   *
   * @author duangsuse
   * @since 1.0
   * @see Lexer
   */
  object Main {
    /**
     * Main function, read all tokens from input and print out tokens with debug info
     *
     * @param args System arg ary
     */
    @JvmStatic
    fun main(args: Array<String>) {
      val scan = Scanner(System.`in`) // scan input
      val buf = StringBuilder()

      // log input
      while (scan.hasNextLine())
        buf.append(scan.nextLine()).append('\n')

      // lexical analyze
      val lex = Lexer(buf.toString())
      lex.lex() // do analytics
      val result = lex.fetch()

      // print result
      result.forEach {
        println("${it.line}:${it.col} = $it")
      }
    }
  }
}
