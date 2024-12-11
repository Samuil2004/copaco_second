package nl.fontys.s3.copacoproject.business.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidInputException extends ResponseStatusException {
    public InvalidInputException() {
        super(HttpStatus.BAD_REQUEST, "Invalid input");
    }

    public InvalidInputException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
