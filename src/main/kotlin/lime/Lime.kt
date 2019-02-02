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
import lime.type.Macro
import lime.type.SexpList
import lime.type.Symbol
import lime.type.toSource
import java.io.File
import java.io.PrintStream
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.HashMap

/** Scope frame type */
typealias Scope = HashMap<Symbol, Any?>

/**
 * Lime macro expander class
 *
 * @version 1.0
 * @author duangsuse
 * @see Lexer lime lexer
 * @see Parser lime parser
 * @field sexp s-expression list to expand
 */
class Lime(private val sexp: SexpList) {
  /** Lime environment */
  private var env: Scope = HashMap()

  /** Lime expanding stack */
  @Suppress("WeakerAccess")
  var stack: LinkedList<Scope> = LinkedList()

  /**
   * Current scope
   *
   * @see stack scope stack
   */
  @Suppress("WeakerAccess")
  var scope: Scope = HashMap()

  /** Send handlers */
  @Suppress("WeakerAccess")
  var handlers: HashMap<Symbol, Method> = HashMap()

  /** on error hook */
  @Suppress("WeakerAccess")
  var onError: ILimeTraceFunc? = null

  /** on resolve variable */
  @Suppress("WeakerAccess")
  var onResolve: ILimeTraceFunc? = null

  /** on reference missing */
  @Suppress("WeakerAccess")
  var onMissing: ILimeReplacement? = null

  /** File name */
  @Suppress("WeakerAccess")
  var file: String = "<unknown>"

  /** working dir */
  @Suppress("WeakerAccess")
  var dir: File? = null

  /** Exception print stream */
  @Suppress("WeakerAccess")
  var except: PrintStream = System.out

  /**
   * Fetch sexp field
   *
   * @see sexp wrapped field
   * @return sexp value
   */
  @Suppress("unused")
  fun fetch() = sexp

  /**
   * Construct with bare code
   */
  @Suppress("unused")
  constructor(code: String) : this(parse(code))

  /**
   * Initialize __stack, __env, __lime
   */
  @Suppress("unused")
  fun initMetaProgrammingGlobals() {
    env[Symbol("__stack")] = stack
    env[Symbol("__env")] = env
    env[Symbol("__lime")] = this
  }

  /**
   * Inherit from parent interpreter
   *
   * @param parent parent interpreter to inherit from
   */
  fun inherit(parent: Lime) {
    env = parent.env
    stack = parent.stack
    handlers = parent.handlers
    onError = parent.onError
    onResolve = parent.onResolve
    onMissing = parent.onMissing
    file = parent.file
    dir = parent.dir
    except = parent.except
  }

  /**
   * Run this macro
   *
   * @return macro expanding result
   * @see sexp macro
   */
  fun run(): Any? {
    // Expand send (. native dispatch) macro
    if (sexp.isNotEmpty() && sexp.first() is Symbol && (sexp.first() as Symbol) == dotSym) {
      // detect send handler id
      sexp.removeAt(0)
      if (sexp.size < 1)
        raise("Failed to expand send, no handler id found")
      val id = sexp.removeAt(0)

      // check, expand and apply java method routine
      val len = sexp.size
      val ary = Array(len) {
        expand(sexp, it)
      }

      if (id !is Symbol)
        raise("Failed to expand send, $id not a symbol")

      // apply java routine
      return send(id as Symbol, ary)

      // Expand common macro (macro resolving)
    } else if (sexp.isNotEmpty()) {
      // get macro element
      val id = sexp.removeAt(0)

      // resolve macro
      val macro = when (id) {
        is Symbol -> resolve(id)
        is Macro -> id
        is SexpList -> expand(id)
        is Any -> resolveToMacro(id)
        else -> raise("Failed to resolve macro for $id")
      }

      // check non-macro
      macro as? Macro ?: raise("Expected macro object, found $id")

      // expand macro arguments (ary size)
      val len = sexp.size

      // expand now
      val ary = if (!(macro as Macro).append)
        Array(len) {
          expand(sexp, it)
        } else sexp.toArray()

      // do macro apply and call
      return expand(macro, ary)

      // () expanded to null
    } else if (sexp.isEmpty()) {
      return null

      // No expanding
    } else {
      raise("No rules to expand $sexp")
    }

    // dummy statement
    return null
  }

