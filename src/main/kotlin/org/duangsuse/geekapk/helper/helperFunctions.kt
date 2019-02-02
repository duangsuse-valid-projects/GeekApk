package org.duangsuse.geekapk.helper

/**
 * Get a Geekapk (geekapk.) property
 * or launch a block, and return it's return value
 *
 * @param property property name
 * @param otherwise or launch a block
 * @return System property geekapk.property
 */
fun getGeekApkConfigOr(property: String, otherwise: () -> String?): String? {
  val geekProp = "geekapk.$property"
  return if (System.getProperties().containsKey(geekProp))
    System.getProperty(geekProp)
  else otherwise()
}

/**
 * Get geekapk property or null pointer
 */
fun getGeekApkConfigOrNull(property: String) = getGeekApkConfigOr(property) {null}

/**
 * Get geekapk property or other string
 */
fun getGeekApkConfigOr(property: String, otherwise: String): String = getGeekApkConfigOr(property) {otherwise}!!

/**
 * Get geekapk config, if failed, throw an exception
 *
 * Why not use functional Option class?
 *
 * @throws IllegalStateException when property not exists
 */
@Throws(IllegalStateException::class)
fun mustGetGeekApkConfig(property: String): String {
  return getGeekApkConfigOrNull(property) ?: throw IllegalStateException("Property must be configured: \"$property\"")
}
