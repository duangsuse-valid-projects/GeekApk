package org.duangsuse.geekapk.helpers

inline infix fun <reified A : Number, B> ((A) -> B).loopFor(times: Int) {
  var restIteration = times
  while (restIteration > 0) {
    this(restIteration as A)
    restIteration--
  }
}
