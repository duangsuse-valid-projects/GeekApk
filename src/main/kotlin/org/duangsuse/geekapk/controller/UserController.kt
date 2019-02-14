package org.duangsuse.geekapk.controller

import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.UserSize
import org.duangsuse.geekapk.entity.GeekUser
import org.duangsuse.geekapk.helper.ApiDoc
import org.duangsuse.geekapk.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("user")
class UserController {
  @Autowired
  private lateinit var users: UserRepository

  @GetMapping
  @ResponseBody fun apiHint(hsr: HttpServletRequest) = ApiDoc.user(hsr).second

  @GetMapping("/{id}")
  @ResponseBody fun readUser(@PathVariable("id") uid: UserId): GeekUser {
    TODO()
  }

  @PutMapping("/{id}")
  @ResponseBody fun updateUser(@PathVariable("id") id: UserId, @RequestParam("prop") prop: String, @RequestBody value: String): Map<String, String> {
    TODO()
  }

  @PutMapping("/{id}/hash")
  @ResponseBody fun resetHash(@PathVariable("id") id: UserId, @RequestParam("shash") shash: String, @RequestParam("hash") hash: String): Map<String, String> {
    TODO()
  }

  @GetMapping("/{id}/checkHash")
  @ResponseBody fun checkHash(@PathVariable("id") id: UserId, @RequestParam("hash") hash: String): Map<String, String> {
    TODO()
  }

  @GetMapping("/all")
  @ResponseBody fun listUser(@RequestParam(name = "sort", required = false) sort: String?,
                             @RequestParam(name = "sliceFrom", required = false) sliceFrom: UserSize?, @RequestParam(name = "sliceTo", required = false) sliceTo: UserSize?): List<GeekUser> {
    TODO()
  }

  @GetMapping("/allHasMetaApp")
  @ResponseBody fun listMetaUser(@RequestParam(name = "sort", required = false) sort: String?,
                             @RequestParam(name = "sliceFrom", required = false) sliceFrom: UserSize?, @RequestParam(name = "sliceTo", required = false) sliceTo: UserSize?): List<GeekUser> {
    TODO()
  }

  @GetMapping("/search/{kw}")
  @ResponseBody fun searchUser(@RequestParam(name = "type", required = false) type: String?, @PathVariable("kw") kw: String, @RequestParam(name = "sort", required = false) sort: String?): List<GeekUser> {
    TODO()
  }

  @PutMapping("/{id}/online")
  @ResponseBody fun updateOnlineTime(@PathVariable("id") uid: UserId) {
    TODO()
  }
}
