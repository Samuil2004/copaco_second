package nl.fontys.s3.copacoproject.business.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ComponentTypeNotFound extends ResponseStatusException {
    public ComponentTypeNotFound(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}