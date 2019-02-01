package org.duangsuse.geekapk.controller

import org.duangsuse.geekapk.helper.ApiDoc
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("timeline")
class TimelineController {
  @GetMapping
  @ResponseBody fun apiHint(hsr: HttpServletRequest) = ApiDoc.timeline(hsr).second
}
