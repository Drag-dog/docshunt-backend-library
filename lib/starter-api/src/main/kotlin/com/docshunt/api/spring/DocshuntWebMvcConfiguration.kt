package com.docshunt.api.spring

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.math.BigDecimal
import java.math.BigInteger

@EnableConfigurationProperties(DocshuntWebMvcConfiguration.CorsProperties::class)
class DocshuntWebMvcConfiguration(
    private val corsProperties: CorsProperties,
) : WebMvcConfigurer, Jackson2ObjectMapperBuilderCustomizer {

    @ConfigurationProperties(prefix = "be.server.cors")
    class CorsProperties(
        val allowedOrigins: List<String> = emptyList(),
        val allowedOriginPatterns: List<String> = emptyList()
    )

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(*corsProperties.allowedOrigins.toTypedArray())
            .allowedOriginPatterns(*corsProperties.allowedOriginPatterns.toTypedArray())
            .allowedMethods(CorsConfiguration.ALL)
            .allowedHeaders(CorsConfiguration.ALL)
            .allowCredentials(true)
    }

    /**
     * this Bean can customize spring boot auto-configured ObjectMapper.
     * you can also customize via yml configuration. spring.jackson.x
     */
    override fun customize(jacksonObjectMapperBuilder: Jackson2ObjectMapperBuilder) {
        jacksonObjectMapperBuilder
            .failOnUnknownProperties(false)
            .serializationInclusion(JsonInclude.Include.NON_ABSENT)
            .featuresToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN) // BigDecimal 지수 표기법 사용 하지 않는다.
            .postConfigurer {
                it.configOverride(BigInteger::class.java).format = JsonFormat.Value.forShape(JsonFormat.Shape.STRING)
                it.configOverride(BigDecimal::class.java).format = JsonFormat.Value.forShape(JsonFormat.Shape.STRING)
            }
    }
}
