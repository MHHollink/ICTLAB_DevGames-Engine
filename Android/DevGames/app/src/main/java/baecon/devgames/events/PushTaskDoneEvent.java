package baecon.devgames.events;

import baecon.devgames.database.modelupdate.IModelUpdate;
import retrofit.client.Response;

/**
 * An event indicating that the synchronization for a {@linkplain baecon.devgames.connection.synchronization.AbsModelManager} is done with
 * synchronizing all its {@linkplain IModelUpdate}s.
 */
public abstract class PushTaskDoneEvent {

    /**
     * The local database id of the model where this update is related to.
     */
    public final String localModelId;

    /**
     * Whether all {@linkplain IModelUpdate}s have been executed successfully
     */
    public final boolean success;

    /**
     * The status code. If {@link #success} == {@code true}, then it is {@link baecon.devgames.connection.task.RESTTask#OK},
     * otherwise another code from {@link baecon.devgames.connection.task.RESTTask}.
     */
    public final int statusCode;

    /**
     * The last update that went not successful. Only populated when {@link #success} == {@code false}.
     */
    public final IModelUpdate lastUnsuccessfulUpdate;

    /**
     * The {@linkplain retrofit.client.Response} from the {@link #lastUnsuccessfulUpdate}. Only populated when {@link
     * #success} == {@code false}.
     */
    public final Response lastUnsuccessfulUpdateResponse;

    public PushTaskDoneEvent(String localModelId, boolean success) {
        this(localModelId, success, 0, null, null);
    }

    public PushTaskDoneEvent(String localModelId, boolean success, int statusCode) {
        this(localModelId, success, statusCode, null, null);
    }

    public PushTaskDoneEvent(String localModelId, boolean success, int statusCode, IModelUpdate lastUnsuccessfulUpdate, Response lastUnsuccessfulUpdateResponse) {
        this.localModelId = localModelId;
        this.success = success;
        this.statusCode = statusCode;
        this.lastUnsuccessfulUpdate = lastUnsuccessfulUpdate;
        this.lastUnsuccessfulUpdateResponse = lastUnsuccessfulUpdateResponse;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{" +
                "localModelId=" + localModelId +
                ", success=" + success +
                ", statusCode=" + statusCode +
                ", lastUnsuccessfulUpdate=" + lastUnsuccessfulUpdate +
                ", lastUnsuccessfulUpdateResponse=" + lastUnsuccessfulUpdateResponse +
                '}';
    }
}