package baecon.devgames.database.task;

import android.content.Context;

import baecon.devgames.DevGamesApplication;
import baecon.devgames.database.DBHelper;
import baecon.devgames.model.ISynchronizable;
import baecon.devgames.model.Setting;
import baecon.devgames.model.User;
import baecon.devgames.util.L;
import baecon.devgames.util.MultiThreadedAsyncTask;

import java.sql.SQLException;

/**
 * A base class for asynchronous database calls.
 *
 * @param <P>
 *         The type of parameters passed to the `doInBackground` method.
 * @param <I>
 *         The type of the increment (Integer usually).
 * @param <R>
 *         The type of the return value of the `doInBackground` method.
 */
public abstract class DBTask<P, I, R> extends MultiThreadedAsyncTask<P, I, R> {

    /**
     * The context from which this task is started.
     */
    final Context context;

    /**
     * Some error/info codes.
     */
    public static final int UPDATED = 2000;
    public static final int NO_WORK = 2001;
    public static final int DB_ERROR = 2002;
    public static final int GENERAL_ERROR = 2003;
    public static final int FLAGGED_FOR_DELETE = 2004;

    /**
     * Creates a new instance of this database task.
     *
     * @param context
     *         the context from which this task was created.
     */
    public DBTask(Context context) {
        this.context = context;
    }

    protected User getLoggedInUser() {
        try {
            Setting userUuid = DBHelper.getSettingDao(getDbHelper()).queryForId(Setting.USER_ID);

            if (userUuid == null) {
                return null;
            }

            return DBHelper.getUserDao(getDbHelper()).queryBuilder().where().eq(ISynchronizable.Column.ID, userUuid.getValue()).queryForFirst();
        }
        catch (SQLException e) {
            L.e(e, "Could not get logged in user");
            return null;
        }
    }

    protected DBHelper getDbHelper() {
        return DevGamesApplication.get(context).getDbHelper();
    }
}