package org.duangsuse.geekapk.relations

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.StarRelId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.annotation.JustRelation
import org.duangsuse.geekapk.annotation.LinkTo
import org.duangsuse.geekapk.annotation.Relation
import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@JustRelation("user", "app", rel = "user >`starred` ~ `has stargazer`< app")
@Entity
data class RelationUserAppStar (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: StarRelId = 0,

  @LinkTo("user", Relation.EQUIVALENT)
  val starer: UserId,
  @LinkTo("app", Relation.EQUIVALENT)
  val starred: AppId
): Serializable
