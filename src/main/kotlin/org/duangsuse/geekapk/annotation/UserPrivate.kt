package org.duangsuse.geekapk.annotation

/**
 * A user's private field should not be seen without authentication
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class UserPrivate
