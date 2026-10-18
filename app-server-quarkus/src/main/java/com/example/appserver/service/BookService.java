package com.example.appserver.service;

import com.example.appserver.model.*;
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
    public BookResponse updateBook(String id, UpdateBookRequest updateBookRequest) {
        BookResponse book = books.get(id);
        if (book != null) {
            if (updateBookRequest.getTitle() != null) {
                book.setTitle(updateBookRequest.getTitle());
            }
            if (updateBookRequest.getAuthor() != null) {
                book.setAuthor(updateBookRequest.getAuthor());
            }
            if (updateBookRequest.getAvailable() != null) {
                book.setAvailable(updateBookRequest.getAvailable());
            }
        }
        return book;
    }
    public BooksAvailabilityReport getBooksAvailabilityReport() {

        int totalBooks = books.size();
        int availableBooks = (int) books.values().stream().filter(b -> b.getAvailable()==true).count();
        int unavailableBooks = totalBooks - availableBooks;

        BooksAvailabilityReport report = new BooksAvailabilityReport();
        report.setTotalBooks(totalBooks);
        report.setAvailableBooks(availableBooks);
        report.setUnavailableBooks(unavailableBooks);

        return report;
    }
    public BooksByAuthorReport getBooksByAuthorReport() {
        BooksByAuthorReport report = new BooksByAuthorReport();
        Map<String, Integer> booksByAuthor = new ConcurrentHashMap<>();

        for (BookResponse book : books.values()) {
            booksByAuthor.put(book.getAuthor(), booksByAuthor.getOrDefault(book.getAuthor(), 0) + 1);
        }
        booksByAuthor.forEach((key, value) -> {
            report.addAuthorsItem(new AuthorBookCount().author(key).count(value));
        });
        return report;
    }
}
