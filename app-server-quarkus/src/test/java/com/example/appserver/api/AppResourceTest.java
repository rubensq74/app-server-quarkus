package com.example.appserver.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class AppResourceTest {

    @Test
    void healthEndpointReturnsOk() {
        given()
            .header("aplicacion-id", "APP1")
            .header("user-id", "SRV")
            .header("request-id", "etfg45cieihfnmujieriier")
            .when().get("/api/health")
            .then()
            .statusCode(200)
            .body("status", equalTo("ok"));
    }

    @Test
    void listBooksReturnsSeedData() {
        given()
            .header("aplicacion-id", "APP1")
            .header("user-id", "SRV")
            .header("request-id", "etfg45cieihfnmujieriier")
            .when().get("/api/books")
            .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("[0].id", notNullValue())
            .body("[0].title", not(emptyOrNullString()));
    }

    @Test
    void createBookReturnsCreatedBook() {
        given()
            .header("aplicacion-id", "APP1")
            .header("user-id", "SRV")
            .header("request-id", "etfg45cieihfnmujieriier")
            .contentType(ContentType.JSON)
            .body("{\"title\":\"The Pragmatic Programmer\",\"author\":\"Andy Hunt\"}")
            .when().post("/api/books")
            .then()
            .statusCode(201)
            .body("title", equalTo("The Pragmatic Programmer"))
            .body("author", equalTo("Andy Hunt"))
            .body("available", equalTo(true));
    }
}
