package app.config.security

import app.config.auth.AuthConfig
import app.util.jwt.jwtAudienceClaimValidator
import app.util.jwt.jwtCompoundOAuth2TokenValidator
import app.util.jwt.jwtIssuerClaimValidator
import app.util.jwt.toOAuth2TokenValidator
import mu.KLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain

/**
 * see: https://github.com/hantsy/spring-security-auth0-sample/blob/master/api/src/main/java/com/example/demo/config/SecurityConfig.java
 */

@Configuration(proxyBeanMethods = false)
class SecurityConfig(
    private val authConfig: AuthConfig
) {
    companion object : KLogging()

    private val endpointsFullyAuthenticated: List<String> = listOf("/api/**")
    private val endpointsUnsecured: List<String> = listOf(
        "/",
        "/info",

        // springfox-swagger2 (2.9.x)
        "/v2/api-docs",
        "/configuration/ui",
        "/swagger-resources/**",
        "/configuration/security",
        "/swagger-ui.html",
        "/webjars/**",

        // custom: fake-authorization-server
    )

    @Bean
    fun springWebFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeRequests {
                it
                    .antMatchers(*(endpointsUnsecured.toTypedArray())).permitAll()
                    .antMatchers(*(endpointsFullyAuthenticated.toTypedArray())).fullyAuthenticated()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { resourceServer(it, authConfig) }
            .build()
    }

    private fun resourceServer(rs: OAuth2ResourceServerConfigurer<HttpSecurity?>, authConfig: AuthConfig): Unit {
        rs.jwt {
            val validator: DelegatingOAuth2TokenValidator<Jwt> = jwtValidator(
                acceptIssuers = authConfig.acceptIssuers,
                acceptAudiences = authConfig.acceptAudiences
            )
            val decoder: NimbusJwtDecoder = jwtDecoder(jwkSetUri = authConfig.jwksUri)
            decoder.setJwtValidator(validator)
            it.decoder(decoder)
        }
        logger.info { "==== jwt resource server ===" }
        logger.info { "=> accept issuers: ${authConfig.acceptIssuers} audience: ${authConfig.acceptAudiences}" }
    }

    private fun jwtDecoder(jwkSetUri: String): NimbusJwtDecoder {
        // e.g.: "http://localhost:8080/.well-known/jwks.json"
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build()
    }

    private fun jwtValidator(
        acceptIssuers: List<String>, acceptAudiences: List<String>
    ): DelegatingOAuth2TokenValidator<Jwt> {
        val defaultValidator: OAuth2TokenValidator<Jwt> = JwtValidators.createDefault()
        val issuerValidator: OAuth2TokenValidator<Jwt> = jwtIssuerClaimValidator(acceptIssuers = acceptIssuers)
            .toOAuth2TokenValidator()
        val audienceValidator: OAuth2TokenValidator<Jwt> =
            jwtAudienceClaimValidator(acceptAudiences = acceptAudiences)
                .toOAuth2TokenValidator()

        return jwtCompoundOAuth2TokenValidator(
            defaultValidator, issuerValidator, audienceValidator
        )
    }

}
