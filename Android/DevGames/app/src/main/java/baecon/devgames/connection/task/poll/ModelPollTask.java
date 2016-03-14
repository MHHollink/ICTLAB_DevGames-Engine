package baecon.devgames.connection.task.poll;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import baecon.devgames.connection.client.DevGamesClient;
import baecon.devgames.connection.client.dto.ModelDTO;
import baecon.devgames.connection.synchronization.AbsModelManager;
import baecon.devgames.connection.task.RESTTask;
import baecon.devgames.events.BusProvider;
import baecon.devgames.events.SynchronizableModelUpdatedEvent;
import baecon.devgames.model.AbsSynchronizable;
import baecon.devgames.model.ISynchronizable;
import baecon.devgames.model.User;
import baecon.devgames.model.update.AbsModelUpdate;
import baecon.devgames.util.L;
import baecon.devgames.util.Utils;
import retrofit.RetrofitError;
import static baecon.devgames.model.ISynchronizable.State.HAS_BLOCKING_CHANGES;
import static baecon.devgames.model.ISynchronizable.State.HAS_UN_SYNCED_CHANGES;


/**
 * <p>Abstracts away a lot of boiler plate code for the tasks that poll for new data.</p>
 *
 * <p>The task retrieves a set of data via {@link #doPoll(DevGamesClient)}, where you implement the call to the
 * back-end. Next, it retrieves a set of data from the local database using {@link #getLocalData(com.j256.ormlite.dao.Dao)}. <br />
 * Both data sets are compared. Based on this, items are added, updated or removed.
 * <strong>So, make sure the constraints of both data sets are the same!</strong><br />
 * Or you'll see some strange things happen with items being removed/added/updated.
 * </p>
 *
 * <p>
 * Once both data sets are retrieved, it compares as follows:
 * <ul>
 * <li>Model in back-end && in db && ! {@link ISynchronizable#contentEquals(Object)} &rarr; do nothing</li>
 * <li>Model in back-end && in db && {@link ISynchronizable#contentEquals(Object)} &rarr; update Model in local db</li>
 * <li>Model ! in back-end && in db && {@link ISynchronizable#contentEquals(Object)} &rarr; remove Model from local db</li>
 * <li>Model in back-end && ! in db && {@link ISynchronizable#contentEquals(Object)} &rarr; add Model to local db</li>
 * </ul>
 * </p>
 *
 * <p>In {@link #doInBackground(Void...)}, before the actual poll call to the REST API is made, we check if the user is
 * still logged in. If the call to the REST API failed because of a missing session, we try once to refresh the session
 * and poll again.</p>
 */
