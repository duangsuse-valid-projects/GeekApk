package org.duangsuse.geekapk.annotation

/**
 * Counter for related entity
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class CounterFor(val entity: String)
