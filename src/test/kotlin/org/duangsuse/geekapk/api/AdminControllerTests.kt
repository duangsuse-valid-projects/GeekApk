package org.duangsuse.geekapk.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.duangsuse.geekapk.controller.AdminController
import org.duangsuse.geekapk.entity.*
import org.duangsuse.geekapk.repositories.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.servlet.http.Cookie

/**
 * Tests for service endpoint `AdminController`
 *
 * @see AdminController to be tested
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTests {
  @Autowired
  private lateinit var mock: MockMvc

  @Autowired
  private lateinit var users: UserRepository

  @Autowired
  private lateinit var categories: CategoryRepository

  @Autowired
  private lateinit var comments: CommentRepository

  @Autowired
  private lateinit var apps: AppRepository

  @Autowired
  private lateinit var reversions: AppUpdateRepository

  @Test fun contextLoads() = Unit

  var adminId = 0
  var adminHash = ""
  val mapper = ObjectMapper()

  var userId = -1
  var catId = -1
  var pkgId = -1L

  fun makeTestCookie(ctx: AdminControllerTests): Array<Cookie>
    = arrayOf(Cookie(USER_COOKIE, ctx.adminId.toString()), Cookie(HASH_COOKIE, ctx.adminHash), Cookie(ADMIN_COOKIE, "1234567"))

  /**
   * Provide basic data like operational admin user
   */
  @Test
  fun setupDatabase() {
    val admin = GeekUser.makeUser("doge")
    admin.bio = "Just a simple administer :doge:"
    admin.flags = GeekUser.FLAG_ADMIN
    val saved = users.save(admin)

    adminId = saved.id
    adminHash = saved.hash

    val cat = Category.makeNew("Foo")
    val savedCat = categories.save(cat)

    val pkg = App(packageName = "ooo.ooo", author = adminId, category = savedCat.id)
    pkgId = apps.save(pkg).id
  }

  /**
   * Creating new user
   */
  @Test
  fun createsUser() {
    mock.perform(post("/admin/makeUser")
      .param("username", "duangsuse")
      .cookie(*makeTestCookie(this)))
      .andExpect(status().isCreated)
      .andExpect(jsonPath("$.username").value("duangsuse"))
      .andExpect { user ->
        val obj = mapper.readValue(user.response.contentAsString, HashMap<String, String>()::class.java)
        userId = obj.getOrElse("id") { throw RuntimeException("Expect $obj to have property 'id'") }.toInt()
        assert (users.findById(obj["id"]!!.toInt()).isPresent) { "User id for $obj should be present" }
      }
  }

  @Test
  fun rejectCreatingExistingUser() {
    mock.perform(post("/admin/makeUser")
      .param("username", "duangsuse")
      .cookie(*makeTestCookie(this)))
      .andExpect(status().isConflict)
  }

  @Test
  fun resetsSharedHash() {
    mock.perform(put("/admin/resetMetaHash/$userId")
      .cookie(*makeTestCookie(this)))
      .andExpect(status().isNoContent)
      .andExpect { Assert.assertEquals(it.response.contentAsString, users.findById(userId).get().sharedHash) }

    mock.perform(put("/admin/resetMetaHash/$userId")
      .param("shash", "yoo"))
      .andExpect(status().isNoContent)
      .andExpect { Assert.assertEquals(it.response.contentAsString, "yoo") }

    Assert.assertEquals(users.findById(userId).get().sharedHash, "yoo")
  }
  @Test
  fun flagsUser() {
    mock.perform(put("/admin/flagUser/$userId")
      .cookie(*makeTestCookie(this))
      .param("flag", GeekUser.FLAG_READONLY.toString()))
      .andExpect(status().isNoContent)
      .andExpect(jsonPath("$.flags").value(GeekUser.FLAG_READONLY))

    Assert.assertEquals(users.findById(userId).get().flags, GeekUser.FLAG_READONLY)
  }
  @Test
  fun deletesUser() {
    mock.perform(delete("/admin/dropUser/$userId")
      .cookie(*makeTestCookie(this)))
      .andExpect(status().isGone)
      .andExpect(jsonPath("$.username").value("duangsuse"))

    users.findById(userId).map { throw AssertionError("$userId Should missing when delete succeed") }
  }

  @Test
  fun createsCategory() {
    mock.perform(post("/admin/makeCategory")
      .cookie(*makeTestCookie(this))
      .param("name", "Forums"))
      .andExpect(status().isCreated)
      .andExpect(jsonPath("$.name").value("Forums"))
  }
  @Test
  fun renamesCategory() {
    mock.perform(put("/admin/nameCategory/$catId")
      .cookie(*makeTestCookie(this))
      .param("name", "NewCats"))
      .andExpect(status().isNoContent)
      .andExpect(jsonPath("$.name").value("Forums"))
  }
  @Test
  fun deletesCategory() {
    mock.perform(delete("/admin/dropCategory/$catId")
      .cookie(*makeTestCookie(this)))
      .andExpect(status().isGone)
      .andExpect(jsonPath("$.name").value("NewCats"))
  }

  @Test
  fun transfersAppOwner() {
    mock.perform(put("/admin/transferApp/$pkgId")
      .cookie(*makeTestCookie(this))
      .param("uid", adminId.toString()))
      .andExpect(status().isNoContent)
      .andExpect(jsonPath("$.author").value(adminId))
  }

  @Test
  fun transfersAppCategory() {
    val cat = categories.save(Category.makeNew("Utils"))
    mock.perform(put("/admin/moveApp/$pkgId")
      .cookie(*makeTestCookie(this))
      .param("cid", cat.id.toString()))
      .andExpect(status().isNoContent)
      .andExpect(jsonPath("$.category").value(cat.id))
  }

  @Test
  fun deletesAppUpdate() {
    apps.save(App(id = 3, author = -1, category = -1))
    val update = AppUpdate(forApp = 3, reversion = 1, updates = "Foo")
    val saved = reversions.save(update)

    mock.perform(delete("/admin/dropAppUpdate/3/1")
      .cookie(*makeTestCookie(this)))
      .andExpect(status().isGone)
      .andExpect(jsonPath("$.updates").value("Foo"))
  }

  @Test
  fun deletesComment() {
    val comment = comments.save(Comment(author = -1, app = -1, replies = -1, repliesCount = 3))

    mock.perform(delete("/admin/dropComment/${comment.id}")
      .cookie(*makeTestCookie(this)))
      .andExpect(status().isGone)
      .andExpect(jsonPath("$.deletedSubComments").value(3))
  }

  @Test
  fun deletesApp() {
    mock.perform(delete("/admin/dropApp/$pkgId")
      .cookie(*makeTestCookie(this)))
      .andExpect(status().isGone)
      .andExpect(jsonPath("$.packageName").value("ooo.ooo"))
  }

  @Test
  fun shouldGiveUserNotFound() {
    mock.perform(delete("/admin/dropUser/5")
      .cookie(*makeTestCookie(this)))
      .andExpect(status().isNotFound)
  }

  @Test
  fun shouldGiveCommentNotFound() {
    mock.perform(delete("/admin/dropComment/1000")
      .cookie(*makeTestCookie(this)))
      .andExpect(status().isNotFound)
  }

  @Test
  fun shouldGiveUpdateNotFound() {
    mock.perform(delete("/admin/dropApp/100")
      .cookie(*makeTestCookie(this)))
      .andExpect(status().isNotFound)
  }

  @Test
  fun shouldRejectNonAdministrativeUser() {
    val person = users.save(GeekUser.makeUser("fooK"))
    mock.perform(delete("/admin/dropUser/${person.id}"))
      .andExpect(status().isUnauthorized)
  }

  companion object {
    const val USER_COOKIE = "gaUser"
    const val HASH_COOKIE = "gaHash"
    const val ADMIN_COOKIE = "gaModTok"
  }
}
