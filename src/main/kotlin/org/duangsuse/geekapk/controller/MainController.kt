package org.duangsuse.geekapk.controller

import org.springframework.boot.SpringBootVersion
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.lang.management.ManagementFactory
import java.util.*
import javax.servlet.http.HttpServletRequest
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter



@Controller
class MainController {
  fun String.href(req: HttpServletRequest) = "${req.scheme}://${req.localAddr}:${req.localPort}/$this"

  /* This is API document(R */
  @GetMapping("/")
  @ResponseBody
  fun apiHint(hsr: HttpServletRequest): Map<String, Map<String, String>> = mapOf(
    "server" to mapOf(
      "description" to "GeekApk server information related APIs",
      "index() -> object<category,object<operation,linkTemplate>>" to "".href(hsr),
      "version() -> plain" to "serverVersion".href(hsr),
      "desc() -> plain" to "serverDesc".href(hsr),
      "upTime() -> string:datetime" to "serverBoot".href(hsr),
      "detailedInfo() -> object:<prop,desc>" to "detail".href(hsr)
    ),
    "admin" to mapOf(
      "description" to "GeekApk community administration functions",
      "schema" to "Cookie(gaModTok)",
      "POST@createUser(username) -> object:user" to "makeUser".href(hsr),
      "PUT@resetSharedHash(uid,shash?) -> plain" to "resetMetaHash/{uid}".href(hsr),
      "DELETE@deleteUser(uid) -> object:user" to "dropUser/{uid}".href(hsr),
      "PUT@flagUser(uid,flag) -> object:user" to "flagUser/{uid}".href(hsr),
      "POST@createCategory(name) -> object:category" to "makeCategory".href(hsr),
      "PUT@renameCategory(id,name) -> object:category" to "nameCategory/{id}".href(hsr),
      "DELETE@deleteCategory(id) -> object:category" to "dropCategory/{id}".href(hsr),
      "DELETE@deleteApp(aid) -> object:app" to "dropApp/{aid}".href(hsr),
      "PUT@transferAppCategory(aid,cid) -> object:[aid,old,new]" to "moveApp/{aid}".href(hsr),
      "PUT@transferAppOwner(aid,uid) -> object:[aid,old,new]" to "transferApp/{aid}".href(hsr),
      "DELETE@deleteAppUpdate(aid,rev) -> object:appUpdate" to "dropAppUpdate/{aid}/{rev}".href(hsr),
      "DELETE@deleteComment(cid) -> object:[comment,deletedSubComments]" to "dropComment/{cid}".href(hsr)
    )
  )

  /* useless but fun */
  @GetMapping("/mustError")
  @ResponseStatus(code = HttpStatus.I_AM_A_TEAPOT, reason = "see \$object")
  @ResponseBody
  fun errorJson(): Map<String, String> = mapOf("message" to "(^_^)")

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
      result["bootUptime"] = javaImpBean.uptime.toString()
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

/* makes no difference on my machine... */
@Configuration("cors")
class CorsConfiguration : WebMvcConfigurer {
  override fun addCorsMappings(registry: CorsRegistry) {
    registry.addMapping("/**")
      .allowedOrigins("*")
      .allowedMethods("GET", "POST", "DELETE", "PUT")
      .maxAge(3600)
  }
}

@Bean
fun corsConfigurer(): WebMvcConfigurer {
  return object : WebMvcConfigurerAdapter() {
    override fun addCorsMappings(registry: CorsRegistry) {
      registry.addMapping("/**").allowedOrigins("*")
    }
  }
}
