package baecon.devgames.database.task;


import android.content.Context;

import baecon.devgames.database.modelupdate.IModelUpdate;
import baecon.devgames.database.modelupdate.Operation;
import baecon.devgames.model.ISynchronizable;
import baecon.devgames.util.L;
import baecon.devgames.util.Utils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.UUID;

import static baecon.devgames.model.ISynchronizable.State;

/**
 * An asynchronous task, that does a CUD (Create, Update or Delete) operation on a {@link ISynchronizable}. Note that a
 * {@link baecon.devgames.database.modelupdate.Operation#DELETE} is merely a flag for deletion. Once the back-end removed it, it will automatically be
 * removed through the procedure during a {@link baecon.devgames.connection.task.poll.ModelPollTask}. <p/> Passing {@code null} as the operation or model,
 * will result in an IllegalArgumentException. Be warned!
 * <p/>
 * <b>Handling return values</b><br />
 * {@link #onPostExecute(Object)} can get the following result:
 * <ul>
 *     <li>{@link #UPDATED}, which means the operation succeeded</li>
 *     <li>{@link #DB_ERROR}, which means something went wrong during a database operation</li>
 * </ul>
 */
public abstract class ModelCUDTask<Model extends ISynchronizable, ModelUpdate extends IModelUpdate<Model>> extends DBTask<Void, Void, Integer> {

    /**
     * The kind of the operation. Is not allowed to be null: an IllegalArgumentException will be thrown
     */
    private Operation operation;

    /**
     * Only used when {@link #ModelCUDTask(android.content.Context, Operation, ISynchronizable)} is invoked.
     * The model instance that will undergo the operation.
     * Is not allowed to be null: an IllegalArgumentException will be thrown
     */
    private Model model;

    /**
     * Only used when {@link #ModelCUDTask(android.content.Context, Long, String, java.io.Serializable)} is invoked. The
     * new value for the {@link #field}. Is not allowed to be 0L: an IllegalArgumentException will be thrown
     */
    private Long id;

    /**
     * Only used when {@link #ModelCUDTask(android.content.Context, Long, String, java.io.Serializable)} is invoked
     */
    private String field;

    /**
     * Only used when {@link #ModelCUDTask(android.content.Context, Long, String, java.io.Serializable)} is invoked. The
     * new value for the {@link #field}. May be null.
     */
    private Serializable value;


    private ModelUpdate update;

    /**
     * Creates a new CUD task. In case of an {@link Operation#UPDATE}, the whole model will be replaced.
     *
     * @param context
     *         The Context to access the database
     * @param operation
     *         The kind of operation that the model will undergo. Passing null results in a IllegalArgumentException
     * @param model
     *         The model that will undergo the operation. Passing null results in an IllegalArgumentException
     */
    public ModelCUDTask(Context context, Operation operation, Model model) {
        super(context);

        this.operation = operation;
        this.model = model;

        if (this.operation == null) {
            throw new IllegalArgumentException("Null is not an operation");
        }

        if (this.model == null) {
            throw new IllegalArgumentException("Model is not allowed to be null");
        }
    }

