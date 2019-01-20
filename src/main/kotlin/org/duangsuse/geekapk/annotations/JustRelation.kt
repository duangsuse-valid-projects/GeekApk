package org.duangsuse.geekapk.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class JustRelation(val p: String, val q: String, val rel: String = "p ~ q")
