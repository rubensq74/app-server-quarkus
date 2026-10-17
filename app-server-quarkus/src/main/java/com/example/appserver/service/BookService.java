package com.example.appserver.service;

import com.example.appserver.openapi.model.BookResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class BookService {
    private final Map<String, BookResponse> books = new ConcurrentHashMap<>();

    public BookService() {
        BookResponse b1 = new BookResponse();
        b1.setId("bk-1001");
        b1.setTitle("Quarkus in Action");
        b1.setAuthor("Josh Long");
        b1.setAvailable(true);
        books.put(b1.getId(), b1);

        BookResponse b2 = new BookResponse();
        b2.setId("bk-1002");
        b2.setTitle("Clean Code");
        b2.setAuthor("Robert C. Martin");
        b2.setAvailable(false);
        books.put(b2.getId(), b2);
    }

    public List<BookResponse> listBooks() {
        return new ArrayList<>(books.values());
    }

    public BookResponse createBook(String title, String author) {
        String id = "bk-" + (books.size() + 1001);
        BookResponse book = new BookResponse();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setAvailable(true);
        books.put(id, book);
        return book;
    }

    public BookResponse getBookById(String id) {
        return books.get(id);
    }
}
