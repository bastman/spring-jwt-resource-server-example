package app.util.jwt

import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimNames
import org.springframework.security.oauth2.jwt.JwtClaimValidator

fun <T> JwtClaimValidator<T>.toOAuth2TokenValidator(): OAuth2TokenValidator<Jwt> =
    OAuth2TokenValidator<Jwt> { jwt: Jwt ->
        this.validate(jwt)
    }

fun jwtCompoundOAuth2TokenValidator(
    vararg tokenValidators: OAuth2TokenValidator<Jwt>
): DelegatingOAuth2TokenValidator<Jwt> = DelegatingOAuth2TokenValidator(*tokenValidators)

fun jwtIssuerClaimValidator(
    claimName: String = JwtClaimNames.ISS, block: (issuer: String?) -> Boolean
): JwtClaimValidator<String?> = JwtClaimValidator(claimName) { issuer: String? -> block(issuer) }

fun jwtIssuerClaimValidator(
    claimName: String = JwtClaimNames.ISS, acceptIssuers: List<String>
): JwtClaimValidator<String?> =
    jwtIssuerClaimValidator(claimName = claimName) { issuer: String? -> issuer in acceptIssuers }

fun jwtAudienceClaimValidator(
    claimName: String = JwtClaimNames.AUD, block: (audience: List<String?>?) -> Boolean
): JwtClaimValidator<List<String?>?> = JwtClaimValidator(claimName) { audience: List<String?>? -> block(audience) }

fun jwtAudienceClaimValidator(
    claimName: String = JwtClaimNames.AUD, acceptAudiences: List<String>
): JwtClaimValidator<List<String?>?> = jwtAudienceClaimValidator(claimName = claimName) { audience: List<String?>? ->
    when {
        audience == null -> false
        audience.isEmpty() -> false
        else -> audience.any { it in acceptAudiences }
    }
}
