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

/**
 * GeekApk application comments
 */
@StandaloneEntity("comment")
@Entity
@Table(name = "comments")
data class Comment (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: CommentId = 1,

  /**
   * User whose published this comment
   */
  @LinkTo("user", rel = Relation.BELONGING)
  val author: UserId,

  /**
   * Linked app (belonging to)
   */
  @LinkTo("app", rel = Relation.BELONGING)
  val app: AppId,

  /**
   * Maybe replies to comment in the same app
   */
  @LinkTo("comment", rel = Relation.BELONGING)
  val replies: CommentId?,

  /**
   * It's content markdown text
   */
  @Size(message = "comment too long (at most 6k characters)", min = 0, max = 6000)
  @Markdown @Lob @Nls var content: String = "",

  /**
   * Created at timestamp
   */
  @Temporal(TemporalType.TIMESTAMP)
  val createdAt: Date = Date(),

  /**
   * Modified by owner at timestamp
   */
  @Temporal(TemporalType.TIMESTAMP)
  var updatedAt: Date = Date(),

  /* Weak field */
  /**
   * Comment replies number
   */
  @CounterFor("comment")
  var repliesCount: Long = 0
): Serializable
