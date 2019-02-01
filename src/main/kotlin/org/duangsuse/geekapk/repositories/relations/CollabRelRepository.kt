package org.duangsuse.geekapk.repositories.relations

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.CollabRelId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.relations.RelationUserAppCollab
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface CollabRelRepository : CrudRepository<RelationUserAppCollab, CollabRelId> {
  @Query("SELECT x.app FROM #{#entityName} x WHERE x.collaborator = ?1")
  fun allHasCollaborator(uid: UserId): MutableList<AppId>
  @Query("SELECT x.collaborator FROM #{#entityName} x WHERE x.app = ?1")
  fun allCollaboratedUsers(aid: AppId): MutableList<UserId>
}
