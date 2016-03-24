package nl.devgames.connection;

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

    protected void post(String jsonString, RequestProperty... properties) throws IOException {

        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        for (RequestProperty property : properties) {
            connection.setRequestProperty(property.getK(), property.getV());
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
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code was: " + responseCode);

        BufferedReader in;
            in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //result
        System.out.println(response.toString());
    }

    protected void put() {

    }

    protected void delete() {

    }
}
