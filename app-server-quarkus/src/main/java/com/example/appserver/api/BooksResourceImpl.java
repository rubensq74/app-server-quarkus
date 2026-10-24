package com.example.appserver.api;

import com.example.appserver.model.BookResponse;
import com.example.appserver.model.CreateBookRequest;
import com.example.appserver.model.UpdateBookRequest;
import com.example.appserver.resources.BooksResource;
import com.example.appserver.service.BookService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BooksResourceImpl implements BooksResource {

    @Inject
    BookService bookService;

    @Override
    public BookResponse createBook(String aplicacionId, String userId, String requestId, CreateBookRequest createBookRequest) {
        return bookService.createBook(createBookRequest.getTitle(), createBookRequest.getAuthor());
    }

    @Override
    public BookResponse getBookById(String id, String aplicacionId, String userId, String requestId) {
        BookResponse book = bookService.getBookById(id);
        if (book == null) {
            throw new jakarta.ws.rs.NotFoundException();
        }
        return book;
    }

    @Override
    public java.util.List<BookResponse> listBooks(String aplicacionId, String userId, String requestId) {
        return bookService.listBooks();
    }

    @Override
    public BookResponse updateBook(String id, String aplicacionId, String userId, String requestId, UpdateBookRequest updateBookRequest) {
        BookResponse book = bookService.updateBook(id, updateBookRequest);
        if (book == null) {
            throw new jakarta.ws.rs.NotFoundException();
        }
        return book;
    }
}
