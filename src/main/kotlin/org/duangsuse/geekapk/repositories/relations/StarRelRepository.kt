package org.duangsuse.geekapk.repositories.relations

import org.duangsuse.geekapk.relations.RelationUserAppStar
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface StarRelRepository : CrudRepository<RelationUserAppStar, Long> {
  @Query("SELECT x.starred FROM #{#entityName} x WHERE x.starer = ?1")
  fun allUserStars(uid: Int): MutableList<Long>
  @Query("SELECT x.starer FROM #{#entityName} x WHERE x.starred = ?1")
  fun allStaredUsers(appId: Long): MutableList<Int>
}
