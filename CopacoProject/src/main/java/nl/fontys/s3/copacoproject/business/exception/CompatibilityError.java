package nl.fontys.s3.copacoproject.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CompatibilityError extends ResponseStatusException {
    public CompatibilityError(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}