package nl.devgames.rest.errors;

/**
 * Created by Marcel on 16-5-2016.
 */
public class DatabaseOfflineException extends KnownInternalServerError {
    public DatabaseOfflineException(String message, Object... args) {
        super(message, args);
    }
}
