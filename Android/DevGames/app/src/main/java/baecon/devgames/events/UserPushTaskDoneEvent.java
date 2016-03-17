package baecon.devgames.events;

import baecon.devgames.database.modelupdate.IModelUpdate;
import retrofit.client.Response;

public class UserPushTaskDoneEvent extends PushTaskDoneEvent{

    public UserPushTaskDoneEvent(String localModelId, boolean success) {
        super(localModelId, success);
    }

    public UserPushTaskDoneEvent(String localModelId, boolean success, int statusCode) {
        super(localModelId, success, statusCode);
    }

    public UserPushTaskDoneEvent(String localModelId, boolean success, int statusCode, IModelUpdate lastUnsuccessfulUpdate, Response lastUnsuccessfulUpdateResponse) {
        super(localModelId, success, statusCode, lastUnsuccessfulUpdate, lastUnsuccessfulUpdateResponse);
    }
}
