package nl.devgames.connection.gcm;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.rest.errors.KnownInternalServerError;
import nl.devgames.rest.errors.NotFoundException;
import nl.devgames.utils.L;

public class GCMMessageComposer {
	/*
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
	 * Method to send a message via gcm
	 * messageType = type of message to send (see GCMMessageType)
	 * messageTitle = string to send as message title
	 * messageText = string to send as message text
	 */
	public static void sendMessage(GCMMessageType messageType, String messageTitle, String messageText) {
    	System.out.println( "Sending POST to GCM" );

    	//create message
    	GCMMessage message = createGCMMessage(messageType, messageTitle, messageText);
    	
//    	//api key devGames
//    	String apiKey = "AIzaSyC9G357o3fykQPsbANNtn6KXOkHJi6_kwA";
    	
//    	//jorik project test
//        String apiKey = "AIzaSyAYYJpgR6yk4AePQioLiMKVNsOVyMrcKVU";
    	
//      //marcels tel test
//      tokenList.add("APA91bH9_5pjDv1TIDorZoRcm8Ye_bTGJK6eFTKQuVJI1lGf12EdSXfwmv1wtc9hXFj82eHb8O5b_ta7zxDlfbtCGiRdMxugCZ3N1e_WfPWSbZYBlaT15VpZuGxgKW_t77FrEM8jcIln");
      
//      //joriks tel test
//      tokenList.add("ctUfXcOYkEw:APA91bHchgJZU-hMRxXuwxFbKQsIOKAl82HORl53EQZGzjPbdlccufAYPBwiO3d9-YkikxW41VitbwisaqJHjXmzTe5IIZD08PXYHVlIOzHtXOHyLu7E6x3qtHg6g7BHM_TDG7IrIYjy");
        
        //get list of tokens from neo4j db
        //TODO: for now this gets 2 users, make it work for a list of users or userids
//        tokenList;
        String tokenResponseAsString = Neo4JRestService.getInstance().postQuery("Match (n:User) Where ID(n) = 37 Return n.gcmRegId");
        JsonObject tokenResponseJson = new JsonParser().parse(tokenResponseAsString).getAsJsonObject(); //parse neo4j response
        JsonArray errorsArray = tokenResponseJson.get("errors").getAsJsonArray(); //get list of errors

        //Trhrows exception if there are errors
        if (errorsArray.size() != 0) { 
            for (JsonElement error : errorsArray) L.og(error.getAsString());
            throw new KnownInternalServerError("InternalServerError: " + errorsArray.getAsString());
        }
        //get data
        JsonArray data = tokenResponseJson.get("results").getAsJsonArray().get(0).getAsJsonObject().get("data").getAsJsonArray();

        if (data.size() == 0) {
            throw new NotFoundException("The server could not find the requested data...");
        }
        //get actual tokens
        JsonArray tokensAsJsonArray = data.get(0).getAsJsonObject().get("row").getAsJsonArray();

        //add tokens
        for(JsonElement token : tokensAsJsonArray) {
        	message.addToken(token.toString());
        }
        
        GCMRestService.getInstance().postMessage(
              message
      );
    }
}