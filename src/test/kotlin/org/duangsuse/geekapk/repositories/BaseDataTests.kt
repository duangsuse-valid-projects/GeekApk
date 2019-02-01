package org.duangsuse.geekapk.repositories

import org.duangsuse.geekapk.entity.GeekUser
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class BaseDataTests {
  @Autowired lateinit var apps: AppRepository
  @Autowired lateinit var appUps: AppUpdateRepository
  @Autowired lateinit var categories: CategoryRepository
  @Autowired lateinit var comments: CommentRepository
  @Autowired lateinit var notifications: NotificationRepository
  @Autowired lateinit var timeline: TimelineRepository
  @Autowired lateinit var users: UserRepository

  @Test
  fun contextLoads() = Unit

  @Test
  fun userInitializer() {
    val u0 = GeekUser.makeUser("duangsuse")
    val u1 = GeekUser.makeUser("Trumpet")

    assert(u0.username != u1.username)
    assert(u0.hash != u1.hash)
  }

  @Before
  fun prologue() {}

  @After
  fun epilogue() {}
}
