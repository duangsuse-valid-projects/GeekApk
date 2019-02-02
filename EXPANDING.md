# Lime 的「六个展开」

3. ((macro sexp) arglist)
5. (macro-value arglist)
1. (symbol arglist)
2. (. handlerid arglist)
6. object
4. ()

## 额外展开：

`^#` 定义特殊的宏对象，它接受后面所有 S 表达式
`(1 args)` 如果对象拥有 `Macro toMacro()` 方法，尝试使用 `toMacro()` 返回的宏
否则，尝试全局的 `(->macro 1)` 返回的宏，如果没有定义直接失败，如果返回的不是宏也失败

这所谓「六个展开」其实可以归纳为三类

## 1. 宏展开

Lime 基于宏和自省功能来实现类似 Scheme 的语法，宏最终被展开为原生调用执行

```scheme
(symbol arglist)
((macro sexp) arglist)
(macro-value arglist)
(object arglist)
```

「宏解析」允许解析「符号」、S 表达式列表、宏对象、可以转换为宏的对象

Apply（施用宏）过程中，S 表达式首项必须是一个能被解析为宏的对象
而其后皆为宏的参数，如果参数长度不对，抛出 `Arity Mismatch`，除非是 `vararg` 宏

## 2. 原生调用

Lime 使用原生调用赋予拥有反射能力的 JVM 方便执行动态动作的能力

```scheme
(. handlerid arglist)
```

Lime 必须有一个手段把宏变为实际行动，这就是 Lime 如此做的途径，“原生”调用

`handlerid` 其后皆为宏的参数，如果参数长度不对，抛出 `Arity Mismatch`，除非是 `vararg` 接收器

## 3. 无展开

不进行展开，直接返回原对象

```scheme
()
object
```

Lime 不知道该如何展开这些对象，一般它认为这些对象已经被展开，所以直接返回他们
