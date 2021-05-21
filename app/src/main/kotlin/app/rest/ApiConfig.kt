package app.rest

import org.springframework.stereotype.Component

@Component
class ApiConfig {
    val title: String
        get() = "Demo App - API (resource server)"
}


