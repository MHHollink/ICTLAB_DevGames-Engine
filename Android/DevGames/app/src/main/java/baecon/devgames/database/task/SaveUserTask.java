package baecon.devgames.database.task;

import android.content.Context;

import baecon.devgames.DevGamesApplication;
import baecon.devgames.connection.synchronization.UserManager;
import baecon.devgames.database.DBHelper;
import baecon.devgames.database.modelupdate.Operation;
import baecon.devgames.events.BusProvider;
import baecon.devgames.model.User;
import baecon.devgames.model.update.UserUpdate;
import baecon.devgames.util.L;
import baecon.devgames.util.Utils;
import com.j256.ormlite.dao.Dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Applies an Operation to a User in an asynchronous task.
 * <p />
 * Note that a Delete Operation is merely a flag for deletion. Once the delete command is accepted by the back-end, it
 * will be deleted.
 *
 * @see User
 * @see Operation
 */
public class SaveUserTask extends ModelCUDTask<User, UserUpdate> {

    public SaveUserTask(Context context, Operation operation, User user) {
        super(context, operation, user);
    }

    public SaveUserTask(Context context, Long id, String field, Serializable value) {
        super(context, id, field, value);
    }

    @Override
    protected Dao<User, Long> getModelDao() {
        return DBHelper.getUserDao(getDbHelper());
    }

    @Override
    protected Dao<UserUpdate, Long> getModelUpdateDao() {
        return DBHelper.getUserUpdateDao(getDbHelper());
    }

    @Override
    protected UserUpdate generateModelUpdate(Operation operation, User user) {
        return new UserUpdate(user);
    }

    @Override
    protected void onUpdate(User user) throws SQLException {
        super.onUpdate(user);

        DevGamesApplication app = DevGamesApplication.get(context);
        User loggedInUser = app.getLoggedInUser();

        if (loggedInUser != null && loggedInUser.equals(user)) {
            loggedInUser.merge(user);
            app.setLoggedInUser(loggedInUser);
        }
    }

    @Override
    protected void onUpdateField(Long id, String field, Serializable value) throws SQLException {
        super.onUpdateField(id, field, value);

        DevGamesApplication app = DevGamesApplication.get(context);
        User loggedInUser = app.getLoggedInUser();

        if (loggedInUser != null && (Objects.equals(loggedInUser.getId(), getId()))) {
            app.setLoggedInUser(getModelDao().queryForId(getId()));
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);

        L.v("{0}", result);

        if (getModelUpdate() != null && result != null) {
            UserManager.get(context).offerUpdate(getModelUpdate());
            //TODO BusProvider.getBus().post(new UserPushScheduledEvent(getModelUpdate(), result == UPDATED));
        }
        else {
            L.w("UserUpdate not scheduled! User was null or a local database error occurred");
        }
    }
}
