package nl.devgames.connection;

import nl.devgames.utils.L;
import nl.devgames.utils.Tuple;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class AbsRestService {

    private URL url;

    protected String response;

    public AbsRestService(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    protected String get() {
        L.w("tried to use not implemented method [GET]");
        return null;
    }

    protected String post(String jsonString, Tuple... properties) throws IOException {
        L.t("starting [POST] to %s", url);
        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        for (Tuple property : properties) {
            connection.setRequestProperty(String.valueOf(property.getK()), String.valueOf(property.getV()));
        }
        connection.setDoOutput(true);
        connection.connect();

        //get connection output stream
        DataOutputStream stream = new DataOutputStream(connection.getOutputStream());

        stream.writeBytes(jsonString);

        //send and close
        stream.flush();
        stream.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()
                )
        );

        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        this.response = response.toString();
        return this.response;
    }

    protected String put() {
        L.w("tried to use not implemented method [PUT]");
        return null;
    }

    protected String delete() {
        L.w("tried to use not implemented method [DELETE]");
        return null;
    }
}
