package org.duangsuse.geekapk.controller

import org.duangsuse.geekapk.helper.ApiDoc
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("appUpdate")
class AppReversionController {
  @GetMapping
  @ResponseBody fun apiHint(hsr: HttpServletRequest) = ApiDoc.appUpdate(hsr).second
}
