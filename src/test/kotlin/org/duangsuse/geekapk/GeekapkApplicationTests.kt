package org.duangsuse.geekapk

import org.duangsuse.geekapk.entity.GeekUser
import org.duangsuse.geekapk.helpers.loopFor
import org.duangsuse.geekapk.helpers.times
import org.hamcrest.number.IsCloseTo
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class GeekApkHelperTests {
  @Test
  fun contextLoads() = Unit

  /**
   * Checks for the shared key generator
   */
  @Test
  fun generatesSharedHash() {
    assert(GeekUser.makeSharedHash(0).isEmpty()) { "sharedHash generator not handling empty request" }
    assert(GeekUser.makeSharedHash(1).length == 1) { "sharedHash generator not giving right length" }
    val check = GeekUser.makeSharedHash(100)
    Assert.assertThat("not randomized", check.toSet().size.toDouble(), IsCloseTo(50.0, 40.0))
  }

  /**
   * Check for the f**king damn fake DSL
   */
  @Test
  fun loopForAndTimesExtensionWorks() {
    var n = 0
    val f = fun (x : Int) { n += x }
    let { f loopFor 5.times() }
    Assert.assertEquals(n, 5 + 4 + 3 + 2 + 1)
  }
}
