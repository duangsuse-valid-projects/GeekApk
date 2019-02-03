package org.duangsuse.geekapk.entity

import org.duangsuse.geekapk.CategoryId
import org.duangsuse.geekapk.annotation.StandaloneEntity
import org.jetbrains.annotations.Nls
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.Size

/**
 * GeekApk application categories
 */
@StandaloneEntity("category")
@Table(name = "categories")
@Entity
data class Category (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: CategoryId = 0,

  /**
   * transparent version record
   */
  @Version var version: Long = 0,

  /**
   * Category name, maybe hierarchical using '/' character
   */
  @Size(message = "category name too long (~ ..60)", min = 0, max = 60)
  @Nls var name: String = "(none)"
): Serializable {
  companion object {
    fun makeNew(name: String) = Category(name = name)
  }
}
