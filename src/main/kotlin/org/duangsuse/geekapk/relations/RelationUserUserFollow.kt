package org.duangsuse.geekapk.relations

import org.duangsuse.geekapk.FollowRelId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.annotation.JustRelation
import org.duangsuse.geekapk.annotation.LinkTo
import org.duangsuse.geekapk.annotation.Relation
import java.io.Serializable
import javax.persistence.*

/**
 * Follow relation between user and user
 */
@JustRelation("user", "user", rel = "user >`following` ~ `followed`< user")
@Table(name = "rel_user_user_follow")
@Entity
data class RelationUserUserFollow (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: FollowRelId = 0,

  @LinkTo("user", Relation.EQUIVALENT)
  val follower: UserId,
  @LinkTo("user", Relation.EQUIVALENT)
  val followee: UserId
): Serializable
