package com.example.appserver.api;

import com.example.appserver.api.generated.ApiApi;
import com.example.appserver.openapi.model.BookResponse;
import com.example.appserver.openapi.model.CreateBookRequest;
import com.example.appserver.service.BookService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/api")
public class ApiResource implements ApiApi {

    @Inject
    BookService bookService;

    @Override
    public Response createBook(CreateBookRequest createBookRequest) {
        BookResponse created = bookService.createBook(createBookRequest.getTitle(), createBookRequest.getAuthor());
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @Override
    public Response getBookById(String id) {
        BookResponse book = bookService.getBookById(id);
        if (book == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(java.util.Map.of("message", "Book not found")).build();
        }
        return Response.ok(book).build();
    }

    @Override
    public Response getHealth() {
        BookResponse health = bookService.listBooks().stream().findFirst().orElse(null);
        if (health == null) {
            return Response.ok(java.util.Map.of("status", "ok")).build();
        }
        return Response.ok(java.util.Map.of("status", "ok")).build();
    }

    @Override
    public Response listBooks() {
        return Response.ok(bookService.listBooks()).build();
    }
}
