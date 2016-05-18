package nl.devgames.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Marcel on 16-5-2016.
 */
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class UnknownInternalServerError extends RuntimeException {
    public UnknownInternalServerError(String message, Object... args) {
        super(String.format(message, args));
    }
}
