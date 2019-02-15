package org.duangsuse.geekapk.middleware

import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Filter for server admin API authentication
 */
@WebFilter("/admin/*")
class ServerAdminAuthenticationFilter: OncePerRequestFilter() {
  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    filterChain.doFilter(request, response)
  }
}
