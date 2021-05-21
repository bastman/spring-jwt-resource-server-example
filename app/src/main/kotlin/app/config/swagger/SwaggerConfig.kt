package app.config.swagger

import app.rest.ApiConfig
import mu.KLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.ApiKey
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.SecurityReference
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SwaggerConfig(
    private val apiConfig: ApiConfig
) {
    companion object : KLogging()

    private val apiKeyBearerAuth = ApiKey("Bearer <token>", "Authorization", "header")
    private val authScopes: List<AuthorizationScope> = emptyList()

    @Bean
    fun mainApi(): Docket = apiConfig.toDocket()
        .securitySchemes(listOf(apiKeyBearerAuth))
        .securityContexts(
            listOf(
                securityContextFromReferences(
                    securityReferences = listOf(
                        SecurityReference(apiKeyBearerAuth.name, *(authScopes.toTypedArray()))
                    )
                )
            )
        )
        .useDefaultResponseMessages(false)
        .select()
        .apis(RequestHandlerSelectors.basePackage(apiConfig.getBasePackageName()))
        .build()
}


private fun ApiConfig.getBasePackageName(): String = this::class.java.`package`.name
private fun ApiConfig.toApiInfo(): ApiInfo = springfox.documentation.builders.ApiInfoBuilder()
    .title(this.title)
    .build()

private fun ApiConfig.toDocket(): Docket = Docket(DocumentationType.SWAGGER_2)
    .apiInfo(this.toApiInfo())

private fun securityContextFromReferences(
    securityReferences: List<SecurityReference>
): SecurityContext = SecurityContext
    .builder()
    .securityReferences(securityReferences)
    .forPaths { input -> true } // springfox 2.x
    // .operationSelector { ctx -> true } // springfox 3.x
    .build()
