package org.duangsuse.geekapk.entity

import org.duangsuse.geekapk.CategoryId
import org.duangsuse.geekapk.annotations.StandaloneEntity
import org.jetbrains.annotations.Nls
import javax.persistence.*

@StandaloneEntity("category")
@Entity
data class Category (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val tid: CategoryId = 0,

  @Version var version: Long = 0,

  @Nls var name: String = "(none)"
)
