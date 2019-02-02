package org.duangsuse.geekapk.repositories.relations

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

/**
 * Relation-only repositories tests
 */
@RunWith(SpringRunner::class)
@SpringBootTest
class CollabAndStarAndFollowTests {
  @Autowired lateinit var clr: CollabRelRepository
  @Autowired lateinit var asr: StarRelRepository
  @Autowired lateinit var ufr: FollowRelRepository

  fun setUpCollab() {}
  fun setUpStar() {}
  fun setUpFollow() {}

  fun dropCollab() {}
  fun dropStar() {}
  fun dropFollow() {}

  @Before /* preparation */
  fun setUp() {
    setUpCollab()
    setUpStar()
    setUpFollow()
  }

  @Test
  fun testCollab() {}

  @Test
  fun testStar() {}

  @Test
  fun testFollow() {}

  @After /* do clean-ups */
  fun rollBack() {
    dropFollow()
    dropStar()
    dropCollab()
  }
}
