package org.duangsuse.geekapk.controller

import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.lang.management.ManagementFactory
import javax.servlet.http.HttpServletRequest

@Controller
class MainController {
  fun String.href(req: HttpServletRequest) = "${req.scheme}://${req.localAddr}:${req.localPort}/$this"

  @GetMapping
  @ResponseBody
  fun apiHint(hsr: HttpServletRequest): Map<String, Map<String, String>> = mapOf(
    "server" to mapOf(
      "index" to "".href(hsr),
      "version" to "serverVersion".href(hsr),
      "desc" to "serverDesc".href(hsr),
      "upTime" to "serverBoot".href(hsr),
      "detailedInfo" to "detail".href(hsr)
    )
  )

  @GetMapping("detail")
  @ResponseBody
  fun serverDetail(): Map<String, String> = serverDetail

  @GetMapping("serverVersion")
  @ResponseBody
  fun serverVersion(): String = programVersion

  @GetMapping("serverDesc")
  @ResponseBody
  fun serverDesc(): String = serverDesc

  @GetMapping("serverBoot")
  @ResponseBody
  fun serverUptime(): Date = bootUpAt

  companion object {
    fun makeServerDetail(): Map<String, String> {
      val result = mutableMapOf<String, String>()

      val javaRt = System.getProperty("java.version", "Unknown")
      result["jdkVersion"] = javaRt

      val jvmComp = System.getProperty("sun.management.compiler", "Unknown Compiler")
      val jvmMode = System.getProperty("java.vm.info", "Unknown Flags")

      val javaImpBean = ManagementFactory.getRuntimeMXBean()
      result["jvmSpec"] = "${javaImpBean.vmName} from ${javaImpBean.vmVendor}"
      result["jvmVersion"] = javaImpBean.vmVersion
      result["jvmMode"] = jvmMode
      result["bootUptime"] = javaImpBean.uptime.toString()
      result["jvmCompiler"] = jvmComp
      result["jvmDataModel"] = System.getProperty("sun.arch.data.model", "Unknown")
      result["encoding"] = System.getProperty("file.encoding", "UTF-8")

      result.putAll(mapOf(
        "os" to System.getProperty("os.name", "Unknown OS Family"),
        "arch" to System.getProperty("os.arch", "Unknown Arch"),
        "osVersion" to System.getProperty("os.version", "Unknown OS Version")
      ))

      return result
    }

    const val programVersion = "0.1.0"
    val serverDesc = """
      > Spring GeekApk Server by duangsuse, the dying Server for GeekApk.
      > Spring version: 2.1.2
      > Kotlin version: 1.2.7
      """.trimMargin(">")

    val serverDetail = makeServerDetail()
    val bootUpAt = Date()
  }
}

@Configuration("cors")
class CorsConfiguration : WebMvcConfigurer {
  override fun addCorsMappings(registry: CorsRegistry) {
    registry.addMapping("/**")
      .allowedOrigins("*")
      .allowedMethods("GET", "POST", "DELETE", "PUT")
      .maxAge(3600)
  }
}
