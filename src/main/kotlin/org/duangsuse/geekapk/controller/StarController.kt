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

  @PostMapping("/star/{aid}")
  @ResponseBody
  fun star(@PathVariable("aid") aid: AppId): Map<String, Int> /* oldCount: number *//* newCount: number */ {
    TODO()
  }


  @DeleteMapping("/star/{aid}")
  @ResponseBody
  fun unStar(@PathVariable("aid") aid: AppId): Map<String, Int> /* oldCount: number *//* newCount: number */ {
    TODO()
  }


  @GetMapping("/star/{aid}")
  @ResponseBody
  fun stargazers(@PathVariable("aid") aid: AppId): List<App> {
    TODO()
  }


  @GetMapping("/star/user/{uid}")
  @ResponseBody
  fun stars(@PathVariable("uid") uid: UserId): List<App> {
    TODO()
  }
}
