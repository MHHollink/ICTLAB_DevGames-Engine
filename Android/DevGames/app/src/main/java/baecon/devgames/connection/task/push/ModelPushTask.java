package baecon.devgames.connection.task.push;


import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import baecon.devgames.connection.client.DevGamesClient;
import baecon.devgames.connection.task.RESTTask;
import baecon.devgames.database.modelupdate.IModelUpdate;
import baecon.devgames.events.BusProvider;
import baecon.devgames.events.PushDoneEvent;
import baecon.devgames.events.PushTaskDoneEvent;
import baecon.devgames.model.ISynchronizable;
import baecon.devgames.model.update.AbsModelUpdate;
import baecon.devgames.util.L;
import baecon.devgames.util.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MimeUtil;

/**
 * <p>A background task that synchronizes changes to a certain model to the back-end. This is typically fired up from a
 * {@link baecon.devgames.connection.synchronization.AbsModelManager}.</p> <p>The task loops through a queue , synchronizing one update at a time. Every time
 * an update is done, a {@link PushDoneEvent} is fired.</p> <p>The {@link #doInBackground(Void...)} goes through this
 * queue by using {@linkplain java.util.LinkedList#poll()}. That means, when the task is running, and you add items to
 * the queue (typically via the {@linkplain baecon.devgames.connection.synchronization.AbsModelManager}), the loop will pick up those as
 * well.</p>
 */
