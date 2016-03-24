package nl.devgames;

import nl.devgames.connection.database.Neo4JRestService;
import nl.devgames.connection.gcm.GCMMessage;
import nl.devgames.connection.gcm.GCMMessageType;
import nl.devgames.connection.gcm.GCMRestService;
import nl.devgames.utils.L;

public class TestMain {

    public static void main(String[] args) {
        GCMMessage message = new GCMMessage();

        // TODO create message.
        message.addToken(
                "APA91bH9_5pjDv1TIDorZoRcm8Ye_bTGJK6eFTKQuVJI1lGf12EdSXfwmv1wtc9hXFj82eHb8O5b_ta7zxDlfbtCGiRdMxugCZ3N1e_WfPWSbZYBlaT15VpZuGxgKW_t77FrEM8jcIln",
                "ctUfXcOYkEw:APA91bHchgJZU-hMRxXuwxFbKQsIOKAl82HORl53EQZGzjPbdlccufAYPBwiO3d9-YkikxW41VitbwisaqJHjXmzTe5IIZD08PXYHVlIOzHtXOHyLu7E6x3qtHg6g7BHM_TDG7IrIYjy"
        );

        message.createNotification(
                GCMMessageType.PLAIN_NOTIFICATION,
                "Hello mutha fucka",
                "Swag");



        GCMRestService.getInstance().postMessage(
            message
        );
    }

}
