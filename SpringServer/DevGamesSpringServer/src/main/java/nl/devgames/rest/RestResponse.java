package nl.devgames.rest;

public class RestResponse<E> {

    public E entity;
    public String contentType;
    public int status;
    public String statusInfo;

    public RestResponse () {
        this.entity = null;
        this.contentType = "application/json";
        this.status = 200;
        this.statusInfo = null;
    }

    public RestResponse (E res) {
        this.entity = res;
        this.contentType = "application/json";
        this.status = 200;
        this.statusInfo = null;
    }


    public RestResponse<E> status (int statusCode) {
        this.status = statusCode;
        return this;
    }

    public RestResponse<E> status (int statusCode, String info) {
        this.status = statusCode;
        this.statusInfo = info;
        return this;
    }

    //chained construction ( http://en.wikipedia.org/wiki/Method_chaining#Java )
    public static <F> RestResponse<F> entity(F res ) {
        return new RestResponse<F>( res );
    }

    public static <G> RestResponse<G> error(RestError error) {
        return new RestErrorResponse<>(error);
    }

    public static <G> RestResponse<G> error(RestError error, String... messageFormatArgs) {
        return new RestErrorResponse<>(error, messageFormatArgs);
    }
}