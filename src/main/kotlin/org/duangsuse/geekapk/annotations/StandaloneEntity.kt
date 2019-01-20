package org.duangsuse.geekapk.annotations

/* Stand-alone REST entity */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class StandaloneEntity(val entityName: String)
