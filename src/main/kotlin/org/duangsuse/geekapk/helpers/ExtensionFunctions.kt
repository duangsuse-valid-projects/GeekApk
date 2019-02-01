package org.duangsuse.geekapk.helpers

/**
 * Let a block be repeated for n times
 *
 * @param times looped rounds
 * @receiver block to be repeated
 */
inline infix fun <reified A : Number, B> ((A) -> B).loopFor(times: Int) {
  assert(times >= 0)
    { "Input $times must be greater or equal to zero" }
  var restIteration = times
  while (restIteration > 0) {
    this(restIteration as A)
    restIteration--
  }
}

/**
 * identical function for numbers
 * f**king (fake) DSL
 */
inline fun <reified R : Number> Number.times(): R = this as R
