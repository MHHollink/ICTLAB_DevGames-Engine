package nl.devgames.connection.gcm;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.rest.errors.BadRequestException;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.rest.errors.NotFoundException;
import nl.devgames.utils.L;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
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
	public static void sendMessage(GCMMessageType messageType, String messageTitle, String messageText, Long... userIds) {
    	L.d( "Sending POST to GCM" );

    	//create message
    	GCMMessage message = createGCMMessage(messageType, messageTitle, messageText);

        String tokenResponseAsString;
        try {
            String query;
            if(userIds.length == 0) {
                throw new BadRequestException("Dev failed to add ID to post method, slap him with a fish to wake him up!");
            } if (userIds.length == 1) {
                query = "Match (n:User) WHERE ID(n) = "+ userIds[0] +" RETURN n.gcmId";
            } else
                query = "Match (n:User) WHERE ID(n) IN "+ Arrays.toString(userIds) +" RETURN n.gcmRegId";

            tokenResponseAsString = Neo4JRestService.getInstance().postQuery(query);
        } catch (ConnectException e) {
            L.e(e, "Neo4J Post threw exception, Database might be offline!");
            throw new KnownInternalServerError(e.getMessage());
        }

        JsonObject tokenResponseJson = new JsonParser().parse(tokenResponseAsString).getAsJsonObject(); //parse neo4j response
        JsonArray errorsArray = tokenResponseJson.get("errors").getAsJsonArray(); //get list of errors

        //Throws exception if there are errors
        if (errorsArray.size() != 0) { 
            for (JsonElement error : errorsArray) L.w(error.getAsString());
            throw new KnownInternalServerError("InternalServerError: " + errorsArray.getAsString());
        }
        //get data
        JsonArray data = tokenResponseJson.get("results").getAsJsonArray()
                .get(0).getAsJsonObject().get("data").getAsJsonArray();

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