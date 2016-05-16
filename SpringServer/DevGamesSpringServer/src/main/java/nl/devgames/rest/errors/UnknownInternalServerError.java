package nl.devgames.rest.errors;

/**
 * Created by Marcel on 16-5-2016.
 */
public class UnknownInternalServerError extends RuntimeException {
    public UnknownInternalServerError(String message, Object... args) {
        super(String.format(message, args));
    }
}
