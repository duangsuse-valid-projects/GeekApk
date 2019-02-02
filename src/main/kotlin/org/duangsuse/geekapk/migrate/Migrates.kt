package org.duangsuse.geekapk.migrate

import org.duangsuse.geekapk.repositories.*
import org.duangsuse.geekapk.repositories.relations.CollabRelRepository
import org.duangsuse.geekapk.repositories.relations.FollowRelRepository
import org.duangsuse.geekapk.repositories.relations.StarRelRepository

typealias MigrationRepositoriesA = Triple<AppRepository, AppUpdateRepository, CommentRepository>
typealias MigrationRepositoriesB = Triple<UserRepository, TimelineRepository, NotificationRepository>

typealias MigrationRepositoriesC = Pair<CategoryRepository, RelationMigrationRepositories>

typealias RelationMigrationRepositories = Triple<CollabRelRepository, FollowRelRepository, StarRelRepository>

// TODO later implement binary migrate (data dump) program
class Migrates {
  class Migration(val a: MigrationRepositoriesA, val b: MigrationRepositoriesB, val c: MigrationRepositoriesC) {
    fun dump() {
      TODO("Dump to file")
    }
  }

  class RelationsMigration(val data: RelationMigrationRepositories) {
    fun dump() {
      TODO("Dump to file")
    }
  }

  companion object {
    fun dump(@Suppress("unused") migration: Migration) {
      TODO("Dump to file")
    }
  }
}
