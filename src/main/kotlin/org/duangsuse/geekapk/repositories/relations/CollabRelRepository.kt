package org.duangsuse.geekapk.repositories.relations

import org.duangsuse.geekapk.relations.RelationUserAppCollab
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface CollabRelRepository : CrudRepository<RelationUserAppCollab, Int> {
  @Query("SELECT x.app FROM #{#entityName} x WHERE x.collaborator = ?1")
  fun allHasCollab(collabUid: Int): MutableList<Long>
  @Query("SELECT x.collaborator FROM #{#entityName} x WHERE x.app = ?1")
  fun allCollabUsers(appId: Long): MutableList<Int>
}
