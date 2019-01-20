package org.duangsuse.geekapk

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

@SpringBootApplication
class GeekapkApplication

fun main(args: Array<String>) {
  println(":: Starting GeekApk Spring server @ ${Date()}")

  val spring = runApplication<GeekapkApplication>(*args)

  spring.setId("GeekApk @ ${Thread.currentThread()}")
  spring.registerShutdownHook()
}
