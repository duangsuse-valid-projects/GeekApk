package org.duangsuse.geekapk.api

import org.duangsuse.geekapk.controller.CategoryController
import org.duangsuse.geekapk.repositories.CategoryRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@WebMvcTest(CategoryController::class)
class CategoryControllerTests {
  @Autowired
  private lateinit var mock: MockMvc

  @Autowired @MockBean
  private lateinit var categories: CategoryRepository

  @Test
  fun hasApiHint() {
    mock.perform(get("/"))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.['categoryName(id) -> plain']").value("http://127.0.0.1:80/category/{id}"))
  }
}
