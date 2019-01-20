package org.duangsuse.geekapk.entity

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.CategoryId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.annotations.CounterFor
import org.duangsuse.geekapk.annotations.LinkTo
import org.duangsuse.geekapk.annotations.Relation
import org.duangsuse.geekapk.annotations.StandaloneEntity
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import java.util.Date
import javax.persistence.*

@Entity
@StandaloneEntity("app")
data class App (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val aid: AppId = 0,

  @Version var version: Long = 0,

  var packageName: String = "(unset)",

  @LinkTo("user", rel = Relation.BELONGING)
  var author: UserId,
  @LinkTo("category", rel = Relation.BELONGING)
  var category: CategoryId,

  @Nls var name: String = "App",
  @Nls var readme: String = "",
  @NonNls var icon: String = "",
  @NonNls var screenshots: String = "",

  val createdAt: Date = Date(),
  var updatedAt: Date = Date(),

  /* Weak fields */
  var latestReversion: Int = 1,

  @CounterFor("star")
  var starsCount: Int = 0,
  @CounterFor("comment")
  var commentsCount: Int = 0
)
