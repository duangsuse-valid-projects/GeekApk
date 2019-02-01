package org.duangsuse.geekapk.repositories.relations

import org.duangsuse.geekapk.FollowRelId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.relations.RelationUserUserFollow
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface FollowRelRepository : CrudRepository<RelationUserUserFollow, FollowRelId> {
  @Query("SELECT x.follower FROM #{#entityName} x WHERE x.followee = ?1")
  fun allFollowers(uid: UserId): MutableList<UserId>
  @Query("SELECT x.followee FROM #{#entityName} x WHERE x.follower = ?1")
  fun allFollowing(uid: UserId): MutableList<UserId>
}
