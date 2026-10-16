package com.example.appserver.api.generated.impl;

import com.example.appserver.api.generated.ApiApiService;
import com.example.appserver.api.generated.NotFoundException;
import com.example.appserver.openapi.model.BookResponse;
import com.example.appserver.openapi.model.CreateBookRequest;
import com.example.appserver.openapi.model.HealthResponse;
import com.example.appserver.service.BookService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.enterprise.inject.Alternative;
import jakarta.annotation.Priority;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@Alternative
@Priority(1)
@RequestScoped
public class CustomApiApiServiceImpl implements ApiApiService {

    @Inject
    BookService bookService;

    @Override
    public Response createBook(CreateBookRequest createBookRequest, SecurityContext securityContext) throws NotFoundException {
        com.example.appserver.openapi.model.BookResponse saved = bookService.createBook(createBookRequest.getTitle(), createBookRequest.getAuthor());
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @Override
    public Response getBookById(String id, SecurityContext securityContext) throws NotFoundException {
        BookResponse book = bookService.getBookById(id);
        if (book == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(java.util.Map.of("message", "Book not found")).build();
        }
        return Response.ok(book).build();
    }

    @Override
    public Response getHealth(SecurityContext securityContext) throws NotFoundException {
        HealthResponse hr = new HealthResponse();
        hr.setStatus("ok");
        return Response.ok(hr).build();
    }

    @Override
    public Response listBooks(SecurityContext securityContext) throws NotFoundException {
        // bookService returns a list of BookResponse already
        List<BookResponse> resp = bookService.listBooks();
        return Response.ok(resp).build();
    }
}