public abstract class ModelPushTask<ModelClass extends ISynchronizable, ModelUpdate extends IModelUpdate> extends
        RESTTask<Void, PushDoneEvent, PushTaskDoneEvent> {

    /**
     * The id of the model where all the updates are related to
     */
    protected Long id;

    /**
     * The queue of ids of {@linkplain IModelUpdate}s. The {@link #doInBackground(Void...)} goes through this queue by
     * using {@linkplain java.util.LinkedList#poll()}. That means, when the task is running, and you add items to the
     * queue (typically via the {@linkplain baecon.devgames.connection.synchronization.AbsModelManager}), the loop will pick up those as well.
     */
    protected LinkedList<ModelUpdate> modelUpdateQueue;

    /**
     * The current {@link IModelUpdate} that is being synchronized.
     */
    protected ModelUpdate currentModelUpdate;

    /**
     * Create a new ModelUpdateTask that will synchronize a queue of updates of a model to the back-end.
     *  @param context
     *         The context
     * @param id
     */
    public ModelPushTask(Context context, Long id) {
        super(context);
        this.id = id;
        this.modelUpdateQueue = new LinkedList<>();
    }

    @Override
    protected PushTaskDoneEvent doInBackground(Void... params) {

        // Retrieve the DAO
        Dao<ModelUpdate, Long> modelUpdateDao = getModelUpdateDao();

        // Retrieve the update queue for this model
        try {
            List<ModelUpdate> list = modelUpdateDao.queryBuilder()
                    .orderBy(AbsModelUpdate.Column.LOCAL_ID, true)
                    .where()
                    .eq(AbsModelUpdate.Column.LOCAL_ID, id)
                    .query();

            // If there are no updates in the database, end this task.
            // Else put all updates in the queue to be processed
            if (list == null) {

                L.i("No updates found for model with local model id {0}", id);
                return getModelPushEventFactory().pushTaskDoneEvent(id, true);
            }
            else {
                // Add all updates to a queue
                modelUpdateQueue.addAll(list);

                L.v("Following ModelUpdates were retrieved from the database: (count={0})", modelUpdateQueue.size());
                long index = 0;
                for (ModelUpdate modelUpdate : modelUpdateQueue) {
                    L.v("{0}: {1}", ++index, modelUpdate);
                }
            }
        }
        catch (SQLException e) {
            L.e(e, "Something went wrong with retrieving ModelUpdates, uuid={0}", currentModelUpdate);
            return getModelPushEventFactory().pushTaskDoneEvent(id, false, LOCAL_DB_ERROR);
        }

        // Set up the interface between the app and the back-end
        DevGamesClient client = super.createService();

        L.v("Model={0}, amount of updates in queue: {1} Retrieved DAO and created AskClient", getClass().getSimpleName(),
                modelUpdateQueue.size());

        // Loop through the queue of updates that are ready to be synchronized
        while ((currentModelUpdate = modelUpdateQueue.poll()) != null) {

            L.v("Synchronizing model update id {0}", currentModelUpdate);

            if (currentModelUpdate.hasBlockingError()) {
                L.w("Warning! Prevented to sync {0} update(s) where the next update has a blocking error!");
                L.w("Update that has blocking error: {0}", currentModelUpdate);
                L.w("Further synchronizing of updates for this model cancelled!");
                return null;
            }

            Response response;

            try {
                // Do the sync and try again ONE TIME if an authentication error is thrown.
                // The one time retry for refreshing the session is not accounted in the ReportUpdate#number_of_retries
                L.v("Model={0}, starting sync", getClass().getSimpleName());
                response = currentModelUpdate.sync(getApplication(), getApplication().getDbHelper(), client);
                L.v("Model={0}, sync succeeded", getClass().getSimpleName());
            }
            catch (RetrofitError error) {

                response = error.getResponse();

                // Based on the response code, determine whether the error is recoverable or not
                int statusCode = getStatus(error);

                L.e(error, "Received an error while synchronizing: status code: {0}, reason: {1}",
                        statusCode,
                        error.getResponse() != null ? error.getResponse().getReason() : null);

                // If the header is in the 4xx or 5xx range, update the currentModelUpdate, so the UI can show it to the user
                if (statusCode >= 400 && statusCode < 500) {
                    // The 4xx range indicates the client did something wrong, so this becomes a unrecoverable error

                    if (statusCode == 403) {

                        // Authentication failed. request re-login for a new session the session.
                        requestReLogin();
                    }

                    // Convert the body to a String as it will contain valuable error message
                    String charset = "UTF-8";
                    StringBuilder body = new StringBuilder();
                    if (response.getBody().mimeType() != null) {
                        charset = MimeUtil.parseCharset(response.getBody().mimeType());
                    }
                    InputStreamReader isr;
                    try {
                        isr = new InputStreamReader(response.getBody().in(), charset);
                        char[] buf = new char[4096];
                        int len;
                        while ((len = isr.read(buf, 0, buf.length)) != -1) {
                            body.append(buf, 0, len);
                        }
                    } catch (Exception e) {
                        L.e(e, "Could not decode body from error response");
                    }

                    currentModelUpdate.setHasBlockingError(statusCode, body.toString());


                    // We encountered a unrecoverable error, further updates should not be synchronized
                    // We'll break out of the loop when further on a PushTaskDoneEvent is posted.
                }
                else {

                    // The 5xx range indicates the back-end did something wrong, this is (mostly) a recoverable error

                    // Increase the amount of retries that is done
                    currentModelUpdate.failed();

                    // We encountered a recoverable error, further updates should not be synchronized until this one is succeeded.
                    // We'll break out of the loop when further on a PushTaskDoneEvent is posted.
                }

                L.v("Model={0}, update failed! Update={1}", getClass().getSimpleName(), currentModelUpdate);

                // Update the amount of retries in the local database
                try {
                    modelUpdateDao.update(currentModelUpdate);
                    L.v("Model={0}, amount of retries updated to {1}", getClass().getSimpleName(),
                            currentModelUpdate.getNumberOfRetries());
                }
                catch (SQLException e) {
                    L.e(e, "Tried to update the amount of retries, but that failed.");
                }

                // The update failed and will be retried the next sync cycle
                return getModelPushEventFactory().pushTaskDoneEvent(id, false, getStatus(error),
                        currentModelUpdate, response);

            }
            catch (Exception e) {
                L.e(e, "Something went wrong while synchronizing a update");
                return getModelPushEventFactory().pushTaskDoneEvent(id, false);
            }

            // Call to the back-end succeeded, delete the Update record from the database
            try {
                modelUpdateDao.delete(currentModelUpdate);
            }
            catch (SQLException e) {
                L.e(e, "Could not delete update from database! But synchronization to back-end was successful");
                return getModelPushEventFactory()
                        .pushTaskDoneEvent(currentModelUpdate.getId(), false, LOCAL_DB_ERROR);
            }

            L.v("Model={0}, removed update record", getClass().getSimpleName());

            // And post an event that this Update is done
            publishProgress(getModelPushEventFactory().pushDoneEvent(currentModelUpdate));

            L.v("Model={0}, publish progress called", getClass().getSimpleName());
        }

        try {
            List<ModelUpdate> list = modelUpdateDao.queryBuilder()
                    .orderBy(AbsModelUpdate.Column.LOCAL_ID, true)
                    .where()
                    .eq(AbsModelUpdate.Column.LOCAL_ID, id)
                    .query();

            L.v("ModelUpdates left after ModelPushTask: (count={0})", modelUpdateQueue.size());
            long index = 0;
            for (ModelUpdate modelUpdate : modelUpdateQueue) {
                L.v("{0}: ", ++index, modelUpdate);
            }
        }
        catch (SQLException e) {
            L.e(e, "Could not retrieve list of left over model updates");
        }

        updateModelState();

        // Pass event that will be fired in onPostExecute
        return getModelPushEventFactory().pushTaskDoneEvent(id, true);
    }

    @Override
    protected void onProgressUpdate(PushDoneEvent... values) {
        super.onProgressUpdate(values);

        L.v("Model={0}, publishProgress", getClass().getSimpleName());

        // Fires an event that an update is done
        if (values != null && values.length == 1 && values[0] != null) {
            L.v("Model={0}, event={1}", getClass().getSimpleName(), values[0]);
            BusProvider.getBus().post(values[0]);
        }
        else {
            L.w("Tried to post progress, but passed param was null");
        }
    }

    @Override
    protected void onPostExecute(PushTaskDoneEvent doneEvent) {
        super.onPostExecute(doneEvent);

        L.v("Model={0}, onPostExecute", getClass().getSimpleName());

        // Fires an event that an update is done
        if (doneEvent != null) {
            L.v("Model={0}, event={1}", getClass().getSimpleName(), doneEvent);

            if (doneEvent.lastUnsuccessfulUpdateResponse != null) {
                L.v("retrofit.client.response: statuscode={0}, reason={1}, body={2}, headers={3}",
                        doneEvent.lastUnsuccessfulUpdateResponse.getStatus(),
                        doneEvent.lastUnsuccessfulUpdateResponse.getReason(),
                        doneEvent.lastUnsuccessfulUpdateResponse.getBody(),
                        Utils.collectionToString(doneEvent.lastUnsuccessfulUpdateResponse.getHeaders()));
            }
            BusProvider.getBus().post(doneEvent);
        }
        else {
            L.w("Tried to post doneEvent, but passed param was null");
        }
    }

    /**
     * Updates the {@link baecon.devgames.model.AbsSynchronizable#state} to {@link ISynchronizable.State#UP_TO_DATE} when no model updates
     * are left.
     */
    protected void updateModelState() {

        try {
            long leftOverUpdates = getModelUpdateDao().queryBuilder().where().eq(AbsModelUpdate.Column.LOCAL_ID, id).countOf();

            L.v("Updates left over: {0}", leftOverUpdates);

            // If we do not have updates left over, update the state of the model to UP_TO_DATE
            if (getModelUpdateDao().queryBuilder().where().eq(AbsModelUpdate.Column.LOCAL_ID, id).countOf() == 0L) {

                ModelClass model = getModelDao().queryForId(id);

                if (model != null) {
                    model.setState(ISynchronizable.State.UP_TO_DATE);
                    getModelDao().update(model);
                }

                // Query again to refresh the cache
                getModelDao().refresh(model);
            }
        }
        catch (SQLException e) {
            L.e(e, "Could not count left over updates. ISynchronizable.state not changed");
        }
    }

    protected abstract Dao<ModelClass, Long> getModelDao();

    /**
     * Override to return the Dao for this sub class of {@link IModelUpdate}. This could not be generified, because a Class
     * instance is needed to get the Dao. The passed type {@link ModelUpdate} is not allowed to be instantiated.
     *
     * @return The Dao for this sub class of {@link IModelUpdate}
     */
    protected abstract Dao<ModelUpdate, Long> getModelUpdateDao();

    /**
     * Override to return the event factory for this {@link ModelPushTask}. For every {@link IModelUpdate} that is
     * finished (doesn't matter whether it was successful), a {@link PushDoneEvent} should be fired. For every {@link
     * ModelPushTask} that is finished (doesn't matter whether it was successful), a {@link PushDoneEvent} should be
     * fired.
     *
     * @return The event factory for this {@link ModelPushTask}
     */
    protected abstract ModelPushEventFactory getModelPushEventFactory();

    /**
     * A factory for events that should be fired after one {@link IModelUpdate} is finished and after a {@link
     * ModelPushTask} is finished.
     */
    public static abstract class ModelPushEventFactory {

        /**
         * Returns a fresh instance of a {@link PushTaskDoneEvent}.
         *
         * @return Returns a fresh instance of a {@link PushTaskDoneEvent}.
         */
        abstract PushTaskDoneEvent pushTaskDoneEvent(Long id, boolean success);

        /**
         * Returns a fresh instance of a {@link PushTaskDoneEvent}.
         *
         * @return Returns a fresh instance of a {@link PushTaskDoneEvent}.
         */
        abstract PushTaskDoneEvent pushTaskDoneEvent(Long id, boolean success, int statusCode);

        /**
         * Returns a fresh instance of a {@link PushTaskDoneEvent}.
         *
         * @return A fresh instance of an {@link PushTaskDoneEvent}.
         */
        abstract PushTaskDoneEvent pushTaskDoneEvent(Long id, boolean success, int statusCode,
                                                     IModelUpdate lastUnsuccessfulUpdate,
                                                     Response lastUnsuccessfulUpdateResponse);

        /**
         * Returns a fresh instance of a {@link PushDoneEvent}.
         *
         * @return A fresh instance of a {@link PushDoneEvent}.
         */
        abstract PushDoneEvent pushDoneEvent(long updateId, String uuid);

        /**
         * Returns a fresh instance of a {@link PushDoneEvent}.
         *
         * @return A fresh instance of a {@link PushDoneEvent}.
         */
        abstract PushDoneEvent pushDoneEvent(IModelUpdate modelUpdate);
    }
}