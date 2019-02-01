package org.duangsuse.geekapk.entity

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.AppUpdateId
import org.duangsuse.geekapk.annotation.Appendage
import org.duangsuse.geekapk.annotation.LinkTo
import org.duangsuse.geekapk.annotation.Markdown
import org.duangsuse.geekapk.annotation.Relation
import org.jetbrains.annotations.Nls
import java.io.Serializable
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Size

/**
 * GeekApk Application reversion
 *
 * belonging to app
 *
 * reversion field is unique to forApp
 *
 * has unique *reversion*, version name, minimal SDK version, install link, created date fields
 */
@Appendage("app")
@Table(name = "app_reversions")
@Entity
data class AppUpdate (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: AppUpdateId = 0,

  /**
   * Owning geekapk application
   */
  @LinkTo("app", rel = Relation.BELONGING)
  val forApp: AppId,

  /**
   * Reversion id, unique to one application
   */
  var reversion: Int = 1,

  /**
   * Application version name
   */
  @Size(message = "version name size should be in [0, 64)", min = 1, max = 64)
  var version: String = "v0.1.0",

  /**
   * Updates description document
   */
  @Size(message = "update log too large or short (~ 0..32768)", min = 0, max = 1024 * 16 * 2)
  @Markdown @Nls var updates: String = "(none)",

  /**
   * Minimal Android Runtime version required to parse the package
   */
  var minSdk: Int = 14,

  /**
   * GeekLink to perform install operation
   */
  @Size(message = "install url length should be in [0, 32768)", min = 1, max = 1024 * 16 * 2)
  var installLink: String = "no-source:to-be-filled",

  /**
   * Creation time for this update
   */
  @Temporal(TemporalType.TIMESTAMP)
  val createdAt: Date = Date()
): Serializable
