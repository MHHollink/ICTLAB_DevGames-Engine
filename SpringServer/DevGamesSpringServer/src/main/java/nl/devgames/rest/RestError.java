package nl.devgames.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.logging.Logger;

/**
 * A (not complete) list of errors that can happen in the REST API. Pass an item to
 * {@link RestResponse#error(RestError)}.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RestError {

    /** Message: {@code Something went wrong building the RestResponse, probably null was passed as RestError. Please check back-end logs for 'Error inception'.} */
    ERROR_INCEPTION(-1, 500, "Something went wrong building the RestResponse, probably null was passed as RestError. Please check back-end logs for 'Error inception'."),

    /** Message: {@code Unknown error occurred. Please check the logs for more information.} */
    UNKNOWN_INTERNAL_SERVER_ERROR(0, 500, "Unknown error occurred. Please check the logs for more information."),

    /** Message: {@code Error occurred: %s} */
    KNOWN_INTERNAL_SERVER_ERROR(1, 500, "Error occurred: %s"),

    /** Message: {@code The session is null or invalid} */
    INVALID_SESSION(0, 403, "The session is null or invalid");


    private static String formatForDoc(RestError e){
        return String.format("errorCode: %s, httpStatusCode: %s, Message: {@code %s", e.errorCode, e.httpStatusCode, e.message);
    }

    private static final Logger log = Logger.getLogger(RestError.class.toString());

    static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    final int errorCode;
    final int httpStatusCode;
    final String message;
    final String contentType;

    RestError(int errorCode, int httpStatusCode, String message) {
        this(errorCode, httpStatusCode, message, "application/json");
    }

    RestError(int errorCode, int httpStatusCode, String message, String contentType) {
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
        this.message = message;
        this.contentType = contentType;
    }

    public String getMessage() {
        try {
            RestErrorEntity entity = new RestErrorEntity(errorCode, message);
            String retVal = mapper.writeValueAsString(entity);
            log.info("RestError.getMessage(): " + retVal);
            return retVal;
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error building RestError message. Please check back-end logs for more information.";
        }
    }

    public String getMessage(String... messageFormatArgs) {
        try {
            RestErrorEntity entity = new RestErrorEntity(errorCode, message, messageFormatArgs);
            String retVal = mapper.writeValueAsString(entity);
            log.info("RestError.getMessage(String...): " + retVal);
            return retVal;
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error building RestError message. Please check back-end logs for more information.";
        }
    }

    public int getErrorCode() {
        return errorCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getContentType() {
        return contentType;
    }
}