package nl.fontys.s3.copacoproject.business.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ObjectNotFound extends ResponseStatusException {
    public ObjectNotFound(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}