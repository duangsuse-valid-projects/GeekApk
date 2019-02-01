package org.duangsuse.geekapk.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.duangsuse.geekapk.AppId
import org.duangsuse.geekapk.UserId
import org.duangsuse.geekapk.annotation.*
import org.duangsuse.geekapk.helper.loopFor
import org.duangsuse.geekapk.helper.times
import org.jetbrains.annotations.Nls
import java.io.Serializable
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Size
import kotlin.math.roundToInt

/**
 * GeekApk users
 */
@StandaloneEntity("user")
@Table(name = "users")
@Entity
data class GeekUser (
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: UserId = 0,

  /**
   * Transient version log
   */
  @Version var version: Long = 0,

  /**
   * User's identical machine name (but not in GeekApk 1.x)
   */
  @Size(message = "username must be greater than 1 char and smaller than 21 char count", min = 2, max = 20)
  @Nls var username: String = "(generated)",

  /**
   * User's display name
   */
  @Size(message = "nickname too long (~ ..90)", min = 0, max = 90) /* in case for unicode composition */
  @Nls var nickname: String = "Geek",

  /**
   * User's avatar bitmap image link
   */
  @Size(message = "avatar url too large (~ ..600)", min = 0, max = 600)
  @Lob var avatarUrl: String = "",

  /**
   * User's meta application ID, may null
   */
  @LinkTo("app", Relation.HAS_ONE)
  var metaApp: AppId? = null,

  /**
   * User's markdown bio text
   */
  @Size(message = "bio too long (~ ..6k)", min = 0, max = 6000)
  @Markdown @Lob @Nls var bio: String = "_No bio provided QAQ_",

  /**
   * User status flags
   */
  var flags: Int = FLAG_NONE,

  /**
   * "Distributed" Password
   */
  @UserPrivate
  @JsonIgnore
  var sharedHash: String = makeSharedHash(20), /* static server-computed size */

  /**
   * Password for the user
   */
  @UserPrivate /* should be set to another value in controller level */
  @JsonIgnore
  @Size(min = 256 / 4, max = 256 / 4) /* SHA-256 hex representation hash */
  var hash: String = "68e656b251e67e8358bef8483ab0d51c6619f3e7a1a9f0e75838d41ff368f728",

  /**
   * User's registration date
   */
  @Temporal(TemporalType.TIMESTAMP)
  val createdAt: Date = Date(),

  /* Weak field */
  /**
   * Followers number
   */
  @CounterFor("user")
  var followersCount: Int = 0
): Serializable {
  companion object {
    /**
     * Admin token (doge token)
     */
    val KEY: String = System.getProperty("geekapk.key", "dolphins")
    /**
     * Internal bot user ID used to send system messages
     */
    val BOT_UID: UserId = System.getProperty("geekapk.bot", "0").toInt()
    /**
     * By default, GeekApk send all system broadcasts to admin user's meta app (if none, the feature will be disabled)
     * set this to -1 to disable sending
     */
    val BOT_OVERRIDE: AppId? = System.getProperty("geekapk.botMessageAppOverride", "").toLongOrNull()

    /**
     * Dummy flag implies "no-login" user
     */
    const val FLAG_NOBODY = -0b1
    /**
     * Banned user
     */
    const val FLAG_BANNED = 0b10
    /**
     * "Muted" users
     */
    const val FLAG_READONLY = 0b1
    /**
     * Basic user permissions
     */
    const val FLAG_NONE = 0b0
    /**
     * Administers
     */
    const val FLAG_ADMIN = 0b11

    @Suppress("SpellCheckingInspection")
    private const val A_TO_Z = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    @Suppress("SpellCheckingInspection")
    private const val LOWERCASE_A_TO_Z = "abcdefghijklmnopqrstuvwxyz"
    private const val DIGITS = "0123456789"
    @Suppress("SpellCheckingInspection")
    private val SHARED_HASH_CHARS = "$A_TO_Z$LOWERCASE_A_TO_Z${DIGITS}_".toCharArray()

    /** Generate random [A-Za-z0-9] strings */
    @JvmStatic
    fun makeSharedHash(length: Int): String {
      val ret = StringBuilder()

      val appendRandomChar = fun (_: Int) {
        ret.append(SHARED_HASH_CHARS[(Math.random() * SHARED_HASH_CHARS.lastIndex).roundToInt()])
      }

      let { appendRandomChar loopFor length . times() }

      return ret.toString()
    }

    private val makeNewShared64CharKey = { makeSharedHash(64) }

    /**
     * Function to make a new user
     */
    private fun makeUserProto(hash: () -> String = makeNewShared64CharKey) = fun (name: String): GeekUser {
      return GeekUser(username = name, hash = hash())
    }

    val makeUser = makeUserProto()
  }
}
