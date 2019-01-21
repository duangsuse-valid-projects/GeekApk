package org.duangsuse.geekapk.controller

import org.springframework.boot.SpringBootVersion
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import java.lang.management.ManagementFactory
import java.util.*
import javax.servlet.http.HttpServletRequest

@Controller
class MainController {
  fun String.href(req: HttpServletRequest) = "${req.scheme}://${req.localAddr}:${req.localPort}/$this".intern()

  /* This is API document(R */
  @GetMapping("/")
  @ResponseBody
  /* must be */ fun apiHint(hsr: HttpServletRequest): Map<String, Map<String, String>> = mapOf(
    "server" to mapOf(
      description to "GeekApk server information related APIs",
      schema to "ALL INTERFACES REQUIRES NONE",
      "index() -> object<category,object<operation,linkTemplate>>" to "".href(hsr),
      "version() -> plain" to "serverVersion".href(hsr),
      "desc() -> plain" to "serverDesc".href(hsr),
      "upTime() -> (string|number):datetime" to "serverBoot".href(hsr),
      "detailedInfo() -> object:<prop,desc>" to "detail".href(hsr)
    ),

    "admin" to mapOf(
      description to "GeekApk community administration functions",
      /* `admin flag` in GeekApk JUST a flag, modTok is required for non-deleteApp,transAppCategory,deleteComment operations */
      schema to "ALL INTERFACES BUT (deleteApp,transferAppCategory,deleteComment,flagUser)#a REQUIRES Cookie(gaModTok) BE " +
        "`geekapk admin token` AND (Cookie(gaUser), Cookie(gaHash) BE `valid login`)#b; #a REQUIRES REWRITE #b `valid superuser login`",
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
      "DELETE@deleteComment(cid) -> object:[comment,deletedSubCommentsCount]" to "dropComment/{cid}".href(hsr)
    ),

    "category" to mapOf( /* trivially */
      description to "Application Categories",
      schema to "ALL INTERFACES REQUIRES NONE",
      "categoryList() -> array:category" to "category/all".href(hsr),
      "categoryName(id) -> plain" to "category/{id}".href(hsr)
    ),

    "user" to mapOf(
      description to "GeekApk user APIs",
      schema to "INTERFACE updateUser REQUIRES Cookie(gaHash) BE `valid hash`",
      "readUser(id) -> object:user" to "user/{id}".href(hsr),
      "PUT@updateUser(id,prop{username,nickname,avatar,bio,metaApp},value) -> [user,prop,old,new]" to "user/{id}".href(hsr),
      "PUT@resetHash(id,shash,hash) -> [id,newShash,newHash]" to "user/{id}/hash".href(hsr),
      "checkHash(id,hash) -> [valid,message]" to "user/{id}/checkHash".href(hsr),
      "listUser(sort?{created,followers},sliceFrom?,sliceTo?) -> array:object:user" to "user/all".href(hsr),
      "listMetaUser(sort?{created,followers},sliceFrom?,sliceTo?) -> array:object:user" to "user/allHasMetaApp".href(hsr),
      "searchUser(type?{username,nickname,bio},kw,sort?{created,followers}) -> array:object:user" to "user/search/{kw}".href(hsr)
    ),

    "timeline" to mapOf(
      description to "GeekApk user timeline functions"
    ),
    "notification" to mapOf(
      description to "GeekApk user private notification APIs"
    ),
    "app" to mapOf(
      description to "GeekApk Android application metadata APIs"
    ),
    "appUpdate" to mapOf(
      description to "GeekApk Android application reversion metadata APIs"
    ),
    "comment" to mapOf(
      description to "GeekApk User->App comment interfaces"
    ),

    "follow" to mapOf(
      description to "GeekApk User->User follow relation operations",
        schema to "ALL NON GET INTERFACE REQUIRES Cookie(gaUser), Cookie(gaHash) BE `valid login`",
        "POST@follow(uid)" to "follow/{uid}".href(hsr),
        "DELETE@unfollow(uid)" to "follow/{uid}".href(hsr),
        "followers(uid)" to "follow/followers/{uid}".href(hsr),
        "following(uid)" to "follow/{uid}".href(hsr)
    ),

    "star" to mapOf(
      description to "GeekApk User->App star functions",
      schema to "ALL NON GET INTERFACE REQUIRES Cookie(gaUser), Cookie(gaHash) BE `valid login`",
      "POST@star(aid) -> [oldCount,newCount]" to "star/{aid}".href(hsr),
      "DELETE@unStar(aid) -> [oldCount,newCount]" to "star/{aid}".href(hsr),
      "stargazers(aid) -> array:object:user" to "star/{aid}".href(hsr),
      "stars(uid) -> array:object:app" to "star/user/{uid}".href(hsr)
    )
  )

  /* useless but fun */
  @GetMapping("/mustError")
  @ResponseStatus(code = HttpStatus.I_AM_A_TEAPOT, reason = "see \$object")
  @ResponseBody /* body not concerned by Spring */
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
  fun serverUptime(): Date = bootUpAt()

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

    const val programVersion = "0.1.0"
    const val description = "description"
    const val schema = "schema"
    val serverDesc = """
      > Spring GeekApk Server by duangsuse, the dying Server for GeekApk.
      > Spring version: 2.1.2
      > Kotlin version: 1.2.7
      """.trimMargin(">").intern()

    val serverDetail = makeServerDetail()
    val bootUpAt = { Date() } /* must be call-by-need */
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

@Configuration /* not efficient, don't enable this in production environment */
class MainJsonPrettyPrintConfiguration : WebMvcConfigurationSupport() {
  override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
    for (converter in converters)
      if (converter is MappingJackson2HttpMessageConverter)
        (converter as? MappingJackson2HttpMessageConverter)?.run { setPrettyPrint(true) }
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
