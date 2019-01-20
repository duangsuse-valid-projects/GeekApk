package org.duangsuse.geekapk.repositories

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.entity.App
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface AppRepository : CrudRepository<App, AppId> {
  @Query("SELECT x FROM #{#entityName} x ORDER BY x.updatedAt")
  fun allByUpdated(): MutableList<App>
  @Query("SELECT x FROM #{#entityName} x ORDER BY x.createdAt")
  fun allByCreated(): MutableList<App>
  @Query("SELECT x FROM #{#entityName} x ORDER BY x.commentsCount")
  fun allByComments(): MutableList<App>
  @Query("SELECT x FROM #{#entityName} x ORDER BY x.starsCount")
  fun allByStars(): MutableList<App>

  @Query("SELECT x FROM #{#entityName} x WHERE x.category = ?1 ORDER BY x.updatedAt")
  fun allInCategory(categoryId: Int): MutableList<App>

  @Query("SELECT x FROM #{#entityName} x WHERE x.category = ?1 ORDER BY x.starsCount")
  fun allInCategoryStars(categoryId: Int): MutableList<App>

  @Query("SELECT x FROM #{#entityName} x WHERE x.category = ?1 ORDER BY x.commentsCount")
  fun allInCategoryComments(categoryId: Int): MutableList<App>

  @Query("SELECT x FROM #{#entityName} x WHERE x.name LIKE %?1% ORDER BY x.updatedAt")
  fun searchForName(content: String): MutableList<App>

  @Query("SELECT x FROM #{#entityName} x WHERE x.name LIKE %?1% ORDER BY x.starsCount")
  fun searchForNameStars(content: String): MutableList<App>

  @Query("SELECT x FROM #{#entityName} x WHERE x.packageName LIKE %?1% ORDER BY x.updatedAt")
  fun searchForPackage(content: String): MutableList<App>

  @Query("SELECT x FROM #{#entityName} x WHERE x.readme LIKE %?1% ORDER BY x.updatedAt")
  fun searchForReadme(content: String): MutableList<App>
}
