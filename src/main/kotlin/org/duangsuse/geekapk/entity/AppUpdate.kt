package org.duangsuse.geekapk.entity

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.AppUpdateId
import org.duangsuse.geekapk.annotations.Appendage
import org.duangsuse.geekapk.annotations.LinkTo
import org.duangsuse.geekapk.annotations.Markdown
import org.duangsuse.geekapk.annotations.Relation
import org.jetbrains.annotations.Nls
import java.io.Serializable
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.Size

@Appendage("app")
@Entity
data class AppUpdate (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: AppUpdateId = 0,

  @LinkTo("app", rel = Relation.BELONGING)
  val forApp: AppId,

  var reversion: Int = 1,
  @Size(message = "version name size should be in [0, 64)", min = 1, max = 64)
  var version: String = "v0.1.0",

  @Size(message = "update log too large or short (~ 0..32768)", min = 0, max = 1024 * 16 * 2)
  @Markdown @Nls var updates: String = "(none)",

  var minSdk: Int = 14,

  @Size(message = "install url length should be in [0, 32768)", min = 1, max = 1024 * 16 * 2)
  var installLink: String = "no-source:to-be-filled",

  val createdAt: Date = Date()
): Serializable
