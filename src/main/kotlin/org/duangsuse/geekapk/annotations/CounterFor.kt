package org.duangsuse.geekapk.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class CounterFor(val entity: String)
