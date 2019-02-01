package org.duangsuse.geekapk.relations

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.StarRelId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.annotation.JustRelation
import org.duangsuse.geekapk.annotation.LinkTo
import org.duangsuse.geekapk.annotation.Relation
import java.io.Serializable
import javax.persistence.*

/**
 * Star relation between user and app
 */
@JustRelation("user", "app", rel = "user >`starred` ~ `has stargazer`< app")
@Table(name = "rel_user_app_star")
@Entity
data class RelationUserAppStar (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: StarRelId = 0,

  @LinkTo("user", Relation.EQUIVALENT)
  val starer: UserId,
  @LinkTo("app", Relation.EQUIVALENT)
  val starred: AppId
): Serializable
