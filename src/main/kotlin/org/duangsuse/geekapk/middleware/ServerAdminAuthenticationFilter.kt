package org.duangsuse.geekapk.middleware

import org.duangsuse.geekapk.entity.GeekUser
import org.duangsuse.geekapk.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Filter for server admin API authentication
 *
 * Auth serverAdmin scheme(Token, Cookie): uid=gaUser tok=gaHash admin_tok=gaModTok
 * All ADMIN apis except deleteApp, transferAppCategory, removeComment, flagUser should have serverAdmin permission
 */
@WebFilter("/admin/*")
class ServerAdminAuthenticationFilter: OncePerRequestFilter() {
  @Autowired lateinit var users: UserRepository

  /**
   * Validates user authentication token
   */
  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    if (request.cookies.isNullOrEmpty()) {
      response.status = HttpStatus.UNAUTHORIZED.value()
      response.writer.print("Cookie required")
      return
    }

    val user = request.cookies.find { it.name == GA_USER }
    val hash = request.cookies.find { it.name == GA_HASH }

    if (user == null || hash == null) {
      response.status = HttpStatus.UNAUTHORIZED.value()
      response.writer.print("User account required")
      return
    }

    val userInt = user.value.toIntOrNull()

    // Bad practice
    // Bad duangsuse
    if (userInt == null) {
      response.status = HttpStatus.BAD_REQUEST.value()
      response.writer.print("Failed to parse user id ${user.value}")
      return
    }

    // check user
    userInt.let {
      val found = users.findById(it)

      if (!found.isPresent) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.writer.print("User $it not found in database")
        return
      }

      if (found.get().flags xor GeekUser.FLAG_ADMIN == 1) {
        response.status = HttpStatus.FORBIDDEN.value()
        response.writer.print("User $it is not a server administer")
        return
      }

      val realHash = found.get().hash

      if (hash.value != realHash) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.writer.print("Bad hash value for user $it")
        return
      }

      if (request.httpServletMapping.mappingMatch.name in arrayOf("deleteApp", "transferAppCategory", "deleteComment", "flagUser"))
        return@let

      // check for server Admin permission
      val admin = request.cookies.find { cookie ->  cookie.name == GA_MOD_TOKEN }
      admin?.run {
        if (value == GeekUser.KEY) return@let
      }

      response.status = HttpStatus.FORBIDDEN.value()
      response.writer.print("Bad server program password")
      return
    }

    // proceed
    filterChain.doFilter(request, response)
  }

  companion object {
    const val GA_MOD_TOKEN = "gaModTok"
    const val GA_USER = "gaUser"
    const val GA_HASH = "gaHash"
  }
}
