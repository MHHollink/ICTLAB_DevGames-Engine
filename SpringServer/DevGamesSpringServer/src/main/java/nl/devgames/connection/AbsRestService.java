package nl.devgames.connection;

import nl.devgames.utils.L;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class AbsRestService {

    public AbsRestService(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    URL url;

    protected void get() {

    }

    protected String post(String jsonString, Tuple... properties) throws IOException {
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

        //get response
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        L.og("Sending 'POST' request to URL : %s", url);
        L.og("Response Message: %s", responseMessage);
        L.og("Response Code: %d", responseCode);

        BufferedReader in;
            in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    protected void put() {

    }

    protected void delete() {

    }
}
