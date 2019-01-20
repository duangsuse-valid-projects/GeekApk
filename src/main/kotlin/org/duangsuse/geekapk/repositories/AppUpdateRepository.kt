package org.duangsuse.geekapk.repositories

import org.duangsuse.geekapk.entity.AppUpdate
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface AppUpdateRepository : CrudRepository<AppUpdate, Long> {
  @Query("SELECT x FROM #{#entityName} x WHERE x.forApp = ?1")
  fun allForApp(appId: Long): MutableList<AppUpdate>
}
