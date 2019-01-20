package org.duangsuse.geekapk

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan
import java.io.BufferedInputStream
import java.io.IOException
import java.util.*

@ServletComponentScan
@SpringBootApplication
class GeekapkApplication

fun main(args: Array<String>) {
  println(":: Starting GeekApk Spring server @ ${Date()}")

  val ini = GeekapkApplication::class.java.getResource("/info.ini")

  val file = ini.openStream().let(::BufferedInputStream)
  val buffer = ByteArray(file.available())

  try { file.read(buffer) }
  catch (ioe : IOException) { println("==! Failed to read INI.") }

  String(buffer).lines().forEach setProp@{
    println("==> $it")

    if (it.trimStart().startsWith('#') or it.isBlank())
      return@setProp

    val pair = it.split('=')

    if (pair.size < 2) {
      println("==! Illegal set ${pair.toList()}")
      return@setProp
    }

    val (key, value) = (pair[0] to pair[1])
    System.setProperty(key, value)

    println("== Setup ($key) to ($value)")
  }

  println(":: Bootstrap SpringBoot Application ${GeekapkApplication::class}")

  val spring = runApplication<GeekapkApplication>(*args)

  spring.setId("GeekApk @ ${Thread.currentThread()}")
  spring.registerShutdownHook()
}
