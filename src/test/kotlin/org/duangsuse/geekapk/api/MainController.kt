package org.duangsuse.geekapk.api

import org.duangsuse.geekapk.controller.MainController
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.matchers.JUnitMatchers.containsString
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootVersion
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.core.SpringVersion
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/*
* SUMMARY
*
* main controller(GET)
*
* object index: /
* object serverDetail: /detail
* plain serverVersion: /serverVersion
* plain serverDesc: /serverDesc
* string.datetime serverBootUp: /serverBoot
*
* */
@RunWith(SpringRunner::class)
@WebMvcTest(MainController::class)
class MainController {
  @Autowired
  private lateinit var mock: MockMvc

  @Test
  fun hasIndex() {
    mock.perform(get("/")
      .contentType(MediaType.APPLICATION_JSON_UTF8))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$").isMap)
      .andExpect(jsonPath("$.server.upTime").exists())
      .andExpect(jsonPath("$.server.index").isNotEmpty)
  }

  @Test
  fun givesDetail() {
    mock.perform(get("/detail")
      .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.encoding").isString)
      .andExpect(jsonPath("$.os").exists())
      .andExpect(jsonPath("$.spring").value(SpringBootVersion.getVersion()!!))
  }

  @Test
  fun givesVersion() {
    mock.perform(get("/serverVersion")
      .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk)
      .andExpect(content().encoding("UTF-8"))
      .andExpect { s -> s.toString().contains("1.") }
  }

  @Test
  fun givesDesc() {
    mock.perform(get("/serverDesc")
      .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk)
      .andExpect { c -> c.response.contentAsString.isNotEmpty() }
  }

  @Test
  fun givesStartTime() {
    mock.perform(get("/serverBoot")
      .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$").isString)
  }

  @Test
  fun internalGivesMustError() {
    mock.perform(get("/mustError")
      .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isIAmATeapot) /* just for fun */
  }

  @Test
  fun httpGivesCORSHeader() {
    mock.perform(get("/serverVersion")
      .contentType(MediaType.APPLICATION_JSON))
      .andExpect(header().exists("Access-Control-Allow-Origin"))
  }
}
