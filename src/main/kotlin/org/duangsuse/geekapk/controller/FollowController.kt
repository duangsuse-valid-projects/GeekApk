package org.duangsuse.geekapk.controller

import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.entity.GeekUser
import org.duangsuse.geekapk.helper.ApiDoc
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("follow")
class FollowController {
  @GetMapping
  @ResponseBody fun apiHint(hsr: HttpServletRequest) = ApiDoc.follow(hsr).second

  @PostMapping("/{uid}")
  @ResponseBody
  fun follow(@PathVariable("uid") uid: UserId): Map<String, Int> /* oldCount: number *//* newCount: number */ {
    TODO()
  }


  @DeleteMapping("/{uid}")
  @ResponseBody
  fun unfollow(@PathVariable("uid") uid: UserId): Map<String, Int> /* oldCount: number *//* newCount: number */ {
    TODO()
  }


  @GetMapping("/followers/{uid}")
  @ResponseBody
  fun followers(@PathVariable("uid") uid: UserId): List<GeekUser> {
    TODO()
  }


  @GetMapping("/{uid}")
  @ResponseBody
  fun following(@PathVariable("uid") uid: UserId): List<GeekUser> {
    TODO()
  }
}
