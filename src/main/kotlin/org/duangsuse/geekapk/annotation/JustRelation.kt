package org.duangsuse.geekapk.annotation

/**
 * Just a (K, V) pair relation abstraction
 *
 * > Why not @OneToMany ???
 *
 * I did not know this JPA annotation when I started writing these code...
 * @param p the first object
 * @param q the second object
 * @param rel relationship between them
 * @author duangsuse
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class JustRelation(val p: String, val q: String, val rel: String = "p ~ q")
