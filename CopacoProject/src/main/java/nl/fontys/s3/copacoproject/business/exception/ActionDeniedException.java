package nl.fontys.s3.copacoproject.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ActionDeniedException extends ResponseStatusException {
    public ActionDeniedException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
