# Spring XML JWT Microservices

A Spring Boot learning project demonstrating communication between two independent microservices using XML, JWT authentication, correlation IDs, retries, timeouts, and structured error handling.

The project consists of two Spring Boot applications:

- **Service A** - the outward-facing service that receives requests and communicates with Service B.
- **Service B** - processes XML messages and handles JWT generation and validation.

## Project Structure

```text
spring-xml-jwt-microservices/
├── service-a/
├── service-b/
└── README.md
```

Each service is an independent Spring Boot application with its own Maven configuration and test suite.

## Technologies

- Java 25
- Spring Boot 4.1.2
- Maven
- JAXB
- JWT
- Java HttpClient
- JUnit
- SLF4J / MDC

## Architecture

The basic request flow is:

```text
Client
  |
  v
Service A :8080
  |
  | XML
  | X-Correlation-ID
  | Authorization: Bearer <JWT>
  v
Service B :8081
  |
  v
XML Response
  |
  v
Service A
  |
  v
Client
```

Service A acts as the entry point and Service B performs the downstream XML processing.

## Service A

Service A runs on:

```text
http://localhost:8080
```

Its main endpoint is:

```text
POST /api/message
```

Service A is responsible for:

- Receiving XML requests.
- Unmarshalling XML into Java objects using JAXB.
- Generating and propagating correlation IDs.
- Calling Service B using Java's built-in `HttpClient`.
- Sending JWTs to Service B when available.
- Capturing a JWT returned by Service B.
- Applying configurable connection and request timeouts.
- Retrying eligible communication and server-side failures.
- Not retrying client-side failures.
- Unmarshalling the response from Service B.
- Returning the processed response to the client.

## Service B

Service B runs on:

```text
http://localhost:8081
```

Its XML endpoint is:

```text
POST /api/xml
```

Service B is responsible for:

- Receiving XML from Service A.
- Unmarshalling XML using JAXB.
- Validating incoming messages.
- Processing the message.
- Marshalling the result back into XML.
- Generating a JWT when one has not been supplied.
- Validating supplied JWTs.
- Rejecting invalid or expired JWTs.
- Participating in correlation ID propagation and logging.

A successfully processed message has its content prefixed with:

```text
Processed:
```

## JWT Authentication

The services demonstrate a simple JWT exchange.

When Service B receives a request without a JWT, it generates a token and returns it to Service A.

Service A can then send the token on a subsequent request using:

```text
Authorization: Bearer <token>
```

Service B validates supplied tokens before processing the request.

Invalid or expired tokens are rejected.

The JWT lifetime is configurable.

## Local Secret Configuration

JWT secrets should not be committed to Git.

Local secret values can be placed in:

```text
service-b/src/main/resources/application-local.properties
```

This file is deliberately excluded from version control.

Start Service B using the local Spring profile when local secret configuration is required.

For example:

```powershell
.\mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

> **Important:** Do not commit real JWT secrets or other credentials to the repository.

## Correlation IDs

Requests are traced across both microservices using:

```text
X-Correlation-ID
```

Service A creates a correlation ID for each transaction and sends it to Service B.

Both services place the correlation ID into SLF4J MDC so related log entries can be identified across service boundaries.

MDC state is cleaned up after request processing.

## Retries and Timeouts

Communication from Service A to Service B includes configurable retry and timeout behaviour.

The retry policy is designed to:

- Retry communication failures.
- Retry eligible server-side failures.
- Avoid retrying client-side `4xx` failures.
- Stop after the configured maximum number of attempts.
- Report retry exhaustion through a dedicated exception.

Connection and request timeout values are also configurable.

Example configuration:

```properties
service-b.retry.max-attempts=3
service-b.retry.delay-ms=2000
service-b.http.connect-timeout-ms=2000
service-b.http.request-timeout-ms=5000
```

## Running the Project

The services should be started separately.

### 1. Start Service B

From the `service-b` directory:

```powershell
.\mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Service B starts on port `8081`.

### 2. Start Service A

From the `service-a` directory:

```powershell
.\mvnw spring-boot:run
```

Service A starts on port `8080`.

## Example Request

Send an XML request to Service A:

```http
POST http://localhost:8080/api/message
Content-Type: application/xml
```

Example body:

```xml
<message>
    <content>Hello World</content>
</message>
```

The request flows through:

```text
Client -> Service A -> Service B -> Service A -> Client
```

Service B processes the message and returns the resulting XML through Service A.

## Running Tests

Tests can be run independently for each service.

From either service directory:

```powershell
.\mvnw test
```

The test suites cover areas including:

- XML marshalling and unmarshalling.
- Message validation.
- Controller behaviour.
- JWT generation and validation.
- Invalid and expired tokens.
- Service-to-service communication.
- Retry behaviour.
- Timeout configuration.
- Exception handling.
- Correlation ID handling.

## Error Handling

The project includes handling for common failure scenarios, including:

- Malformed XML.
- Invalid message content.
- Invalid JWTs.
- Expired JWTs.
- Client-side HTTP failures.
- Server-side HTTP failures.
- Communication failures.
- Retry exhaustion.

The services distinguish failures that can reasonably be retried from failures where retrying would not help.

## Design Decisions

### Independent Microservices

Service A and Service B are kept as separate Spring Boot applications.

Although some small models and XML-related concepts are shared, a separate common Maven module has deliberately not been introduced because the amount of duplicated contract code does not currently justify the additional coupling and project complexity.

### Java HttpClient

Service A uses Java's built-in `java.net.http.HttpClient` for communication with Service B.

The client is configured centrally, injected where required, and reused rather than creating a new HTTP client for every request.

### Configuration

Runtime behaviour such as retry limits, retry delays, HTTP timeouts, JWT lifetime, service URLs, and secrets is configurable rather than being hard-coded into application logic.

