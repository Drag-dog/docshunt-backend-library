package com.docshunt.api.spring

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springdoc.core.properties.SwaggerUiConfigParameters
import org.springframework.beans.factory.getBean
import org.springframework.beans.factory.getBeanProvider
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.web.context.WebServerInitializedEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Import

@Import(
    DocshuntWebMvcConfiguration::class,
    DocshuntExceptionHandler::class
)
class DocshuntApiAutoConfiguration : ApplicationListener<WebServerInitializedEvent> {
    override fun onApplicationEvent(event: WebServerInitializedEvent) {
        val openapiUrl = event.applicationContext.getBeanProvider<SwaggerUiConfigParameters>().ifAvailable
            ?.let { "http://localhost:${event.webServer.port}${it.path}" }
        val h2ConsoleUrl = event.applicationContext.getBeanProvider<H2ConsoleProperties>().ifAvailable?.takeIf { it.enabled }
            ?.let { "http://localhost:${event.webServer.port}${it.path}" }

        listOf(
            "docshunt api application started ${event.applicationContext.getBean<BuildProperties>().version}",
            "openapi    : ${openapiUrl ?: "disabled"}",
            "h2 console : ${h2ConsoleUrl ?: "disabled"}"
        ).joinToString("\n").let { KotlinLogging.logger { }.info { it } }
    }
}
