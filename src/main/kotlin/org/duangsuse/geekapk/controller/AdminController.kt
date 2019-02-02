package org.duangsuse.geekapk.controller

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.CategoryId
import org.duangsuse.geekapk.CommentId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.entity.App
import org.duangsuse.geekapk.entity.AppUpdate
import org.duangsuse.geekapk.entity.Category
import org.duangsuse.geekapk.entity.GeekUser
import org.duangsuse.geekapk.helper.ApiDoc
import org.duangsuse.geekapk.repositories.AppRepository
import org.duangsuse.geekapk.repositories.CategoryRepository
import org.duangsuse.geekapk.repositories.CommentRepository
import org.duangsuse.geekapk.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("admin")
class AdminController {
  @Autowired
  private lateinit var users: UserRepository

  @Autowired
  private lateinit var categories: CategoryRepository

  @Autowired
  private lateinit var comments: CommentRepository

  @Autowired
  private lateinit var apps: AppRepository

  @GetMapping
  @ResponseBody fun apiHint(hsr: HttpServletRequest): Map<String, String> = ApiDoc.admin(hsr).second

  @PostMapping("makeUser")
  @ResponseBody
  fun createUser(@RequestParam("username") username: String): GeekUser {
    TODO()
  }

  @PutMapping("resetMetaHash/{uid}")
  @ResponseBody
  fun resetSharedHash(@PathVariable("uid") uid: UserId, @RequestParam("shash", required = false) shash: String?): String {
    TODO()
  }

  @DeleteMapping("dropUser/{uid}")
  @ResponseBody
  fun deleteUser(@PathVariable("uid") uid: UserId): GeekUser {
    TODO()
  }

  @PutMapping("flagUser/{uid}")
  @ResponseBody
  fun flagUser(@PathVariable("uid") uid: UserId, @RequestParam("flag") flag: Int): GeekUser {
    TODO()
  }

  @PostMapping("makeCategory")
  @ResponseBody
  fun createCategory(@RequestParam("name") name: String): Category {
    TODO()
  }

  @PutMapping("nameCategory/{id}")
  @ResponseBody
  fun renameCategory(@PathVariable("id") id: CategoryId, @RequestParam("name") newName: String): Category {
    TODO()
  }

  @DeleteMapping("dropCategory/{id}")
  @ResponseBody
  fun deleteCategory(@PathVariable("id") id: CategoryId): Category {
    TODO()
  }

  @DeleteMapping("dropApp/{aid}")
  @ResponseBody
  fun deleteApp(@PathVariable("aid") aid: AppId): App {
    TODO()
  }

  @PutMapping("moveApp/{aid}")
  @ResponseBody
  fun transferAppCategory(@PathVariable("aid") aid: AppId, @RequestParam("cid") newCid: CategoryId): Map<String, Long> {
    TODO()
  }

  @PutMapping("transferApp/{aid}")
  @ResponseBody
  fun transferAppOwner(@PathVariable("aid") aid: AppId, @RequestParam("uid") newUid: UserId): Map<String, Long> {
    TODO()
  }

  @DeleteMapping("dropAppUpdate/{aid}/{rev}")
  @ResponseBody
  fun deleteAppUpdate(@PathVariable("aid") aid: AppId, @PathVariable("rev") rev: Int): AppUpdate {
    TODO()
  }

  @DeleteMapping("dropComment/{cid}")
  @ResponseBody
  fun deleteComment(@PathVariable("cid") cid: CommentId): Map<String, Long> {
    TODO()
  }
}
