package nl.devgames.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * TODO: Write class level documentation
 *
 * @author Marcel
 * @since 26-5-2016.
 */
@ResponseStatus(code = HttpStatus.CONFLICT)
public class EntityAlreadyExistsException extends RuntimeException {

    public EntityAlreadyExistsException(String message, Object... args) {
        super(
                String.format(message, args )
        );
    }
}
