package org.duangsuse.geekapk.controller

import org.duangsuse.geekapk.qcl.QCL
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class QCLController {
  @PostMapping("/qclExec")
  @ResponseBody
  fun qclExecute(@RequestParam("stmt") stmt: String): String {
    return QCL.execute(stmt).toString()
  }
}
