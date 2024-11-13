package nl.fontys.s3.copacoproject.business.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ObjectExistsAlreadyException extends ResponseStatusException {
    public ObjectExistsAlreadyException() {
        super(HttpStatus.CONFLICT, "This object already exists");
    }
    public ObjectExistsAlreadyException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
