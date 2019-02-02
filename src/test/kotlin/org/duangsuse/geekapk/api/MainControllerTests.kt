package org.duangsuse.geekapk.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.duangsuse.geekapk.controller.MainController
import org.duangsuse.geekapk.middleware.CorsFilter
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootVersion
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import javax.servlet.annotation.WebFilter

/*
* SUMMARY
*
* main controller(GET)
*
* object index: /
* object serverDetail: /serverDetail
* plain serverVersion: /serverVersion
* plain serverDescription: /serverDescription
* string.datetime serverBootUp: /serverBoot
*
* */
@RunWith(SpringRunner::class)
@WebMvcTest(MainController::class)
class MainControllerTests {
  @Autowired
  private lateinit var mock: MockMvc

  @Test
  fun hasIndex() {
    mock.perform(get("/")
      .contentType(MediaType.APPLICATION_JSON_UTF8))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$").isMap)
      .andExpect(jsonPath("$.server.description").exists())
      .andExpect(jsonPath("$.admin.schema").isNotEmpty)
  }

  @Test
  fun indexShowsHref() {
    mock.perform(get("/"))
      .andExpect(jsonPath("$.admin.['POST@createUser(username) -> object:user']").value("http://127.0.0.1:80/admin/makeUser"))
  }

  @Test
  fun givesDetail() {
    mock.perform(get("/serverDetail")
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
  fun givesDescription() {
    mock.perform(get("/serverDescription")
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
      //.andExpect(header().exists("Access-Control-Allow-Origin"))
      /* NOTE: Middleware `CorsWorkaround' is invisible when mocking... */

    assert(CorsFilter::class.annotations.map(Annotation::toString).single().contains(WebFilter::class.simpleName!!))
  }

  @Test
  fun indexAPILinksAreRight() {
    mock.perform(get("/")).andDo {
      val mapper = ObjectMapper()
      val json = mapper.readValue(it.response.contentAsString, HashMap<String, HashMap<String, String>>()::class.java)
      val serverBase = json["server"]!!["basePath"]!!
      val serverInfo = json["server"]!!["detailedInfo() -> object:<prop,desc>"]!!

      mock.perform(get(serverBase)).andExpect(status().isOk)
      mock.perform(get("$serverInfo/").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk)
        .andExpect(jsonPath("$").isMap)
    }
  }
}
