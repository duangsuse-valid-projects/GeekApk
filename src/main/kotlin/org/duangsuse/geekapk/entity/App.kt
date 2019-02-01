package org.duangsuse.geekapk.entity

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.CategoryId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.annotation.*
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import java.io.Serializable
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Size

/**
 * GeekApk Application
 *
 * belonging to user
 * linked with category
 *
 * has name, packageName(unique identifier in table), readme, icon, screenshots
 * created time, updated time, latest reversion index
 *
 * and counters: star, comment
 */
@Entity
@StandaloneEntity("app")
data class App (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: AppId = 0,

  /**
   * Unused SQL column (transparent internal)
   */
  @Version var version: Long = 0,

  /**
   * Package name for the application, unique in the table
   */
  @Size(message = "unexpected package name size, expected size in [0, 64)", min = 1, max = 64)
  var packageName: String = "(unset)",

  /**
   * User owning this application
   */
  @LinkTo("user", rel = Relation.BELONGING)
  var author: UserId,
  /**
   * Category which this application is belonging to
   */
  @LinkTo("category", rel = Relation.BELONGING)
  var category: CategoryId,

  /**
   * Simple name may containing searchable AppAttributes (attr=value)
   */
  @Size(message = "expected application name size to be in (0, 100)", min = 0, max = 100)
  @Nls var name: String = "App",

  /**
   * Markdown readme text
   */
  @Size(message = "expected readme text size to be in (0, 32768)", min = 0, max = 32768)
  @Markdown @Nls var readme: String = "",

  /**
   * Application icon
   */
  @Size(message = "~ ..500", min = 0, max = 500)
  @NonNls var icon: String = "",

  /**
   * Application screenshot URLs text may containing AppType (type=typeName) and other attributes which are not searchable
   */
  @Size(message = "~ ..1000", min = 0, max = 1000)
  @NonNls var screenshots: String = "(hasShots=false)(reasonText=:()",

  /**
   * Creation date
   */
  @Temporal(TemporalType.TIMESTAMP)
  val createdAt: Date = Date(),
  /**
   * Last updated (published pairing reversion or metadata)
   */
  @Temporal(TemporalType.TIMESTAMP)
  var updatedAt: Date = Date(),

  /* Weak fields */
  /**
   * Last reversion
   */
  var latestReversion: Int = 1,

  /* Counters */
  @CounterFor("star")
  var starsCount: Int = 0,
  @CounterFor("comment")
  var commentsCount: Int = 0
): Serializable
