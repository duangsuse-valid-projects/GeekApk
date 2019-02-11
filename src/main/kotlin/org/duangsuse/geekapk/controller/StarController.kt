package org.duangsuse.geekapk.controller

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.entity.App
import org.duangsuse.geekapk.helper.ApiDoc
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("star")
class StarController {
  @GetMapping
  @ResponseBody
  fun apiHint(hsr: HttpServletRequest) = ApiDoc.star(hsr).second

  @PostMapping("/{aid}")
  @ResponseBody
  fun star(@PathVariable("aid") aid: AppId): Map<String, Int> /* oldCount: number *//* newCount: number */ {
    TODO()
  }


  @DeleteMapping("/{aid}")
  @ResponseBody
  fun unstar(@PathVariable("aid") aid: AppId): Map<String, Int> /* oldCount: number *//* newCount: number */ {
    TODO()
  }


  @GetMapping("/{aid}")
  @ResponseBody
  fun stargazers(@PathVariable("aid") aid: AppId): List<App> {
    TODO()
  }


  @GetMapping("/user/{uid}")
  @ResponseBody
  fun stars(@PathVariable("uid") uid: UserId): List<App> {
    TODO()
  }
}
