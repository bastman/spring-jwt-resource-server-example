package app.config.auth

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "app.auth")
@ConstructorBinding
data class AuthConfig(
    val jwksUri: String,
    val acceptIssuers: List<String>,
    val acceptAudiences: List<String>
)
