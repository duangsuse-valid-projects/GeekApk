package org.duangsuse.geekapk.middleware

import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebFilter
class CorsFilter : OncePerRequestFilter() {

  @Throws(ServletException::class, IOException::class)
  override fun doFilterInternal(request: HttpServletRequest,
                                response: HttpServletResponse, filterChain: FilterChain) {
    response.addHeader("Access-Control-Allow-Origin", "*")
    if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS" == request.method) {

      response.addHeader("Access-Control-Allow-Methods",
        "GET, POST, PUT, DELETE")

      response.addHeader("Access-Control-Allow-Headers",
        "X-Requested-With,Origin,Content-Type, Accept")
    }

    filterChain.doFilter(request, response)
  }

}
