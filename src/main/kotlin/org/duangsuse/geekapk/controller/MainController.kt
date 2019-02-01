package org.duangsuse.geekapk.controller

import org.duangsuse.geekapk.helper.ApiDoc
import org.springframework.boot.SpringBootVersion
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.management.ManagementFactory
import java.util.*
import javax.servlet.http.HttpServletRequest

/**
 * Main (server-related information) controller logic
 *
 * @author duangsuse
 * @version 1.0
 * @since 1.0
 */
@Controller
class MainController {
  /* This is API document(R */
  @GetMapping("/")
  @ResponseBody
  /** must be fun */ fun apiHint(hsr: HttpServletRequest): Map<String, Map<String, String>> = ApiDoc.root(hsr)

  /* useless but fun */
  @GetMapping("/mustError")
  @ResponseStatus(code = HttpStatus.I_AM_A_TEAPOT, reason = "see \$object")
  @ResponseBody /* body not concerned by Spring */
  fun errorJson(): Map<String, String> = mapOf("message" to "(^_^)")

  @GetMapping("serverDetail")
  @ResponseBody
  fun serverDetail(): Map<String, String> = serverDetail

  @GetMapping("serverVersion")
  @ResponseBody
  fun serverVersion(): String = programVersion

  @GetMapping("serverDescription")
  @ResponseBody
  fun serverDescription(): String = serverDescription

  @GetMapping("serverBoot")
  @ResponseBody
  fun serverUptime(): Date = bootUpAt()

  companion object {
    /**
     * Make server detail maps using JMX capabilities
     *
     * @return java collections map with K(property name) to V(value)
     */
    private fun makeServerDetail(): Map<String, String> {
      val result = mutableMapOf<String, String>()

      val javaRt = System.getProperty("java.version", "Unknown")
      result["jdkVersion"] = javaRt

      val jvmComp = System.getProperty("sun.management.compiler", "Unknown Compiler")
      val jvmMode = System.getProperty("java.vm.info", "Unknown Flags")

      val javaImpBean = ManagementFactory.getRuntimeMXBean()
      result["jvmSpec"] = "${javaImpBean.vmName} from ${javaImpBean.vmVendor}"
      result["jvmVersion"] = javaImpBean.vmVersion
      result["jvmMode"] = jvmMode
      result["bootUpTime"] = javaImpBean.uptime.toString()
      result["jvmCompiler"] = jvmComp
      result["jvmDataModel"] = System.getProperty("sun.arch.data.model", "Unknown")
      result["encoding"] = System.getProperty("file.encoding", "UTF-8")

      result.putAll(mapOf(
        "os" to System.getProperty("os.name", "Unknown OS Family"),
        "arch" to System.getProperty("os.arch", "Unknown Arch"),
        "osVersion" to System.getProperty("os.version", "Unknown OS Version")
      ))

      /* Information should be provided as properties since they're inefficient to detect automatically */
      result.putAll(mapOf(
        "spring" to SpringBootVersion.getVersion(),
        "webServer" to System.getProperty("geekapk.info.springEmbeddedServer", "Tomcat"),
        "webServerVersion" to System.getProperty("geekapk.info.springEmbeddedServerVersion", "Unknown"),
        "hibernate" to org.hibernate.Version.getVersionString(),
        "postgres" to System.getProperty("geekapk.info.postgreSQLVersion", "Unknown")
      ))

      return result
    }

    /**
     * Server program version
     */
    const val programVersion = "0.1.0"

    const val description = "description"
    const val schema = "schema"

    /**
     * Server program description for text clients
     */
    val serverDescription = """
      > Spring GeekApk Server by duangsuse, the dying Server for GeekApk.
      > Spring version: 2.1.2
      > Kotlin version: 1.2.7
      > Built with love and ^_^ using IntelliJ IDEA
      """.trimMargin(">").intern()

    /**
     * Static server detail made during server boot-ups
     */
    val serverDetail = makeServerDetail()
    val bootUpAt = { Date() } /* must be call-by-need (why not `by` lazy?) */
  }
}
