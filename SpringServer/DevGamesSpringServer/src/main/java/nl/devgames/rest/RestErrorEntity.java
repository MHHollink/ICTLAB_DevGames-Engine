package nl.devgames.rest;

public class RestErrorEntity {

    final boolean error = true;
    final int errorCode;
    final String errorMessage;

    public RestErrorEntity() {
        errorCode = -1;
        errorMessage = "";
    }

    public RestErrorEntity(int errorCode, String message) {
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public RestErrorEntity(int errorCode, String message, String... messageFormatArgs) {
        this.errorCode = errorCode;
        this.errorMessage = getFormattedString(message, messageFormatArgs);
    }

    public boolean isError() {
        return error;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private static String getFormattedString(String message, String... messageFormatArgs) {
        if (message != null && messageFormatArgs != null && messageFormatArgs.length != 0) {
            try {
                return String.format(message, messageFormatArgs);
            }
            catch (Exception e) {
                e.printStackTrace();
                return message;
            }
        }
        else {
            return "";
        }
    }
}