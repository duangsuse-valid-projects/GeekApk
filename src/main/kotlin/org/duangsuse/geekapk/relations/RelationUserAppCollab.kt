package org.duangsuse.geekapk.relations

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.CollabRelId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.annotation.JustRelation
import org.duangsuse.geekapk.annotation.LinkTo
import org.duangsuse.geekapk.annotation.Relation
import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@JustRelation("user", "app", rel = "user >`collaborate to` ~ `has collaborator`< app")
@Entity
data class RelationUserAppCollab (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: CollabRelId = 0,

  @LinkTo("user", Relation.EQUIVALENT)
  val collaborator: UserId,
  @LinkTo("app", Relation.EQUIVALENT)
  val app: AppId
): Serializable
