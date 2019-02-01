package org.duangsuse.geekapk.entity

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.CommentId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.annotation.*
import org.jetbrains.annotations.Nls
import java.io.Serializable
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Size

@StandaloneEntity("comment")
@Entity
data class Comment (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: CommentId = 1,

  @LinkTo("user", rel = Relation.BELONGING)
  val author: UserId,
  @LinkTo("app", rel = Relation.BELONGING)
  val app: AppId,
  @LinkTo("comment", rel = Relation.BELONGING)
  val replies: CommentId?,

  @Size(message = "comment too long (at most 6k characters)", min = 0, max = 6000)
  @Markdown @Nls var content: String = "",

  @Temporal(TemporalType.TIMESTAMP)
  val createdAt: Date = Date(),
  @Temporal(TemporalType.TIMESTAMP)
  var updatedAt: Date = Date(),

  /* Weak field */
  @CounterFor("comment")
  var repliesCount: Long = 0
): Serializable
