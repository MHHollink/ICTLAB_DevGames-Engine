package nl.devgames.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ReportParseExeption extends RuntimeException {
    public ReportParseExeption(String s) {
        super(s);
    }
}
