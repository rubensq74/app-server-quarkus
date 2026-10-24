package com.example.appserver.service;

import com.example.appserver.config.BooksConfig;
import com.example.appserver.persistence.BookEntity;
import com.example.appserver.repository.BookRepository;
import com.example.appserver.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;

import java.util.List;

@ApplicationScoped
public class BookService {
    private static final String BOOK_ID_PREFIX = "bk-";
    private static final long BOOK_ID_OFFSET = 1000;

    @Inject
    BookRepository bookRepository;
    @Inject
    BooksConfig booksConfig;

    @Transactional
    public List<BookResponse> listBooks() {
        Log.info("\n datos desde el config server: \n"
                + booksConfig.getValor1() + ", \n"
                + booksConfig.getValor2() + ", \n"
                + booksConfig.getValor3());

        Log.info("Consultando libros persistidos");
        return bookRepository.listAll(Sort.by("id")).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public BookResponse createBook(String title, String author) {
        BookEntity book = new BookEntity();
        book.setTitle(title);
        book.setAuthor(author);
        book.setAvailable(true);
        bookRepository.persist(book);
        return toResponse(book);
    }

    @Transactional
    public BookResponse getBookById(String id) {
        Long entityId = toEntityId(id);
        if (entityId == null) {
            return null;
        }
        return bookRepository.findByIdOptional(entityId)
                .map(this::toResponse)
                .orElse(null);
    }

    @Transactional
    public BookResponse updateBook(String id, UpdateBookRequest updateBookRequest) {
        Long entityId = toEntityId(id);
        if (entityId == null) {
            return null;
        }
        BookEntity book = bookRepository.findById(entityId);
        if (book == null) {
            return null;
        }
        if (updateBookRequest.getTitle() != null) {
            book.setTitle(updateBookRequest.getTitle());
        }
        if (updateBookRequest.getAuthor() != null) {
            book.setAuthor(updateBookRequest.getAuthor());
        }
        if (updateBookRequest.getAvailable() != null) {
            book.setAvailable(updateBookRequest.getAvailable());
        }
        return toResponse(book);
    }

    @Transactional
    public BooksAvailabilityReport getBooksAvailabilityReport() {
        long totalBooks = bookRepository.count();
        long availableBooks = bookRepository.count("available", true);
        int unavailableBooks = Math.toIntExact(totalBooks - availableBooks);

        BooksAvailabilityReport report = new BooksAvailabilityReport();
        report.setTotalBooks(Math.toIntExact(totalBooks));
        report.setAvailableBooks(Math.toIntExact(availableBooks));
        report.setUnavailableBooks(Math.toIntExact(unavailableBooks));
        return report;
    }

    @Transactional
    public BooksByAuthorReport getBooksByAuthorReport() {
        BooksByAuthorReport report = new BooksByAuthorReport();
        List<Object[]> booksByAuthor = bookRepository.getEntityManager()
                .createQuery("select book.author, count(book) from BookEntity book group by book.author order by book.author",
                        Object[].class)
                .getResultList();
        booksByAuthor.forEach(row -> report.addAuthorsItem(
                new AuthorBookCount().author((String) row[0]).count(Math.toIntExact((Long) row[1]))));
        return report;
    }

    private BookResponse toResponse(BookEntity entity) {
        BookResponse response = new BookResponse();
        response.setId(BOOK_ID_PREFIX + (entity.getId() + BOOK_ID_OFFSET));
        response.setTitle(entity.getTitle());
        response.setAuthor(entity.getAuthor());
        response.setAvailable(entity.isAvailable());
        return response;
    }

    private Long toEntityId(String id) {
        if (id == null || !id.startsWith(BOOK_ID_PREFIX)) {
            return null;
        }
        try {
            long entityId = Long.parseLong(id.substring(BOOK_ID_PREFIX.length())) - BOOK_ID_OFFSET;
            return entityId > 0 ? entityId : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
