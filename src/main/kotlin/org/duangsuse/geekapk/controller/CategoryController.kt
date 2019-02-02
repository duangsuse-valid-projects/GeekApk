package org.duangsuse.geekapk.controller

import org.duangsuse.geekapk.CategoryId
import org.duangsuse.geekapk.entity.Category
import org.duangsuse.geekapk.helper.ApiDoc
import org.duangsuse.geekapk.repositories.CategoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("category")
class CategoryController {
  @Autowired
  private lateinit var categories: CategoryRepository

  @GetMapping
  @ResponseBody fun apiHint(hsr: HttpServletRequest) = ApiDoc.category(hsr).second

  /**
   * Read all categories
   */
  @GetMapping("all")
  @ResponseBody
  fun allCategories(): Iterable<Category> {
    return categories.findAll()
  }

  /**
   * Read category name by id
   */
  @GetMapping("{id}")
  @ResponseBody
  fun categoryName(hsr: HttpServletResponse, @PathVariable("id") id: CategoryId): String {
    if (categories.existsById(id))
      return categories.findById(id).get().name

    hsr.status = HttpStatus.NOT_FOUND.value()
    return "NoDef"
  }
}
