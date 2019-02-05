package org.duangsuse.geekapk.controller

import org.duangsuse.geekapk.TimelineSize
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.entity.Timeline
import org.duangsuse.geekapk.helper.ApiDoc
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("timeline")
class TimelineController {
  @GetMapping
  @ResponseBody
  fun apiHint(hsr: HttpServletRequest) = ApiDoc.timeline(hsr).second

  @GetMapping("/{uid}")
  @ResponseBody
  fun readUserTimeline(@PathVariable("uid") uid: UserId, @RequestParam(name = "type", required = false) type: Int?, @RequestParam(name = "sliceFrom", required = false) sliceFrom: TimelineSize?, @RequestParam(name = "sliceTo", required = false) sliceTo: TimelineSize?): List<Timeline> {
    TODO()
  }


  @GetMapping("/all")
  @ResponseBody
  fun readAllTimeline(@RequestParam(name = "type", required = false) type: Int?, @RequestParam(name = "sliceFrom", required = false) sliceFrom: TimelineSize?, @RequestParam(name = "sliceTo", required = false) sliceTo: TimelineSize?): List<Timeline> {
    TODO()
  }


  @GetMapping("/bulk/{uids}")
  @ResponseBody
  fun bulkReadUserTimeline(@PathVariable("uids") uids: String, @RequestParam(name = "type", required = false) type: Int?, @RequestParam(name = "sliceFrom", required = false) sliceFrom: TimelineSize?, @RequestParam(name = "sliceTo", required = false) sliceTo: TimelineSize?): List<Timeline> {
    TODO()
  }


  @GetMapping("/check/{uid}")
  @ResponseBody
  fun getUserTimelineCount(@PathVariable("uid") uid: UserId): Int {
    TODO()
  }


  @GetMapping("/check/{uids}")
  @ResponseBody
  fun getBulkUserTimelineCount(@PathVariable("uids") uids: String): Int {
    TODO()
  }
}
