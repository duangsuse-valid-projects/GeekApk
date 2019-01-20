package org.duangsuse.geekapk.repositories

import org.duangsuse.geekapk.entity.Category
import org.springframework.data.repository.CrudRepository

interface CategoryRepository : CrudRepository<Category, Int>