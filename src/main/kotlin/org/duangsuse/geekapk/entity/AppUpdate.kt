package org.duangsuse.geekapk.entity

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.AppUpdateId
import org.duangsuse.geekapk.annotations.Appendage
import org.duangsuse.geekapk.annotations.LinkTo
import org.duangsuse.geekapk.annotations.Relation
import org.jetbrains.annotations.Nls
import java.util.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Appendage("app")
@Entity
class AppUpdate (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: AppUpdateId = 0,

  @LinkTo("app", rel = Relation.BELONGING)
  val forApp: AppId,

  var reversion: Int = 1,
  var version: String = "v0.1.0",

  @Nls var updates: String = "(none)",

  var minSdk: Int = 14,

  var installLink: String = "no-source:to-be-filled",

  val createdAt: Date = Date()
)
