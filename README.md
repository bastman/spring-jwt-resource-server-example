# spring-jwt-resource-server-example

- resource server: https://github.com/bastman/spring-jwt-resource-server-example
- authorization server: https://github.com/bastman/jwt-fake-authorization-server

## docker

```
# build image
$ ./gradlew bootBuildImage

# run 
$ docker-compose up


```

## endpoints

- swagger-ui: http://localhost:8080/swagger-ui.html
- GET /api/me
    - describe the current user (derived from jwt)
    - expose the claims of the jwt
    - requires bearer-auth
    
## bearer auth

http://localhost:8080/swagger-ui.html

click "authorize"
enter "Bearer <your token>"
click "login"

You may want to get a valid bearer token issued from an authorization server
(e.g.: https://github.com/bastman/jwt-fake-authorization-server )


