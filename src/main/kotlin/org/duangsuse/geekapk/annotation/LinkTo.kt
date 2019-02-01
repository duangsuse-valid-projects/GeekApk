package org.duangsuse.geekapk.annotation

/**
 * SQL Foreign key links
 *
 * entity: Another entity to be linked
 * rel: Relation between this domain object and other
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class LinkTo(val entity: String, val rel: Relation)

/**
 * Relation kinds
 */
enum class Relation {
  BELONGING,
  HAS_MANY,
  HAS_ONE,
  EQUIVALENT /* No special relation (symmetric) */
}
