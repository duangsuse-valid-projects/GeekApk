package org.duangsuse.geekapk.repositories

import org.duangsuse.geekapk.CategoryId
import org.duangsuse.geekapk.entity.Category
import org.springframework.data.repository.CrudRepository

interface CategoryRepository : CrudRepository<Category, CategoryId>
