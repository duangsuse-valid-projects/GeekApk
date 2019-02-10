
import org.duangsuse.geekapk.constraint.SimpleName
import org.junit.Assert
import org.junit.Test

class SimpleNameValidatorTests {
  @Test
  fun acceptsValidNames() {
    val instance = SimpleName.Validator()

    Assert.assertTrue(instance.isValid("duangsuse", null))
    Assert.assertTrue(instance.isValid("_geek", null))
    Assert.assertTrue(instance.isValid("AbcQ", null))
    Assert.assertTrue(instance.isValid("_adc", null))
    Assert.assertTrue(instance.isValid("_ab12324-foo-bar___A", null))
  }

  @Test
  fun rejectsInvalidNames() {
    val instance = SimpleName.Validator()
    val wrong = setOf("_", "a","A", "B-", "A0", "-", "999", "666", "-A", "AK汗", "space ful")

    wrong.forEach tries@ {
      name ->
      runCatching { if (instance.isValid(name, null)) throw RuntimeException() }
        //.onSuccess { throw AssertionError("$name is not a valid simpleName") }
    }

    Assert.assertTrue(wrong.none { instance.isValid(it, null) })
  }
}
