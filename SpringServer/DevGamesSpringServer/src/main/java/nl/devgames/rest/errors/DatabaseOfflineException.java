package nl.devgames.rest.errors;

public class DatabaseOfflineException extends KnownInternalServerError {
    public DatabaseOfflineException() {
        super("Database service might be offline!");
    }

    @Deprecated
    public DatabaseOfflineException(String message, Object... args) {
        super(message, args);
    }
}
