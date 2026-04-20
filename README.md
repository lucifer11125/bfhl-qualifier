# BFHL Qualifier - MIT Java

This is a Spring Boot application created for the Bajaj Finserv Health MIT Java Qualifier.

## Functionality
- Generates a webhook by sending a dynamic POST request to the API with candidate details.
- Reads the Webhook and JWT bearer token from the API response.
- Submits the solution for Question 2 (SQL problem) to the Webhook URL using the token.

## Building and Running
The application includes a pre-built JAR. You can build it yourself by running Maven:
```sh
mvn clean package
```
Run the jar:
```sh
java -jar target/bfhl-qualifier-0.0.1-SNAPSHOT.jar
```
