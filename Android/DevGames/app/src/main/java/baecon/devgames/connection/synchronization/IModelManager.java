package baecon.devgames.connection.synchronization;

import android.app.Application;

import java.io.Serializable;

import baecon.devgames.model.ISynchronizable;
import baecon.devgames.database.modelupdate.IModelUpdate;

/**
 * A class that manages the {@link Model}. It is the main entrance point for persisting and synchronizing the Model.
 * <p/>
 * <b>Synchronizing</b><br />
 * The synchronization system polls and pushes data from/to the back-end. Polling is interval driven, where pushing is
 * event driven.<br /><br />
 *
 * <i>Polling</i><br />
 * Polling can be executed in a 'one-shot' poll and a repeating poll cycle.<br />
 * To execute a 'one-shot' poll, use {@link #startPoll()}. <br />
 * To execute a repeating poll cycle, use {@link #schedulePolling()}. The interval of the cycle is based on
 * {@link #isInForegroundSyncMode()}. If true, it uses the {@link #getForegroundPollingInterval()}, otherwise
 * {@link #getBackgroundPollingInterval()}. The latter only applies if {@link #isAllowedToSyncInBackground()} is true.
 *
 * <br /><br />
 * <i>Pushing</i><br />
 * Pushing happens on request. A push is sending the changed value(s) from a model to the back-end. The changes values
 * are captured in an {@link IModelUpdate}. Once a create, update or (flag for) delete operation has succeeded, an
 * IModelUpdate has to be generated and offered to this manager through {@link #offerUpdate(IModelUpdate)} or
 * {@link #offerUpdate(Long)}.
 */
public interface IModelManager<Model extends ISynchronizable> {

    /**
     * Offer an update to a model from the local database. These updates have to be synchronized asynchronously, so they
     * are queued and synchronized sequentially.
     *
     * @param id
     *         The local model id
     */
    void offerUpdate(Long id);

    /**
     * Offer an update to a model from the local database. These updates have to be synchronized asynchronously, so they
     * are queued and synchronized sequentially.
     *
     * @param modelUpdate
     *         The update to a model from the local database.
     */
    void offerUpdate(IModelUpdate modelUpdate);

    /**
     * Tells the manager to poll the back-end whether there is new data.
     */
    void startPoll();

    /**
     * Schedules a poll cycle. The interval used, should be based on {@link #isInForegroundSyncMode()} and
     * {@link #isAllowedToSyncInBackground()}.
     * <p/>
     * If you're looking for a 'one-shot' poll, use {@link #startPoll()}.
     */
    void schedulePolling();

    /**
     * Stops the scheduled polling cycle, if running.
     */
    void stopSchedulePolling();

    /**
     * Returns the {@link android.app.Application} to provide a {@link android.content.Context} to execute the {@link baecon.devgames.connection.task.push.ModelPushTask}s.
     *
     * @return The {@link android.app.Application} to provide a {@link android.content.Context} to execute the {@link baecon.devgames.connection.task.push.ModelPushTask}s.
     */
    Application getApplication();

    /**
     * Should be called before using the first time.
     */
    void init();

    /**
     * @return Whether this manager in initialized.
     */
    boolean isInited();

    /**
     * Tell the SyncManager to release its resources and kill all the tasks it is doing.
     */
    void shutdown();

    /**
     * @return Whether a scheduled polling cycle is allowed.
     */
    boolean scheduledPollingEnabled();

    /**
     * Starts synchronizing with the {@link #getForegroundPollingInterval()}.
     */
    void startForegroundSyncMode();

    /**
     * Stops using the {@link #getForegroundPollingInterval()}. If {@link #isAllowedToSyncInBackground()} is true,
     * synchronizing continues with the {@link #getBackgroundPollingInterval()}. Otherwise synchronization stops.
     */
    void stopForegroundSyncMode();

    /**
     * @return Whether the manager is using the {@link #getForegroundPollingInterval()}
     */
    boolean isInForegroundSyncMode();

    /**
     * @return The interval for starting a poll cycle, when the app is in the background. In milliseconds.
     */
    long getBackgroundPollingInterval();

    /**
     * @return The interval for starting a poll cycle, when the app is in the foreground. In milliseconds.
     */
    long getForegroundPollingInterval();

    /**
     * @return Whether this manager should allow synchronization tasks, when the app is in the background.
     */
    boolean isAllowedToSyncInBackground();

    /**
     * Creates this model in the local database. If that succeeds, a {@link IModelUpdate} is should be offered to this
     * class. Next, this class is responsible for pushing the new model to the back-end.
     *
     * @param model The instance of a model that has to be created
     */
    void create(Model model);

    /**
     * Updates the whole model in the local database. If that succeeds, a {@link IModelUpdate} should be offered to this
     * class, so it can push the changes to the back-end.
     *
     * @param model The instance of a model that has to be updated. Keep in mind that this completely overrides the
     *              model
     *
     * @see #update(Long, String, java.io.Serializable)
     */
    void update(Model model);

    /**
     * Updates one field of the model with {@code localModelId} in the local database. If that succeeds, a
     * {@link IModelUpdate} should be offered to this class, so it can push the changes to the back-end.
     *
     * @param id
     * @param field The name of the column that is used in the local database. See {@link ISynchronizable.Column}
     * @param value The new value for the field
     */
    void update(Long id, String field, Serializable value);

    /**
     * Flags a model for deletion by setting the state to {@link ISynchronizable.State#FLAGGED_FOR_DELETE}. Only the
     * back-end executes the real deletion. The poll procedure for this Model should detect whether the model is removed
     * from the data set. Only if that is true, the model should be removed from the local database.
     *
     * @param model The model to flag for delete
     *
     * @see //#delete(long)
     */
    void delete(Model model);
}