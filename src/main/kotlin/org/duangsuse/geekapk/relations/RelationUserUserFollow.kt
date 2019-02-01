package org.duangsuse.geekapk.relations

import org.duangsuse.geekapk.FollowRelId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.annotations.JustRelation
import org.duangsuse.geekapk.annotations.LinkTo
import org.duangsuse.geekapk.annotations.Relation
import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@JustRelation("user", "user", rel = "user >`following` ~ `followed`< user")
@Entity
data class RelationUserUserFollow (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: FollowRelId = 0,

  @LinkTo("user", Relation.EQUIVALENT)
  val follower: UserId,
  @LinkTo("user", Relation.EQUIVALENT)
  val followee: UserId
): Serializable
