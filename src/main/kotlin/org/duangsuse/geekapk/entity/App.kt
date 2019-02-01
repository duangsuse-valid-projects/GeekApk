package org.duangsuse.geekapk.entity

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.CategoryId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.annotations.*
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import java.io.Serializable
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@StandaloneEntity("app")
data class App (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val aid: AppId = 0,

  @Version var version: Long = 0,

  @Size(message = "unexpected package name size, expected size in [0, 64)", min = 1, max = 64)
  var packageName: String = "(unset)",

  @LinkTo("user", rel = Relation.BELONGING)
  var author: UserId,
  @LinkTo("category", rel = Relation.BELONGING)
  var category: CategoryId,

  @Size(message = "expected application name size to be in (0, 100)", min = 0, max = 100)
  @Nls var name: String = "App",

  @Size(message = "expected readme text size to be in (0, 32768)", min = 0, max = 32768)
  @Markdown @Nls var readme: String = "",

  @Size(message = "~ ..500", min = 0, max = 500)
  @NonNls var icon: String = "",
  @Size(message = "~ ..1000", min = 0, max = 1000)
  @NonNls var screenshots: String = "",

  val createdAt: Date = Date(),
  var updatedAt: Date = Date(),

  /* Weak fields */
  var latestReversion: Int = 1,

  @CounterFor("star")
  var starsCount: Int = 0,
  @CounterFor("comment")
  var commentsCount: Int = 0
): Serializable
