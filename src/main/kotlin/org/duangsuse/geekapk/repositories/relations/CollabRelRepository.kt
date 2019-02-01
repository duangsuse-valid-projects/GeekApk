package org.duangsuse.geekapk.repositories.relations

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.CollabRelId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.relations.RelationUserAppCollab
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface CollabRelRepository : CrudRepository<RelationUserAppCollab, CollabRelId> {
  @Query("SELECT x.app FROM #{#name} x WHERE x.collaborator = ?1")
  fun allHasCollab(collabUid: UserId): MutableList<AppId>
  @Query("SELECT x.collaborator FROM #{#name} x WHERE x.app = ?1")
  fun allCollabUsers(appId: AppId): MutableList<UserId>
}
