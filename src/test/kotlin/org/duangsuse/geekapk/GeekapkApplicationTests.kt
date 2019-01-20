package org.duangsuse.geekapk

import org.duangsuse.geekapk.entity.GeekUser
import org.duangsuse.geekapk.helpers.loopFor
import org.duangsuse.geekapk.helpers.times
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class GeekApkApplicationTests {
  @Test
  fun contextLoads() = Unit

  @Test
  fun generatesSharedHash() {
    assert(GeekUser.makeSharedHash(0).isEmpty())
    assert(GeekUser.makeSharedHash(1).length == 1)
    val check = GeekUser.makeSharedHash(100)
    assert(check.toSet().size > 5)
  }

  @Test
  fun loopForAndTimesExtensionWorks() {
    var n = 0
    val f = fun (x : Int) { n += x }
    let { f loopFor 5.times() }
    assert(n == 5 + 4 + 3 + 2 + 1)
  }
}
