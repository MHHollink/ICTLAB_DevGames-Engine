package nl.devgames.connection.gcm;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.rest.errors.NotFoundException;
import nl.devgames.utils.L;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

public class GCMMessageComposer {
	/**
     * TODO fix javadoc
	 * method to compose a GCM message
	 * messageType = type of message to send (see GCMMessageType)
	 * messageTitle = string to send as message title
	 * messageText = string to send as message text
	 * 
	 * returns a GCMMessage
	 */
	private static GCMMessage createGCMMessage(GCMMessageType messageType, String messageTitle, String messageText){

    	GCMMessage message = new GCMMessage();

    	message.createNotification(
    			messageType,
    			messageTitle,
    			messageText
        );
        
        return message;
    }
	/*
	 * TODO fix javadoc
	 * Method to send a message via gcm
	 * messageType = type of message to send (see GCMMessageType)
	 * messageTitle = string to send as message title
	 * messageText = string to send as message text
	 */
	public static void sendMessage(GCMMessageType messageType, String messageTitle, String messageText) {
    	L.i( "Sending POST to GCM" );

    	//create message
    	GCMMessage message = createGCMMessage(messageType, messageTitle, messageText);

        //get list of tokens from neo4j db
        //TODO: for now this gets 2 users, make it work for a list of users or userids

//        String tokenResponseAsString = null;
//        try {
//            tokenResponseAsString = Neo4JRestService.getInstance().postQuery("Match (n:User) Where ID(n) = 37 Return n.gcmRegId");
//        } catch (ConnectException e) {
//            L.e(e, "Neo4J Post threw exception, Database might be offline!");
//            throw new KnownInternalServerError(e.getMessage());
//        }

        String tokenResponseAsString = null;
        try {
            tokenResponseAsString = Neo4JRestService.getInstance().postQuery("Match (n:User) Return n.gcmRegId");
        } catch (ConnectException e) {
            L.e(e, "Neo4J Post threw exception, Database might be offline!");
            throw new KnownInternalServerError(e.getMessage());
        }
        JsonObject tokenResponseJson = new JsonParser().parse(tokenResponseAsString).getAsJsonObject(); //parse neo4j response
        JsonArray errorsArray = tokenResponseJson.get("errors").getAsJsonArray(); //get list of errors

        //Trhrows exception if there are errors
        if (errorsArray.size() != 0) { 
            for (JsonElement error : errorsArray) L.w(error.getAsString());
            throw new KnownInternalServerError("InternalServerError: " + errorsArray.getAsString());
        }
        //get data
        JsonArray data = tokenResponseJson.get("results").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray();

        if (data.size() == 0) {
            throw new NotFoundException("The server could not find the requested data...");
        }
        //get actual tokens
        List<String> tokenList = new ArrayList<>();
        for(JsonElement element : data) {
            String token = element.getAsJsonObject().get("row").getAsJsonArray().get(0).getAsString();
            if(token!=null) {
                tokenList.add(token);
            }
        }

        //add tokens to message
        message.addToken(tokenList);
        
        GCMRestService.getInstance().postMessage(
              message
      );
    }
}