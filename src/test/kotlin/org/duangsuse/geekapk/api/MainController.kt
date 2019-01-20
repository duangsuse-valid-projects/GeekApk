package org.duangsuse.geekapk.api

import org.duangsuse.geekapk.controller.MainController
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

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
  }
}
