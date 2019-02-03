package org.duangsuse.geekapk

import org.duangsuse.geekapk.helper.getGeekApkConfigOr
import org.duangsuse.geekapk.helper.getGeekApkConfigOrNull
import org.duangsuse.geekapk.helper.mustGetGeekApkConfig
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest
@RunWith(SpringRunner::class)
class GeekApkConfigTests {
  @Before
  fun doInitialization() {
    initializeINIConfig()
  }

  @Test
  fun applicationINIInitializes() {
    Assert.assertTrue(System.getProperty("geekapk.key").isNotBlank())
  }

  @Test
  fun helperWorks() {
    getGeekApkConfigOr("bot") { throw AssertionError("Failed to get bot property") }
  }

  @Test
  fun helperOnUnsetWorks() {
    // boilerplate since JUnit version does not have @ExpectedException

    var gotException = false

    try {
      getGeekApkConfigOr("not_exists") { throw AssertionError("Expected") }
    } catch (_: java.lang.AssertionError) { gotException = true }

    Assert.assertTrue(gotException)
  }

  @Test
  fun helperNullWorks() {
    getGeekApkConfigOrNull("bot") ?: run {
      throw AssertionError("geekapk.bot should not be null")
    }
    Assert.assertNull(getGeekApkConfigOrNull("a"))
  }

  @Test
  fun helperOrStringWorks() {
    Assert.assertEquals(getGeekApkConfigOr("a", "b"), "b")
    Assert.assertNotEquals(getGeekApkConfigOr("bot", "b"), "b")
  }

  @Test
  fun mustGetWorks() {
    System.getProperties().forEach(::println)
    Assert.assertEquals(mustGetGeekApkConfig("bot"), "1")

    try { mustGetGeekApkConfig("a") }
    catch (_: IllegalStateException) { return }
    assert (false) { "MustGet returned for non-existing value a" }
  }

  @Test
  fun iniProcessorWorks() {
    val code = "def=123".toByteArray()

    parseGeekINIBuffer(code)

    Assert.assertEquals(System.getProperty("def"), "123")
  }

  fun iniProcessorSkipsComments() {
    val code = "#dfa=123".toByteArray()

    parseGeekINIBuffer(code)

    Assert.assertFalse("dfa" in System.getProperties())
  }

  fun iniProcessorHandlesSections() {
    val code = "\n\n[s]\na=1\n[]\nb=2\n[s.a]\nc=3".toByteArray()

    parseGeekINIBuffer(code)

    Assert.assertEquals(System.getProperty("s.a"), "1")
    Assert.assertEquals(System.getProperty("b"), "2")
    Assert.assertEquals(System.getProperty("s.a.c"), "3")
  }

  fun iniProcessorHandlesIllegalSet() {
    val wrongCode = "b=a=c".toByteArray()

    parseGeekINIBuffer(wrongCode)

    Assert.assertTrue("b" !in System.getProperties())
  }
}