  /**
   * Resolve any object to macro
   *
   * @param obj object to resolve
   * @return macro
   */
  @Suppress("WeakerAccess")
  fun resolveToMacro(obj: Any?): Macro {
    // get application result
    val maybeMacro = resolve(toMacroSym)
    if (maybeMacro !is Macro)
      raise("Failed to resolve non-macro object, please define $toMacroSym macro")
    val macro = maybeMacro as Macro

    val maybeRet = expand(macro, Array(1) { obj })
    if (maybeRet !is Macro)
      raise("Bad non-macro $toMacroSym returned")
    else
      return maybeRet

    // dummy
    return maybeRet as Macro
  }

  /**
   * Expand a macro using args
   *
   * @param macro macro to use
   * @param args application arguments
   * @return application result
   */
  @Suppress("WeakerAccess")
  fun expand(macro: Macro, args: Array<Any?>): Any? {
    stack.push(scope)
    val childExpander = Lime(macro.fill(args.toList()))
    childExpander.inherit(this)
    val ret: Any?
    try {
      ret = childExpander.run()
    } catch (e: Exception) {
      except.println("$e\n    at file $file expanding $macro with (${args.toList()})")
      throw e
    }
    stack.pop()
    return ret
  }

  /**
   * Expand a sexpression
   *
   * @param sexp sexpression to use
   * @return application result
   */
  @Suppress("WeakerAccess")
  fun expand(sexp: SexpList): Any? {
    stack.push(this.scope)
    val childExpander = Lime(sexp)
    childExpander.inherit(this)
    val ret: Any?
    try {
      ret = childExpander.run()
    } catch (e: Exception) {
      except.println("$e\n    at file $file expanding ${sexp.toSource()}")
      throw e
    }
    stack.pop()
    return ret
  }

  /**
   * Expand argument list
   *
   * @param sexp sexpression list
   * @param idx index
   * @return expanded object
   */
  @Suppress("WeakerAccess")
  fun expand(sexp: SexpList, idx: Int): Any? {
    val exp = sexp[idx]!!
    return when (exp) {
      is SexpList -> expand(exp)
      is Symbol -> resolve(exp)
      else -> exp
    }
  }

  /**
   * Send to internal macro handler
   *
   * @param id handler id
   * @param args handler call args
   * @return internal expander result
   */
  @Suppress("WeakerAccess")
  fun send(id: Symbol, args: Array<Any?>): Any? {
    if (!handlers.containsKey(id))
      raise("Failed to index handler $id")
    val ary: Array<Any?> = Array(1) { args }

    try {
      return handlers[id]!!.invoke(null, ary)
    } catch (e: Exception) {
      if (onError != null)
        onError!!.trace(this, e.message.toString())
      except.println("$e\n    invoking handler $id with arguments ${args.toList()}")
      throw e
    }
  }

  /**
   * Throw a fatal exception
   *
   * @param msg message
   * @throws RuntimeException with message, always
   */
  @Suppress("WeakerAccess")
  fun raise(msg: String): Void {
    if (onError != null)
      onError!!.trace(this, msg)
    except.println("Lime: $msg")
    throw RuntimeException("Lime: $msg in file $file")
  }

  /**
   * Load a extension class
   *
   * @param clazz target class
   * @throws NoSuchMethodError if no method found in class
   * @see onLoadHookName hook method name
   */
  @Suppress("unused")
  fun loadExtClass(clazz: Class<*>) {
    val initMethod = clazz.getDeclaredMethod(onLoadHookName, Lime::class.java)
    initMethod.invoke(clazz, this)
  }

  /**
   * Unload a extension class
   *
   * @param clazz target class
   * @throws NoSuchMethodError if no method def found in class
   * @see onUnLoadHookName hook static method name
   */
  @Suppress("unused")
  fun unloadExtClass(clazz: Class<*>) {
    val initMethod = clazz.getDeclaredMethod(onUnLoadHookName, Lime::class.java)
    initMethod.invoke(clazz, this)
  }

