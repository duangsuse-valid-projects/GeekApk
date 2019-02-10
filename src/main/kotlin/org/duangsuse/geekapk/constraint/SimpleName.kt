package org.duangsuse.geekapk.constraint

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

/**
 * Simple user name
 */
@Constraint(validatedBy = [SimpleName.Validator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class SimpleName {
  class Validator: ConstraintValidator<SimpleName, CharSequence> {
    override fun isValid(value: CharSequence?, context: ConstraintValidatorContext?): Boolean {
      value?.run { return matches(VALID_PATTERN) }
      return false
    }

    companion object {
      /**
       * First 3 letters must be plain letters (A-Za-z, _), rest can have digits and dash (-), at least 4 char long
       */
        val VALID_PATTERN = Regex("[A-Za-z_]{3}[A-Za-z0-9_\\-]+")
    }
  }
}
