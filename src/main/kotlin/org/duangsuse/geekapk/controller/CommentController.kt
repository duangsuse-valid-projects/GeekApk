package org.duangsuse.geekapk.controller

import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.CommentId
import org.duangsuse.geekapk.CommentSize
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.entity.Comment
import org.duangsuse.geekapk.helper.ApiDoc
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("comment")
class CommentController {
  @GetMapping
  @ResponseBody
  fun apiHint(hsr: HttpServletRequest) = ApiDoc.comment(hsr).second

  @GetMapping("/search/{content}")
  @ResponseBody
  fun searchComment(@RequestParam(name = "inApp", required = false) inApp: AppId?, @RequestParam(name = "user", required = false) user: UserId?,
                    @RequestParam(name = "repliesTo", required = false) repliesTo: CommentId?, @PathVariable("content") content: String): List<Comment> {
    TODO()
  }


  @GetMapping("/{aid}")
  @ResponseBody
  fun listCommentInApp(@PathVariable("aid") aid: AppId, @RequestParam(name = "sliceFrom", required = false) sliceFrom: CommentSize?,
                       @RequestParam(name = "sliceTo", required = false) sliceTo: CommentSize?): List<Comment> {
    TODO()
  }


  @GetMapping("/subOf/{cid}")
  @ResponseBody
  fun listSubComment(@PathVariable("cid") cid: CommentId): List<Comment> {
    TODO()
  }


  @GetMapping("/all")
  @ResponseBody
  fun listAllComment(@RequestParam(name = "inApp", required = false) inApp: AppId?, @RequestParam(name = "user", required = false) user: UserId?,
                     @RequestParam(name = "sliceFrom", required = false) sliceFrom: CommentSize?, @RequestParam(name = "sliceTo", required = false) sliceTo: CommentSize?): List<Comment> {
    TODO()
  }


  @PostMapping("/{aid}")
  @ResponseBody
  fun createComment(@PathVariable("aid") aid: AppId, @RequestParam("content") content: String): Comment {
    TODO()
  }


  @PutMapping("/edit/{cid}")
  @ResponseBody
  fun editComment(@PathVariable("cid") cid: CommentId): Map<String, String> /* oldContent: string *//* newContent: string */ {
    TODO()
  }


  @DeleteMapping("/delete/{cid}")
  @ResponseBody
  fun deleteComment(@PathVariable("cid") cid: CommentId): Comment {
    TODO()
  }
}
