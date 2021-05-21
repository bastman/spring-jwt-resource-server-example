package app.rest

import mu.KLogging
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.annotations.ApiIgnore

@RestController
class ResourceServerApiController(
) {
    companion object : KLogging()

    @GetMapping("/api/me")
    fun me(
        @ApiIgnore authentication: JwtAuthenticationToken
        // ,@ApiIgnore @AuthenticationPrincipal  oidcUser: OidcUser
    ): Any {

        val jwt: Jwt = authentication.token

        return mapOf(
            "foo" to "bar",
            "auth" to mapOf(
                "name" to authentication.name,
                "authorities" to authentication.authorities,
                "tokenAttributes" to authentication.tokenAttributes,
                "token" to authentication.token as Jwt, // Jwt
                "principal" to authentication.principal as Jwt, // Jwt
                "credentials" to authentication.credentials as Jwt, // Jwt
                "details" to authentication.details
            )
        )
    }

}
