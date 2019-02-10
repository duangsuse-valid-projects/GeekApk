package org.duangsuse.geekapk.api

import org.duangsuse.geekapk.repositories.AppRepository
import org.duangsuse.geekapk.repositories.CategoryRepository
import org.duangsuse.geekapk.repositories.CommentRepository
import org.duangsuse.geekapk.repositories.UserRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTests {
  @Autowired
  private lateinit var mock: MockMvc

  @Autowired
  private lateinit var users: UserRepository

  @Autowired
  private lateinit var categories: CategoryRepository

  @Autowired
  private lateinit var comments: CommentRepository

  @Autowired
  private lateinit var apps: AppRepository

  @Test fun contextLoads() = Unit
}
