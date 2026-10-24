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
            .header("request-id", "health-request-001")
            .when().get("/api/health")
            .then()
            .statusCode(200)
            .body("status", equalTo("ok"));
    }

    @Test
    void missingRequiredHeadersAreRejected() {
        given()
            .when().get("/api/health")
            .then()
            .statusCode(400)
            .body("error", equalTo("Headers requeridos faltantes o inválidos"));
    }

    @Test
    void listBooksReturnsSeedData() {
        given()
            .header("aplicacion-id", "APP1")
            .header("user-id", "SRV")
            .header("request-id", "12345678abc")
            .when().get("/api/books")
            .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("[0].id", notNullValue())
            .body("[0].title", not(emptyOrNullString()));
    }

    @Test
    void createBookReturnsCreatedBook() {
        String createdBookId = given()
            .header("aplicacion-id", "APP1")
            .header("user-id", "SRV")
            .header("request-id", "12345678abc")
            .contentType(ContentType.JSON)
            .body("{\"title\":\"The Pragmatic Programmer\",\"author\":\"Andy Hunt\"}")
            .when().post("/api/books")
            .then()
            .statusCode(200)
            .body("id", startsWith("bk-"))
            .body("title", equalTo("The Pragmatic Programmer"))
            .body("author", equalTo("Andy Hunt"))
            .body("available", equalTo(true))
            .extract().path("id");

        given()
            .header("aplicacion-id", "APP1")
            .header("user-id", "SRV")
            .header("request-id", "12345678abc")
            .contentType(ContentType.JSON)
            .body("{\"title\":\"Pragmatic Programmer, Updated\"}")
            .when().patch("/api/books/{id}", createdBookId)
            .then()
            .statusCode(200)
            .body("title", equalTo("Pragmatic Programmer, Updated"));

        given()
            .header("aplicacion-id", "APP1")
            .header("user-id", "SRV")
            .header("request-id", "12345678abc")
            .when().get("/api/books/{id}", createdBookId)
            .then()
            .statusCode(200)
            .body("title", equalTo("Pragmatic Programmer, Updated"))
            .body("author", equalTo("Andy Hunt"));
    }

    @Test
    void reportsUsePersistedBooks() {
        given()
            .header("aplicacion-id", "APP1")
            .header("user-id", "SRV")
            .header("request-id", "12345678abc")
            .when().get("/api/reports/books-availability")
            .then()
            .statusCode(200)
            .body("totalBooks", greaterThanOrEqualTo(2))
            .body("unavailableBooks", greaterThanOrEqualTo(1));

        given()
            .header("aplicacion-id", "APP1")
            .header("user-id", "SRV")
            .header("request-id", "12345678abc")
            .when().get("/api/reports/books-by-author")
            .then()
            .statusCode(200)
            .body("authors.author", hasItems("Josh Long", "Robert C. Martin"));
    }

    @Test
    void updatingMissingBookReturnsNotFound() {
        given()
            .header("aplicacion-id", "APP1")
            .header("user-id", "SRV")
            .header("request-id", "12345678abc")
            .contentType(ContentType.JSON)
            .body("{\"title\":\"Unknown book\"}")
            .when().patch("/api/books/bk-999999")
            .then()
            .statusCode(404);
    }
}
