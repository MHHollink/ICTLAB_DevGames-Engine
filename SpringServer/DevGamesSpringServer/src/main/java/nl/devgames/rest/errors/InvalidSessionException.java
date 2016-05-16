package nl.devgames.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class InvalidSessionException extends RuntimeException {
    public InvalidSessionException(String message, Object... args) {
        super(String.format(message, args));
    }
}
