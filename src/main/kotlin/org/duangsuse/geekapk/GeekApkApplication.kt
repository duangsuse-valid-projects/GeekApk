package org.duangsuse.geekapk

import org.duangsuse.geekapk.controller.MainController
import org.duangsuse.geekapk.migrate.*
import org.duangsuse.geekapk.repositories.*
import org.duangsuse.geekapk.repositories.relations.CollabRelRepository
import org.duangsuse.geekapk.repositories.relations.FollowRelRepository
import org.duangsuse.geekapk.repositories.relations.StarRelRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.io.BufferedInputStream
import java.io.IOException
import java.util.*

/**
 * Database source CDI consumer
 */
@Component
class DbDataSource {
  fun dumpDb(a: MigrationRepositoriesA, b: MigrationRepositoriesB, c: MigrationRepositoriesC) {
    val migration = Migrates.Migration(a, b, c)
    Migrates.dumpAll(migration)
  }
}

/**
 * The GeekApk Spring Application
 *
 * @version 1.0
 * @author duangsuse
 */
@ServletComponentScan
@SpringBootApplication
@EnableAutoConfiguration
class GeekApkApplication {
  @Autowired(required = false) lateinit var apps: AppRepository
  @Autowired(required = false) lateinit var revs: AppUpdateRepository
  @Autowired(required = false) lateinit var comments: CommentRepository
  @Autowired(required = false) lateinit var users: UserRepository
  @Autowired(required = false) lateinit var timeline: TimelineRepository
  @Autowired(required = false) lateinit var notifications: NotificationRepository
  @Autowired(required = false) lateinit var categories: CategoryRepository
  @Autowired(required = false) lateinit var collaborators: CollabRelRepository
  @Autowired(required = false) lateinit var follow: FollowRelRepository
  @Autowired(required = false) lateinit var star: StarRelRepository

  fun rel() = RelationMigrationRepositories(collaborators, follow, star)

  fun a() = MigrationRepositoriesA(apps, revs, comments)
  fun b() = MigrationRepositoriesB(users, timeline, notifications)
  fun c() = MigrationRepositoriesC(categories, rel())

  /**
   * Print welcome message and (may process initialization files)
   */
  @Bean fun initialize() = CommandLineRunner {
    args ->
    println(":: Post-starting GeekApk Spring server @ ${Date()}")

    handleCommandLine(args)

    if (!iniLoaded) {
      initializeINIConfig()

      // load user config
      initializeINIShadowChain()
    } else println("==! INI Already loaded, skipping...")

    println(":: Bootstrap SpringBoot Application ${GeekApkApplication::class} with configuration below:")

    fun escapePrintln(entry: Map.Entry<Any, Any>) {
      print("  ")
      println("${entry.key}: ${entry.value}")
    }
    System.getProperties().filter { it.key.toString().startsWith("geekapk") }.forEach(::escapePrintln)

    println("GeekApk Server is now running...")
  }

  /**
   * Why not to use shift-dispatch method to make this more flexible?
   */
  fun handleCommandLine(args: Array<String>) {
    when (args.size) {
      0 -> return
      1 -> when (args.first()) {
        "--version", "version" -> println(MainController.programVersion)
        "--help", "help" -> println("Program usage: geekapk [version|help|licence]" +
          "Find help at https://github.com/duangsuse/GeekApk")
        "--licence", "licence" -> println("Copyright (C) 2019 duangsuse, GeekApk Spring, licenced under GNU AGPL-3.0")
        "dump!" -> DbDataSource().dumpDb(a(), b(), c())
        else -> println("Warning: unknown operation")
      }
      else -> println("Warning: unexpected argument vector length: ${args.size}")
    }
  }

  companion object {
    /**
     * Is initialization file processed?
     */
      @Volatile var iniLoaded: Boolean = false


    /**
    * The application entrance
    */
    @JvmStatic
    fun main(args: Array<String>) {
      // Should set initialize properties first
      initializeINIConfig()
      initializeINIShadowChain()

      val spring = runApplication<GeekApkApplication>(*args)

      spring.setId("GeekApk @ ${Thread.currentThread()}")
      spring.registerShutdownHook()
    }
  }
}

/**
 * Environment variable to store external ini paths `:`
 */
const val geekIni = "GEEKAPK_INI"

/**
 * Process initialize ini chain
 */
private fun initializeINIShadowChain() {
  val iniList = mutableSetOf("/geekapk.ini", "/translations.ini")
  if (System.getenv().containsKey(geekIni))
    iniList.addAll(System.getenv(geekIni).split(':'))

  iniList.forEach {
    println("=== Processing external properties file $it ===")
    val configFile = GeekApkApplication::class.java.getResource(it)
    parseGeekINIBuffer(configFile.openStream().let(::BufferedInputStream).readBytes())
  }
  GeekApkApplication.iniLoaded = true
}

/**
 * Process default config file
 */
internal fun initializeINIConfig() {
  val ini = GeekApkApplication::class.java.getResource("/info.ini")

  val file = ini.openStream().let(::BufferedInputStream)
  val buffer = ByteArray(file.available())

  try {
    file.read(buffer)
  } catch (ioe: IOException) {
    println("==! Failed to read initialization INI.")
  }

  // load defaults
  parseGeekINIBuffer(buffer)
}

/**
 * Simple UNIX-style naive text processing util with unbelievable time complexity
 *
 * Sets system properties
 *
 * @author duangsuse
 */
internal fun parseGeekINIBuffer(buffer: ByteArray) {
  var tag = ""

  String(buffer).lines().forEach setProp@{
    println("==> $it")

    if (it.trimStart().startsWith('#') or it.isBlank())
      return@setProp

    if (it.startsWith('[') && it.endsWith(']')) {
      tag = it.substring(1 until it.lastIndex)
      println("== Configure section [$tag]")
      return@setProp
    }

    val pair = it.split('=')

    if (pair.size < 2) {
      println("==! Illegal set ${pair.toList()}")
      return@setProp
    }

    val (key, value) = (pair[0] to pair[1])
    val realPre = if (tag.isEmpty()) "" else tag.plus(".")
    System.setProperty("$realPre$key", value)

    println("== Setup $tag($key) to ($value)")
  }
}
