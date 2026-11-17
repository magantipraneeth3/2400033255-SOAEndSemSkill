# JWT JSON Payload + Swagger UI (Spring Boot)

A small Spring Boot 3.3.5 / Java 17 Maven project. It generates an HS256 JWT from a JSON payload and verifies the token's signature, issuer and expiration.

## Run
1. Open this folder in IntelliJ IDEA or VS Code as an existing Maven project.
2. Ensure Java 17+ and Maven are installed.
3. Run `mvn spring-boot:run`.
4. Open Swagger UI: http://localhost:8098/swagger-ui.html

## Generate a token
Use `POST /api/jwt/generate` with this JSON body:
```json
{
  "sub": "student123",
  "name": "Praneeth",
  "role": "STUDENT",
  "department": "CSE"
}
```
The response contains `accessToken`, `tokenType` and `expiresIn`.

## Verify a token
Copy `accessToken`, then call `POST /api/jwt/verify`:
```json
{
  "token": "PASTE_GENERATED_TOKEN_HERE"
}
```
A valid token returns `valid: true` and its decoded claims. A bad signature, wrong issuer or expired token returns `valid: false`.

## cURL examples
```bash
curl -X POST http://localhost:8098/api/jwt/generate \
  -H 'Content-Type: application/json' \
  -d '{"sub":"student123","role":"STUDENT","name":"Praneeth"}'

curl -X POST http://localhost:8098/api/jwt/verify \
  -H 'Content-Type: application/json' \
  -d '{"token":"PASTE_TOKEN_HERE"}'
```

## Important security notes
- This is a learning/demo project. Do not expose the signing secret or commit it to source control.
- Set a strong secret (at least 32 UTF-8 bytes) using the `JWT_SECRET` environment variable before deployment.
- HS256 uses the same secret to sign and verify. Anyone who can verify with that secret can also mint tokens.
- Do not put passwords, API keys, or other secrets in JWT payloads. JWT payloads are Base64URL-encoded, not encrypted.
- The sample rejects user-supplied `iss`, `iat`, and `exp`; the server controls those claims.
