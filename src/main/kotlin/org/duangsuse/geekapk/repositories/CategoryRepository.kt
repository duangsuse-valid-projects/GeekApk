package org.duangsuse.geekapk.repositories

import org.duangsuse.geekapk.CategoryId
import org.duangsuse.geekapk.entity.Category
import org.springframework.data.repository.CrudRepository
import java.util.*

interface CategoryRepository : CrudRepository<Category, CategoryId> {
  override fun findAll(): MutableIterable<Category>
  override fun existsById(id: CategoryId): Boolean
  override fun findById(id: CategoryId): Optional<Category>
}
