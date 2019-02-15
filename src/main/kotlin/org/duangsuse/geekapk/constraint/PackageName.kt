package org.duangsuse.geekapk.constraint

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

/**
 * Covariant over Payload (<out Payload> is subtype of Payload)
 */
typealias PayloadVariable =  Array<KClass<out Payload>>

/**
 * Boilerplate code written by duangsuse
 * duplicate of SimpleName validator
 *
 * @author duangsuse
 */
@MustBeDocumented
@Constraint(validatedBy = [PackageName.Validator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class PackageName(val maxSize: Int = 1, val message: String = "Invalid package name, should match [A-Za-z0-9_\\\\.]{64}", // yoo
                             @Suppress("unused") val payload: PayloadVariable= [], @Suppress("unused") val groups: Array<KClass<*>> = []) {
  class Validator: ConstraintValidator<PackageName, CharSequence> {
    private var max: Int = 64
    override fun initialize(constraintAnnotation: PackageName?) {
      super.initialize(constraintAnnotation)
      constraintAnnotation?.let { max = it.maxSize }
    }

    /**
     * The magic ;)
     */
    override fun isValid(value: CharSequence?, context: ConstraintValidatorContext?): Boolean {
      return !(value.isNullOrBlank() || value.length > max || !value.matches(Regex("[A-Za-z0-9_\\\\.]+")))
    }
  }
}
