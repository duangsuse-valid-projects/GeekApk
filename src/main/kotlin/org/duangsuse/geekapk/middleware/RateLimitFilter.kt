package org.duangsuse.geekapk.middleware

import org.springframework.web.filter.OncePerRequestFilter
import javax.annotation.Priority
import javax.servlet.FilterChain
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebFilter
@Priority(value = 11)
class RateLimitFilter: OncePerRequestFilter() {
  override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
    filterChain.doFilter(request, response)
  }
}
