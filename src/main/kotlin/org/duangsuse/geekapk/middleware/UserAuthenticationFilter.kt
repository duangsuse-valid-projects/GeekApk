package org.duangsuse.geekapk.middleware

import org.duangsuse.geekapk.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.filter.OncePerRequestFilter
import javax.annotation.Priority
import javax.servlet.FilterChain
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// TODO refactor validator logic
/**
 * GeekApk User authentication filter
 *
 * Auth user scheme(Token, Cookie): uid=gaUser tok=gaHash
 */
@WebFilter(urlPatterns = ["/notification/*", "/app/{aid}/collab"], servletNames = ["updateUser", "updateOnlineTime",
  "updateApp", "createApp", "dropApp", "createReversion", "updateReversion", "dropReversion", "createComment",
  "editComment", "deleteComment", "follow", "unfollow", "star", "unstar", "patchUser", "patchApp", "patchReversion"])
@Priority(value = 10)
class UserAuthenticationFilter: OncePerRequestFilter() {
  @Autowired
  lateinit var users: UserRepository

  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    if (request.cookies.isNullOrEmpty()) {
      response.status = HttpStatus.UNAUTHORIZED.value()
      response.writer.print("Cookie required")
      return
    }

    val user = request.cookies.find { it.name == ServerAdminAuthenticationFilter.GA_USER }
    val hash = request.cookies.find { it.name == ServerAdminAuthenticationFilter.GA_HASH }

    if (user == null || hash == null) {
      response.status = HttpStatus.UNAUTHORIZED.value()
      response.writer.print("User account required")
      return
    }

    // check user
    val uid = user.value.toIntOrNull()
    if (uid != null) {
      val found = users.findById(uid)

      if (!found.isPresent) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.writer.print("User $uid not found in database")
        return
      }

      val realHash = found.get().hash

      if (hash.value != realHash) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.writer.print("Bad hash value for user $uid")
        return
      }
    } else {
      response.status = HttpStatus.BAD_REQUEST.value()
      response.writer.print("Failed to parse user id ${user.value}")
      return
    }

    // proceed
    filterChain.doFilter(request, response)
  }
}
