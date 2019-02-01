package org.duangsuse.geekapk.entity

import org.duangsuse.geekapk.TimelineId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.annotation.Appendage
import org.duangsuse.geekapk.annotation.LinkTo
import org.duangsuse.geekapk.annotation.Relation
import java.io.Serializable
import java.util.*
import javax.persistence.*

/**
 * A user's public timeline
 */
@Appendage("user")
@Entity
data class Timeline (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: TimelineId = 0,

  /**
   * Published user
   */
  @LinkTo("user", rel = Relation.BELONGING)
  val owner: UserId,

  /**
   * Timeline type
   */
  val type: Int = JUST_FORWARD_WIDEN_CID,
  /**
   * Associated data
   */
  val data: Int = 0,

  /**
   * Created time
   */
  @Temporal(TemporalType.TIMESTAMP)
  val createdAt: Date = Date()
): Serializable {
  companion object {
    const val NEW_FOLLOW = 0
    const val NEW_APP = 1
    const val NEW_APP_UPDATE = 2
    const val NEW_REPLY = 3
    const val DELETED_APP = 4
    const val DELETED_APP_UPDATE = 5
    const val DELETED_REPLY = 6
    const val UPDATED_BIO = 7
    const val UPDATED_NAME = 8
    const val UPDATED_USERNAME = 9
    const val COLLABORATE_APP = 10
    const val NEW_STAR = 11
    const val JUST_FORWARD_WIDEN_CID = 1000
  }
}
