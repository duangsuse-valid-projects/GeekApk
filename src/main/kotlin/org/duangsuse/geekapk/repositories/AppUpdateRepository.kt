package org.duangsuse.geekapk.repositories

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.AppUpdateId
import org.duangsuse.geekapk.entity.AppUpdate
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface AppUpdateRepository : CrudRepository<AppUpdate, AppUpdateId> {
  @Query("SELECT x FROM #{#name} x WHERE x.forApp = ?1")
  fun allForApp(appId: AppId): MutableList<AppUpdate>
}
