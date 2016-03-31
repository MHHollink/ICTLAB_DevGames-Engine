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

    public AbsRestService(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    protected String get() {
        return null;
    }

    protected String post(String jsonString, Tuple... properties) throws IOException {
        L.og("starting [POST] to %s", url);
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

        return response.toString();
    }

    protected String put() {
        return null;
    }

    protected String delete() {
        return null;
    }
}
