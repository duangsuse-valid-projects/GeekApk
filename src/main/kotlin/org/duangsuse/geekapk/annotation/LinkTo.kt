package org.duangsuse.geekapk.annotation

/**
 * SQL Foreign key links
 *
 * @param entity Another entity to be linked
 * @param rel Relation between this domain object and other
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class LinkTo(val entity: String, val rel: Relation)

/**
 * Relation kinds
 */
enum class Relation {
  /**
   * It's belonging to that entity
   */
  BELONGING,

  /**
   * It has many "that entity"
   */
  HAS_MANY,

  /**
   * It has exactly one "that entity"
   */
  HAS_ONE,

  /** No special relation (symmetric) */
  EQUIVALENT
}
