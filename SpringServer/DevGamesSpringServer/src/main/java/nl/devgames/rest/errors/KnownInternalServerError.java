package nl.devgames.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class KnownInternalServerError  extends RuntimeException{
    public KnownInternalServerError(String message, Object... args) {
        super(String.format(message, args));
    }

    public KnownInternalServerError(String message) {
        super(message);
    }
}
