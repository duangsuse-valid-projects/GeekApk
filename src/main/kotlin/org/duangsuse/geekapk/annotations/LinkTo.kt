package org.duangsuse.geekapk.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class LinkTo(val entity: String, val rel: Relation)

enum class Relation {
  BELONGING,
  HAS_MANY,
  HAS_ONE,
  EQUIVALENT
}
