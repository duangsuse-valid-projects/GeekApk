package org.duangsuse.geekapk.entity

import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.annotations.CounterFor
import org.duangsuse.geekapk.annotations.StandaloneEntity
import org.duangsuse.geekapk.annotations.UserPrivate
import org.duangsuse.geekapk.helpers.loopFor
import org.jetbrains.annotations.Nls
import java.lang.StringBuilder
import java.util.*
import javax.persistence.*
import kotlin.math.roundToInt

@StandaloneEntity("user")
@Entity
data class GeekUser (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val uid: UserId = 0,
  @Version var version: Long = 0,

  @Nls var username: String = "(generated)",
  @Nls var nickname: String = "Geek",

  var avatarUrl: String = "",

  @Nls var bio: String = "No bio provided QAQ",

  var flags: Int = FLAG_NONE,

  @UserPrivate
  var sharedHash: String = makeSharedHash(20),
  @UserPrivate
  var hash: String = "",

  val createdAt: Date = Date(),

  /* Weak field */
  @CounterFor("user")
  var followersCount: Int = 0
) {
  companion object {
    const val FLAG_NONE = 0b0
    const val FLAG_READONLY = 0b1
    const val FLAG_BANNED = 0b10
    const val FLAG_ADMIN = 0b11
    private val SHARED_HASH_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".toCharArray()

    /** Generate random [A-Za-z0-9] strings */
    @JvmStatic
    fun makeSharedHash(length: Int): String {
      val ret = StringBuilder()

      val appendRandomChar = fun (_: Int) {
        ret.append(SHARED_HASH_CHARS[(Math.random() * SHARED_HASH_CHARS.lastIndex).roundToInt()])
      }

      let { appendRandomChar loopFor length }

      return ret.toString()
    }
  }
}
