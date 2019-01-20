package org.duangsuse.geekapk.repositories

import org.duangsuse.geekapk.entity.Comment
import org.springframework.data.repository.CrudRepository

interface CommentRepository : CrudRepository<Comment, Long>