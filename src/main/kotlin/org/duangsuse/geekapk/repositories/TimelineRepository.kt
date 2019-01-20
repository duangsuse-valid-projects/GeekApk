package org.duangsuse.geekapk.repositories

import org.duangsuse.geekapk.TimelineId
import org.duangsuse.geekapk.entity.Timeline
import org.springframework.data.repository.CrudRepository

interface TimelineRepository : CrudRepository<Timeline, TimelineId>
