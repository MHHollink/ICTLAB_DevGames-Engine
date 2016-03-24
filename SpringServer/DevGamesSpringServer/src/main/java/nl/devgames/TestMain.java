package nl.devgames;

import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.gcm.GCMMessage;
import nl.devgames.connection.gcm.GCMRestService;

public class TestMain {

    public static void main(String[] args) {

        GCMMessage message = new GCMMessage();

        // TODO create message.

        GCMRestService.getInstance().postMessage(
            message
        );
    }
}