  /**
   * Created for error, onResolve hooks
   *
   * @since 1.0
   */
  @FunctionalInterface
  interface ILimeTraceFunc {
    fun trace(ctx: Lime, obj: Any)
  }

  /**
   * Created for reference_missing
   *
   * @since 1.0
   */
  @FunctionalInterface
  interface ILimeReplacement {
    fun replace(ctx: Lime, obj: Any): Any
  }

  /**
   * Resolve symbol reference
   *
   * @param sym symbol
   * @return reference
   */
  @SuppressWarnings("WeakerAccess")
  fun resolve(sym: Symbol): Any? {
    if (onResolve != null)
      onResolve!!.trace(this, sym)
    return when {
      sym.equals("args") -> scope
      env.containsKey(sym) -> env[sym]
      onMissing != null -> onMissing!!.replace(this, sym)
      else -> raise("Failed to resolve reference to $sym")
    }
  }

  /**
   * Alias env#[]
   */
  operator fun get(index: String): Any? {
    return env[Symbol(index)]
  }

  /**
   * Alias for env#[]=
   */
  operator fun set(index: String, obj: Any?) {
    env[Symbol(index)] = obj
  }

  /**
   * Define a macro
   *
   * @param name name of the macro
   * @param args arguments
   * @param body body code
   */
  @Suppress("unused")
  fun defineMacro(name: String, args: String, body: String) {
    val rawList = args.split(' ')
    val argList: List<Symbol> = List(rawList.size) {
      Symbol(rawList[it])
    }
    env[Symbol(name)] = Macro(argList, parse(body))
  }

  /**
   * Register a method as expand handler in klass
   *
   * @param klass target class to find methods
   * @param mid method id to register
   * @return method object
   */
  @Suppress("unused")
  fun register(name: String, klass: Class<Any>, mid: String): Method? {
    return try {
      val method = klass.getDeclaredMethod(mid, Array<Any?>::class.java)
      handlers[Symbol(name)] = method
      method
    } catch (e: NoSuchMethodException) {
      null
    }
  }

  /** Static strings */
  companion object {
    @JvmStatic
    @Suppress("unused")
    val VERSION: String = "1.1"

    @JvmStatic
    val onLoadHookName = "onLoad"

    @JvmStatic
    val onUnLoadHookName = "onUnload"

    @JvmStatic
    @Suppress("unused")
    val pluginClassName = "LimePlugin"

    /** Native dispatch mid */
    private val dotSym = Symbol(".")

    /** toMacro symbol */
    private val toMacroSym = Symbol("->macro")

    /**
     * Parse a lime code string
     *
     * @param code lime code
     * @return parsed sexp list
     */
    @JvmStatic
    fun parse(code: String): SexpList {
      val lexer = Lexer(code)
      lexer.lex()
      val parser = Parser(lexer.fetch())
      return parser.parse()
    }

    /**
     * Main helper program
     *
     * @param args system given args
     */
    @JvmStatic
    fun main(args: Array<String>) {
      val scan = Scanner(System.`in`)
      val buf = StringBuilder()

      // scan input
      while (scan.hasNextLine())
        buf.append(scan.nextLine()).append('\n')

      // lexical analyze
      val lex = Lexer(buf.toString())
      println("Lexing…")
      lex.lex()

      // print lex result
      val result = lex.fetch()
      result.forEach {
        println("${it.line}:${it.col} = $it")
      }

      // parse
      println("Parsing…")
      val parserResult = Parser(result).parse()

      // interpret it
      var interpreter = Lime(parserResult)

      // each sexp in list
      for (i in parserResult) {
        interpreter.inherit(interpreter)

        if (i !is SexpList) {
          println("Skipping non-sexp $i")
          continue
        }

        println("Running $i…")

        interpreter = Lime(i)
        println(interpreter.run())
      }
    }
  }
}
