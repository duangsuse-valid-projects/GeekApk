package org.duangsuse.geekapk.relations

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.CollabRelId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.annotation.JustRelation
import org.duangsuse.geekapk.annotation.LinkTo
import org.duangsuse.geekapk.annotation.Relation
import java.io.Serializable
import javax.persistence.*

/**
 * Collaborator relation between user and app
 */
@JustRelation("user", "app", rel = "user >`collaborate to` ~ `has collaborator`< app")
@Entity
@Table(name = "rel_user_app_collab")
data class RelationUserAppCollab (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: CollabRelId = 0,

  @LinkTo("user", Relation.EQUIVALENT)
  val collaborator: UserId,
  @LinkTo("app", Relation.EQUIVALENT)
  val app: AppId
): Serializable
