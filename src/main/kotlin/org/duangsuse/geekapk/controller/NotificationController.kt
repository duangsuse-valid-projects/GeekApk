package org.duangsuse.geekapk.controller

import org.duangsuse.geekapk.NotificationSize
import org.duangsuse.geekapk.entity.Notification
import org.duangsuse.geekapk.helper.ApiDoc
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("notification")
class NotificationController {
  @GetMapping
  @ResponseBody fun apiHint(hsr: HttpServletRequest) = ApiDoc.notification(hsr).second

  @GetMapping("/notification/active")
  @ResponseBody
  fun readMineNotifications(): List<Notification> {
    TODO()
  }


  @GetMapping("/notification/all")
  @ResponseBody
  fun readAllMineNotifications(@RequestParam(name = "sliceFrom", required = false) sliceFrom:NotificationSize?, @RequestParam(name = "sliceTo", required = false) sliceTo:NotificationSize?): List<Notification> {
    TODO()
  }


  @GetMapping("/notification/mark")
  @ResponseBody
  fun markNotifications(@RequestParam("start") start: NotificationSize, @RequestParam(name = "end", required = false) end: NotificationSize?, @RequestParam("stat") stat: String/* Maybe active or inactive */): Int {
    TODO()
  }


  @GetMapping("/notification/count")
  @ResponseBody
  fun getNotificationCount(): Int {
    TODO()
  }
}
