package org.duangsuse.geekapk.entity

import org.duangsuse.geekapk.CategoryId
import org.duangsuse.geekapk.annotations.StandaloneEntity
import org.jetbrains.annotations.Nls
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.Size

@StandaloneEntity("category")
@Entity
data class Category (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val tid: CategoryId = 0,

  @Version var version: Long = 0,

  @Size(message = "category name too long (~ ..60)", min = 0, max = 60)
  @Nls var name: String = "(none)"
): Serializable
