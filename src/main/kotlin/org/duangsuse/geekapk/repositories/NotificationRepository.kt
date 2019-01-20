package org.duangsuse.geekapk.repositories

import org.duangsuse.geekapk.NotificationId
import org.duangsuse.geekapk.entity.Notification
import org.springframework.data.repository.CrudRepository

interface NotificationRepository : CrudRepository<Notification, NotificationId>