    /**
     * Creates a new Update task to update one field of the model.
     *  @param context
     *         The Context to access the database
     * @param id
     *         The id of the model in the local database
     * @param field
     *         The column of the local database. Can not be null.
     * @param value
     *         The new value for the {@code field}. Can be null.
     */
    public ModelCUDTask(Context context, Long id, String field, Serializable value) {
        super(context);

        this.operation = Operation.UPDATE_FIELD;

        this.id = id;
        this.field = field;
        this.value = value;

        if (id == null || id == 0) {
            throw new IllegalArgumentException("localModelId is not allowed to be null or 0");
        }

        if (Utils.isEmpty(field)) {
            throw new IllegalArgumentException("Field is not allowed to be null");
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        L.v("{0}", this);
    }

    @Override
    protected Integer doInBackground(Void... params) {

        try {

            executeOperation();

            insertModelUpdate(operation, model);

            return UPDATED;
        }
        catch (SQLException e) {
            L.e(e, "Something went wrong while doing a CUD operation");
            return DB_ERROR;
        }
        catch (Exception e) {
            L.e(e, "General error occured");
            return GENERAL_ERROR;
        }
    }

    protected void executeOperation() throws SQLException {

        switch (operation) {

            case CREATE:
                onCreate(model);
                break;

            case UPDATE:
                onUpdate(model);
                break;

            case UPDATE_FIELD:
                onUpdateField(id, field, value);

            case DELETE:
                onDelete(model);
                break;

            default:
                throw new RuntimeException("Unknown operation: " + operation != null ? operation.name() : "null");
        }
    }

    /**
     * Returns the model. Of course, with all applied changes.
     *
     * @return The model. Of course, with all applied changes.
     */
    protected Model getModel() {
        return model;
    }

    protected ModelUpdate getModelUpdate() {
        return update;
    }

    public Operation getOperation() {
        return operation;
    }

    public Long getId() {
        return id;
    }

    public String getField() {
        return field;
    }

    public Serializable getValue() {
        return value;
    }

    /**
     * Creates the model in the database, and sets the state to {@link ISynchronizable.State#NEW}.
     *
     * @param model The model that should be created, as passed in the constructor
     *
     * @throws java.sql.SQLException If an exception occurs during database operations
     */
    protected void onCreate(Model model) throws SQLException {

        // TODO don't user 0 for new models
        model.setId(0l);
        model.setState(State.NEW);

        L.v("{0}", model);
        getModelDao().create(model);
    }

    /**
     * Calls {@link com.j256.ormlite.dao.Dao#update(Object)} with the model, as passed in the constructor. If the model has the state
     * {@link ISynchronizable.State#UP_TO_DATE}, it will be changed to {@link ISynchronizable.State#HAS_UN_SYNCED_CHANGES}. Otherwise, state will not
     * be changed.
     *
     * @param model The model that should be updated, as passed in the constructor
     *
     * @throws java.sql.SQLException If an exception occurs during database operations
     */
    protected void onUpdate(Model model) throws SQLException {

        if (model.getState().equals(State.UP_TO_DATE)) {
            model.setState(State.HAS_UN_SYNCED_CHANGES);
        }

        L.v("Before: {0}", model);
        getModelDao().update(model);
        getModelDao().refresh(model);
        L.v("After: {0}", model);
    }

    protected void onUpdateField(Long id, String field, Serializable value) throws SQLException {

        UpdateBuilder<Model, Long> ub = getModelDao().updateBuilder();

        ub.updateColumnValue(field, value);
        ub.where().idEq(id);
        ub.update();

        model = getModelDao().queryForId(id);

        ub.reset();

        if (State.UP_TO_DATE.equals(model.getState())) {
            ub.updateColumnValue(ISynchronizable.Column.STATE, State.HAS_UN_SYNCED_CHANGES);
            ub.where().idEq(id);
            ub.update();
        }

        getModelDao().refresh(model);
    }

    /**
     * Flags a model for deletion. This <b>does not</b> remove the model from the database. Once the back-end removed
     * it, it will automatically be removed through the procedure during a {@link baecon.devgames.connection.task.poll.ModelPollTask}.
     *
     * @param model The model that should be flagged for deletion.
     *
     * @throws java.sql.SQLException If an exception occurs during database operations
     */
    protected void onDelete(Model model) throws SQLException {
        model.setState(State.FLAGGED_FOR_DELETE);
        getModelDao().update(model);
        getModelDao().refresh(model);
    }

    /**
     * Inserts the {@link #update} in the ModelUpdate database table. The ModelUpdate is retrieved using
     * {@link #generateModelUpdate(Operation, ISynchronizable)}
     *
     * @param operation The kind of operation that the model went through
     * @param model The model
     *
     * @throws java.sql.SQLException If an exception occurs during database operations
     */
    protected void insertModelUpdate(Operation operation, Model model) throws SQLException {

        update = generateModelUpdate(operation, model);
        update.setOperation(operation);
        getModelUpdateDao().create(update);
    }

    /**
     * Returns the Dao for the Model. The implementation has to provide this, because a {@link Class} is needed as a
     * parameter when retrieving the Dao. A generic is not sufficient there.
     *
     * @return The Dao for the Model
     */
    protected abstract Dao<Model, Long> getModelDao();

    /**
     * Returns the Dao for the ModelUpdate. The implementation has to provide this, because a {@link Class} is needed as
     * a parameter when retrieving the Dao. A generic is not sufficient there.
     *
     * @return The Dao for the ModelUpdate
     */
    protected abstract Dao<ModelUpdate, Long> getModelUpdateDao();

    /**
     * Returns a ModelUpdate for the Model. This ModelUpdate will be placed in the table where all 'changes' to a Model
     * are kept, until they are synchronized with the back-end.
     *
     * @param operation The kind of operation that the model went through
     * @param model The model
     *
     * @return A ModelUpdate for the Model
     */
    protected abstract ModelUpdate generateModelUpdate(Operation operation, Model model);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "operation=" + operation +
                ", model=" + model +
                ", id=" + id +
                ", field='" + field + '\'' +
                ", value=" + value +
                "} " + super.toString();
    }
}
