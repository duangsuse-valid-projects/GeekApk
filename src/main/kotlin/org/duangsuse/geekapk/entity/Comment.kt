package org.duangsuse.geekapk.entity

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.CommentId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.annotations.CounterFor
import org.duangsuse.geekapk.annotations.LinkTo
import org.duangsuse.geekapk.annotations.Relation
import org.duangsuse.geekapk.annotations.StandaloneEntity
import org.jetbrains.annotations.Nls
import java.util.Date

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@StandaloneEntity("comment")
@Entity
data class Comment (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val cid: CommentId = 1,

  @LinkTo("user", rel = Relation.BELONGING)
  val author: UserId,
  @LinkTo("app", rel = Relation.BELONGING)
  val app: AppId,
  @LinkTo("comment", rel = Relation.BELONGING)
  val replies: CommentId?,

  @Nls var content: String = "",

  val createdAt: Date = Date(),
  var updatedAt: Date = Date(),

  /* Weak field */
  @CounterFor("comment")
  var repliesCount: Long = 0
)
