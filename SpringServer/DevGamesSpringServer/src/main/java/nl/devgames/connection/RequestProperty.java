package nl.devgames.connection;

/**
 * Created by Marcel on 24-3-2016.
 */
public class RequestProperty {

    private String k;
    private String v;

    public RequestProperty(String k, String v) {
        this.k = k;
        this.v = v;
    }

    public String getK() {
        return k;
    }

    public String getV() {
        return v;
    }
}
