package nl.devgames.rest.errors;

public class DatabaseOfflineException extends KnownInternalServerError {
    public DatabaseOfflineException(String message, Object... args) {
        super(message, args);
    }
}
