package nl.devgames.rest;

import nl.devgames.rest.RestResponse;

public class RestErrorResponse<E> extends RestResponse<E> {

    private final RestError restError;
    private final String[] messageFormatArgs;

    public RestErrorResponse(RestError error) {
        super();

        this.restError = error;
        this.messageFormatArgs = null;

        this.status = error.getHttpStatusCode();
        this.statusInfo = error.getMessage();
        this.contentType = error.getContentType();
    }

    public RestErrorResponse(RestError error, String... messageFormatArgs) {
        super();

        this.restError = error;
        this.messageFormatArgs = messageFormatArgs;

        this.status = error.getHttpStatusCode();
        this.statusInfo = error.getMessage(messageFormatArgs);
        this.contentType = error.getContentType();
    }

    public RestError getRestError() {
        return restError;
    }

    public String[] getMessageFormatArgs() {
        return messageFormatArgs;
    }
}