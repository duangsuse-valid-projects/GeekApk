package org.duangsuse.geekapk.repositories.relations

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.StarRelId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.relations.RelationUserAppStar
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface StarRelRepository : CrudRepository<RelationUserAppStar, StarRelId> {
  @Query("SELECT x.starred FROM #{#entityName} x WHERE x.starer = ?1")
  fun allStars(uid: UserId): MutableList<AppId>
  @Query("SELECT x.starer FROM #{#entityName} x WHERE x.starred = ?1")
  fun allStargazers(appId: AppId): MutableList<UserId>
}