public abstract class ModelPollTask
        <
                Model extends AbsSynchronizable,
                ModelUpdateClass extends AbsModelUpdate,
                ModelDTOClass extends ModelDTO
                >
        extends RESTTask<Void, Void, Integer> {

    private static boolean verboseDebugging = false;

    private Class<Model> modelClazz;

    private Dao<Model, Long> modelDao;
    private Dao<ModelUpdateClass, Long> modelUpdateDao;

    private HashSet<Long> added, removed, updated;

    private List<Long> localModelIdsHavingUpdates;
    private AbsModelManager modelManager;

    /**
     * Creates a new instance of this REST task.
     *
     * @param context
     *         the context from which this task was created.
     * @param modelManager
     *         The SyncManager for this {@link Model}
     */
    public ModelPollTask(Context context, AbsModelManager modelManager) {
        super(context);

        modelDao = getModelDao();
        modelUpdateDao = getModelUpdateDao();

        added = new HashSet<>();
        updated = new HashSet<>();
        removed = new HashSet<>();

        modelClazz = getModelDao().getDataClass();

        this.modelManager = modelManager;
    }

    /**
     * Runs on a background thread. Does return {@code null} immediately if no internet connection is available.
     * <p>First, the logged in user is retrieved and checked. Then {@link #doPoll(DevGamesClient)} is invoked to
     * retrieve the data set from the back-end. If the session expired, we try to refresh the session once. Next, the
     * local data set is retrieved. The uuids of all DTOs from the back-end and local data set are put in a HashSet to
     * get a list of all uuids.</p>
     * <p>Then, the comparison follows (see class javadoc), by iterating over the HashSet with all uuids. See
     * {@link #processItem(ModelDTO, AbsSynchronizable, AbsSynchronizable)}. This method takes care of
     * adding/removing/updating the item in the local database.</p>
     *
     * @param params
     *         Not used.
     *
     * @return Runs the call to the back-end and returns the list of DTOs that you're asking for at the back-end.
     */
    @Override
    protected Integer doInBackground(Void... params) {

        // Fetch all model instances from back-end

        // Any working internet connection available?
        if (!Utils.hasInternetConnection(getApplication())) {
            return null;
        }

        User loggedInUser = getLoggedInUser();
        if (loggedInUser == null) {
            L.e("logged in user is null! This task is probably running outside a logged in session. Skipping...");
            return null; // TODO
        }

        if (verboseDebugging) {
            L.v("loggedInUser {0}", loggedInUser);
        }

        /**
         * Try to make the poll call to the back-end. If a {@link retrofit.RetrofitError} is thrown, and the HTTP status
         * is {@link #FORBIDDEN} or {@link #UNAUTHORIZED}, than try to refresh the session once and execute the poll
         * call again.
         */
        List<ModelDTOClass> dtoFromBackend;
        try {
            dtoFromBackend = doPoll(super.createService());
        }
        catch (RetrofitError error) {
            int status = super.getStatus(error);

            // Also check if the user is still logged in
            // Otherwise this check will login again, that is not a good idea :)
            if ((status == FORBIDDEN || status == UNAUTHORIZED) && getLoggedInUser() != null) {
                super.requestReLogin();
                L.d("user was not logged in, requesting a relogin!");
            }

            L.e(error, "HTTP error: {0}", status);
            return status;
        }
        catch (Exception e) {
            L.e(e, "something unexpected happened");
            return RESTTask.GENERAL_CONNECTION_ERROR;
        }

        HashMap<Long, Model> fromDb;
        HashMap<Long, ModelDTOClass> fromBackend = new HashMap<Long, ModelDTOClass>(Utils.count(dtoFromBackend));

        if (Utils.isNotEmpty(dtoFromBackend)) {
            for (ModelDTOClass dto : dtoFromBackend) {
                if (dto != null) {
                    fromBackend.put(dto.getId(), dto);
                } else {
                    L.w("dto is null! Total dto count is {0}", dtoFromBackend.size());
                }
            }
        }

        // Fetch all Models from the local database to compare them with the ones we receive from the back-end
        try {
            fromDb = getLocalData(modelDao);
        }
        catch (SQLException e) {
            L.e(e, "Could not retrieve the local models from the database");
            return LOCAL_DB_ERROR;
        }
        catch (Exception e) {
            L.e(e, "Could not retrieve the local models from the database");
            return APP_NOT_LOGGED_IN;
        }

        if (Utils.isEmpty(fromDb) && Utils.isEmpty(dtoFromBackend)) {
            L.d("No {0} synchronized, none in local db or from back-end", modelClazz.getSimpleName());
            return RESTTask.NO_CONTENT;
        }

        // Merge all uuids
        HashSet<Long> allModelIds = new HashSet<>();

        if (fromBackend != null) {
            allModelIds.addAll(fromBackend.keySet());
        }

        if (fromDb != null) {
            allModelIds.addAll(fromDb.keySet());
        }

        for (Long id : allModelIds) {

            ModelDTOClass backendDTO = fromBackend.get(id);
            Model backendModel = backendDTO != null ? dtoToModel(backendDTO) : null;
            Model dbModel = fromDb != null ? fromDb.get(id) : null;

            processItem(backendDTO, backendModel, dbModel);
        }

        // We're still in an off UI thread, so let's already grab all model ids that have updates left, otherwise we need
        // to create another AsyncTask, that's just boilerplate stuff, you know.
        // So, grab all models that have local updates left that have to be sync'ed.
        try {

            // Retrieve 'em all from the local update database, with a distinct clause, so we get all ids only once
            List<ModelUpdateClass> modelUpdates = modelUpdateDao.queryBuilder()
                    .distinct()
                    .query();

            // Loop through all ids and put them in a List
            localModelIdsHavingUpdates = new ArrayList<Long>(modelUpdates.size());
            for (ModelUpdateClass modelUpdate : modelUpdates) {
                if (modelUpdate != null) {
                    // Remember that ONLY the local model id is available --> .selectColumns( Column.UUID )
                    localModelIdsHavingUpdates.add(modelUpdate.getId());
                }
            }
        }
        catch (SQLException e) {
            L.e(e, "Could not retrieve the available {0} update local ids", modelClazz.getSimpleName());
        }

        return OK;
    }

    /**
     * Invoked after {@link #doInBackground(Void...)} on the UI thread. Informs the sync manager that any upstream sync
     * tasks should be started
     *
     * @param result
     *         The list of DTOs that came from {@link #doPoll(DevGamesClient)}
     */
    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        // ONLY execute the update task when the user is logged in, otherwise, the app will crash
        if (getApplication().getLoggedInUser() == null) {
            L.w("User is not logged in anymore! Skipping update task...");
            return;
        }

        if (verboseDebugging) {
            L.v("added: {0}", Utils.isNotEmpty(added) ? Arrays.deepToString(added.toArray()) :"null");
            L.v("updated: {0}", Utils.isNotEmpty(updated) ? Arrays.deepToString(updated.toArray()) :"null");
            L.v("removed: {0}", Utils.isNotEmpty(removed) ? Arrays.deepToString(removed.toArray()) :"null");
        }

        // No matter what the result is, start the task with updates to be sync'ed
        if (localModelIdsHavingUpdates != null && localModelIdsHavingUpdates.size() != 0) {
            modelManager.startUpdateTasks(localModelIdsHavingUpdates);
        }

        if (result == null) {
            L.w("Something went terribly wrong while synchronizing {0}(s) to the back-end!", modelClazz.getSimpleName());
        }

        fireUpdatedEvent(result);
    }

    /**
     * Fires the {@link SynchronizableModelUpdatedEvent} after the polling is done. You should provide the event in
     * {@link #getUpdatedEvent(Integer, java.util.HashSet, java.util.HashSet, java.util.HashSet)}, or override this for a custom implementation.
     * <p />
     * This method is invoked from the UI thread in {@link #onPostExecute(Integer)}.
     *
     * @param pollResult The HTTP Status Code of the response from the call to the back-end
     */
    protected void fireUpdatedEvent(Integer pollResult) {
        BusProvider.getBus().post(getUpdatedEvent(pollResult, removed, added, updated));
    }

    protected abstract SynchronizableModelUpdatedEvent getUpdatedEvent(Integer result, HashSet<Long> removed,
                                                                       HashSet<Long> added, HashSet<Long> updated);

    /**
     * Invoked from {@link #doInBackground(Void...)} when we're good to go for a poll to the back-end. This method is not
     * be invoked under the following conditions:
     * <ul>
     * <li>{@link baecon.devgames.DevGamesApplication#getLoggedInUser()} is {@code null}</li>
     * </ul>
     *
     * @param client
     *         The {@link DevGamesClient}, where all the back-end calls are declared
     *
     * @return A list of DTOs for the model that you're polling
     */
    protected abstract List<ModelDTOClass> doPoll(DevGamesClient client) throws SQLException;

    /**
     * Create a {@link Model} from the {@link ModelDTOClass}. Invoked from a background thread.
     *
     * @param data
     *         The ModelDTOClass instance
     *
     * @return A {@link Model} from the {@link ModelDTOClass}
     */
    protected abstract Model dtoToModel(ModelDTOClass data);

    /**
     * @return The {@link com.j256.ormlite.dao.Dao}{@code <}{@link Model}, {@link Long}{@code >}.
     */
    protected abstract Dao<Model, Long> getModelDao();

    /**
     * @return The {@link com.j256.ormlite.dao.Dao}{@code <}{@link ModelUpdateClass}, {@link Long}{@code >}.
     */
    protected abstract Dao<ModelUpdateClass, Long> getModelUpdateDao();

    /**
     * Returns the local data set with the very same constraints, as it is requested by the back-end. By default, that
     * returns all models where {@code ID != null}.
     *
     * @param modelDao
     *         The Dao where we'll execute the query on
     *
     * @return the local data set with the very same constraints, as it is requested by the back-end. By default, that
     * returns all models where {@code ID != null}.
     *
     * @throws java.sql.SQLException
     *         Thrown if something in the query goes wrong
     */
    protected HashMap<Long, Model> getLocalData(Dao<Model, Long> modelDao) throws SQLException {

        HashMap<Long, Model> modelFromDb = new HashMap<>();

        // Retrieve the local version of the data that we fetched from the back-end
        List<Model> allModels = modelDao.queryBuilder().where().isNotNull(ISynchronizable.Column.ID).query();

        if (allModels != null && allModels.size() != 0) {
            for (Model model : allModels) {
                modelFromDb.put(model.getId(), model);
            }
        }

        return modelFromDb;
    }

    /**
     * Processes a Model. In other words, based on in which data set it appears, create, update or delete it from the
     * local database.
     *
     * @param dto
     *         The raw object from the back-end, if the item is available in the back-end data set. May be null
     * @param fromBackend
     *         The converted model from the {@code dto} (using {@link #dtoToModel(ModelDTO)}. May be null
     * @param local
     *         The local model. May be null.
     */
    public void processItem(ModelDTOClass dto, Model fromBackend, Model local) {

        // Whether the Model is in the local database
        boolean inDb = local != null;

        // Whether the Model is in the back-end
        boolean inBackend = fromBackend != null;

        if (inBackend && inDb) {

            // Model is both in the local database and the back-end: UPDATE
            // A model will only be updated when there's no local un synchronized updates anymore AND when the content
            // has changed.
            boolean contentEquals = AbsSynchronizable.contentEquals(local, fromBackend);
            boolean hasModelUpdatesLeft = HAS_UN_SYNCED_CHANGES.equals(local.getState()) || HAS_BLOCKING_CHANGES.equals(local.getState());

            if (!contentEquals && !hasModelUpdatesLeft) {
                onUpdate(dto, fromBackend, local);
            } else {
                // else: //L.v( "{2} not updated, contentEquals={0}, hasModelUpdatesLeft={1}", contentEquals, hasModelUpdatesLeft, modelClazz.getSimpleName() );
                onContentEqualed(dto, fromBackend, local);
            }
        }
        else if (inBackend) {

            // Model is in the back-end, but not in the local database: ADD
            onAdd(dto, fromBackend);

        }
        else if (inDb) {

            // Model is not in the back-end, but it is in the local database: REMOVE
            onRemove(local);

        } else {

            // else: This is hardly possible, either local or fromBackend should be non null
            L.wtf("Both dto and local are null!");
        }
    }

    /**
     * Invoked from {@link #processItem(ModelDTO, AbsSynchronizable, AbsSynchronizable)}, when the item is in
     * the back-end data set, but not in the local data set. This adds the item to the local database. Runs on the
     * background thread.
     *
     * @param dto The raw The raw object from the back-end
     * @param fromBackend The converted model from the {@code dto} (done with {@link #dtoToModel(ModelDTO)}
     */
    protected void onAdd(ModelDTOClass dto, Model fromBackend) {

        try {
            if (verboseDebugging) {
                L.v("Adding new {1} with uuid {0}: {2}", fromBackend.getId(), modelDao.getDataClass().getSimpleName(), fromBackend);
            }
            modelDao.createIfNotExists(fromBackend);
            added.add(fromBackend.getId());
        }
        catch (SQLException e) {
            L.e(e, "Could not add new {0}!", modelDao.getDataClass().getSimpleName());
        }
    }

    /**
     * Invoked from {@link #processItem(ModelDTO, AbsSynchronizable, AbsSynchronizable)}, when the item is not
     * in the back-end data set, but is in the local data set. This removes the item from the local database. Runs on
     * the background thread.
     *
     * @param fromDb The item from the local data set
     */
    protected void onRemove(Model fromDb) {

        try {
            if (verboseDebugging) {
                L.v("Deleting {1} with uuid {0}: {2}", fromDb.getId(), modelDao.getDataClass().getSimpleName(), fromDb);
            }

            if (fromDb != null) {
                modelDao.deleteById(fromDb.getId());
                removed.add(fromDb.getId());
            }
            else {
                L.w("Cannot remove a {0} that is not in the local database!", modelDao.getDataClass().getSimpleName());
            }
        }
        catch (SQLException e) {
            L.e(e, "Could not delete {1} with uuid {0}", fromDb.getId(), modelDao.getDataClass().getSimpleName());
        }
    }

    /**
     * Invoked from {@link #processItem(ModelDTO, AbsSynchronizable, AbsSynchronizable)}, when the item is
     * both in the back-end and local data set. This updates the item in the local database with the one from the
     * back-end item. Runs on the background thread.
     *
     * @param dto The raw object from the back-end, if the item is available in the back-end data set.
     * @param fromBackend The converted model from the {@code dto} (done with {@link #dtoToModel(ModelDTO)}
     * @param fromDb The item from the local data set
     */
    protected void onUpdate(ModelDTOClass dto, Model fromBackend, Model fromDb) {

        try {
            if (fromDb != null) {
                fromBackend.setId(fromDb.getId());

                modelDao.update(fromBackend);

                if (verboseDebugging) {
                    L.i("Updated {1} with uuid {0}", fromBackend.getId(), modelDao.getDataClass().getSimpleName());
                }

                updated.add(fromDb.getId());
            }
            else {
                L.w("Cannot update a {0} that is not in the local database!", modelDao.getDataClass().getSimpleName());
            }
        }
        catch (SQLException e) {
            L.e(e, "Could not delete {1} with uuid {0}", fromBackend.getId(), modelDao.getDataClass().getSimpleName());
        }
    }

    /**
     * Invoked from {@link #processItem(ModelDTO, AbsSynchronizable, AbsSynchronizable)}, when the item is
     * both in the back-end and local data set, but their contents are equal. No CRUD action is taken. Runs on the
     * background thread.
     *
     * @param dto The raw object from the back-end, if the item is available in the back-end data set.
     * @param fromBackend The converted model from the {@code dto} (done with {@link #dtoToModel(ModelDTO)}
     * @param fromDb The item from the local data set
     */
    protected void onContentEqualed(ModelDTOClass dto, Model fromBackend, Model fromDb) {
        // No action required. Could be interesting for subclasses.
        if (verboseDebugging) {
            L.v("fromBackend: {0}", fromBackend);
            L.v("fromDb: {0}", fromDb);
        }
    }
}