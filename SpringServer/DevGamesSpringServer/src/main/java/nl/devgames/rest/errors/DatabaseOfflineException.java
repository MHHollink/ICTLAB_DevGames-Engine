package nl.devgames.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE)
public class DatabaseOfflineException extends RuntimeException {
    public DatabaseOfflineException() {
        super("Database service might be offline!");
    }
}
