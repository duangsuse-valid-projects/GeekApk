package org.duangsuse.geekapk.annotation

/**
 * Counter for related entity
 *
 * @param entity entity to be counted
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class CounterFor(val entity: String)
