package org.duangsuse.geekapk.api

import org.duangsuse.geekapk.entity.Category
import org.duangsuse.geekapk.repositories.CategoryRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTests {
  @Autowired
  private lateinit var mock: MockMvc

  @Autowired
  private lateinit var categories: CategoryRepository

  val utils = Category.makeNew("Utils")
  val ime = Category.makeNew("System/IME")

  @Before
  fun setUpDataLinks() {
    categories.save(utils)
    categories.save(ime)

    assert (categories.count() == 2L) { "Bad initialized count ${categories.count()}" }
  }

  @Test
  fun hasApiHint() {
    mock.perform(get("/category/"))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.['categoryName(id) -> plain']").value("http://127.0.0.1:80/category/{id}"))
  }

  @Test
  fun giveAll() {
    mock.perform(get("/category/all"))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$[0]").value(utils.name))
      .andExpect(jsonPath("$[1]").value(ime.name))
  }

  @Test
  fun giveByName() {
    mock.perform(get("/category/0"))
      .andExpect(status().isOk)
      .andExpect {
        assert (it.response.contentAsString == utils.name)
      }
  }

  @Test
  fun errorIfNotExists() {}
}
