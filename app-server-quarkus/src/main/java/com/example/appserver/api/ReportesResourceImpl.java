package com.example.appserver.api;

import com.example.appserver.model.BooksAvailabilityReport;
import com.example.appserver.model.BooksByAuthorReport;
import com.example.appserver.resources.ReportesResource;
import com.example.appserver.service.BookService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ReportesResourceImpl implements ReportesResource {

    @Inject
    BookService bookService;

    @Override
    public BooksAvailabilityReport getBooksAvailabilityReport(String aplicacionId, String userId, String requestId) {
        return bookService.getBooksAvailabilityReport();
    }

    @Override
    public BooksByAuthorReport getBooksByAuthorReport(String aplicacionId, String userId, String requestId) {
        return bookService.getBooksByAuthorReport();
    }
}
