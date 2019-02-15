package org.duangsuse.geekapk.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

private typealias LimitMap = Map<String, Int>

@Controller
class LimitController {
  @GetMapping("/limit")
  @ResponseBody
  fun getMyLimit(): LimitMap {
    return limitMap
  }

  companion object {
      val limitMap: LimitMap = mutableMapOf()
  }
}
