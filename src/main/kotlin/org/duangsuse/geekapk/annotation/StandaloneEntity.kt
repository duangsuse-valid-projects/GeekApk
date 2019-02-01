package org.duangsuse.geekapk.annotation

/**
 * Standalone entities like App, User, Comment
 */
/* Stand-alone REST entity */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class StandaloneEntity(val name: String)
