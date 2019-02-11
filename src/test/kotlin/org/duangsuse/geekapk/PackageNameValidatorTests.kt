package org.duangsuse.geekapk
import org.duangsuse.geekapk.constraint.PackageName
import org.junit.Test

class PackageNameValidatorTests {
  private val validator = PackageName.Validator()

  @Test
  fun acceptsValidPackageNames() {
    validNames.all { validator.isValid(it, null) }
  }

  @Test
  fun rejectsInvalidPackageNames() {
    invalidNames.none { validator.isValid(it, null) }
  }

  companion object {
    val validNames = setOf("org.duangsuse.minbase64")
    val invalidNames = setOf("we can fly", "", null, "abc".repeat(64 / 3))
  }
}
