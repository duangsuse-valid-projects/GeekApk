package org.duangsuse.geekapk.controller

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.entity.AppUpdate
import org.duangsuse.geekapk.helper.ApiDoc
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("appUpdate")
class AppReversionController {
  @GetMapping
  @ResponseBody
  fun apiHint(hsr: HttpServletRequest) = ApiDoc.appUpdate(hsr).second

  @GetMapping("/appUpdate/{aid}")
  @ResponseBody
  fun readReversions(@PathVariable("aid") aid: AppId): List<AppUpdate> {
    TODO()
  }


  @GetMapping("/appUpdate/check/{aids}")
  @ResponseBody
  fun checkLastReversions(@PathVariable("aids") aids: String): List<Int> {
    TODO()
  }


  @GetMapping("/appUpdate/{aid}/{rev}")
  @ResponseBody
  fun readReversion(@PathVariable("aid") aid: AppId, @PathVariable("rev") rev: Int): AppUpdate {
    TODO()
  }


  @PostMapping("/appUpdate/{aid}/{rev}")
  @ResponseBody
  fun createReversion(@PathVariable("aid") aid: AppId, @PathVariable("rev") rev: Int): Map<String, Int> /* aid: number *//* rev: number */ {
    TODO()
  }


  @PutMapping("/appUpdate/{aid}/{rev}")
  @ResponseBody
  fun updateReversion(@PathVariable("aid") aid: AppId, @PathVariable("rev") rev: Int,
                      @RequestParam("attr") attr: String/* Maybe version or install or updates or minsdk */, @RequestBody value: String): Map<String, String> /* attr: string *//* oldVal: string */ {
    TODO()
  }


  @DeleteMapping("/appUpdate/{aid}/{rev}")
  @ResponseBody
  fun dropReversion(@PathVariable("aid") aid: AppId, @PathVariable("rev") rev: Int): AppUpdate {
    TODO()
  }
}
