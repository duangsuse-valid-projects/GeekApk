package org.duangsuse.geekapk.middleware

import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport

@Configuration /* not efficient, don't enable this in production environment */
class MainJsonPrettyPrintConfiguration : WebMvcConfigurationSupport() {
  override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
    for (converter in converters)
      if (converter is MappingJackson2HttpMessageConverter)
        (converter as? MappingJackson2HttpMessageConverter)?.run { setPrettyPrint(true) }
  }
}
