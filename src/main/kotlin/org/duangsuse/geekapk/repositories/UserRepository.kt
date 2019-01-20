package org.duangsuse.geekapk.repositories

import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.entity.GeekUser
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<GeekUser, UserId>
