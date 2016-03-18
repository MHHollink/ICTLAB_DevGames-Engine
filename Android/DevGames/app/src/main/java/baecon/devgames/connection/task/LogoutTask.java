package baecon.devgames.connection.task;

import android.content.Context;

import baecon.devgames.DevGamesApplication;
import baecon.devgames.database.DBHelper;
import baecon.devgames.events.BusProvider;
import baecon.devgames.events.LogoutEvent;
import baecon.devgames.model.Setting;
import baecon.devgames.model.User;
import baecon.devgames.model.update.UserUpdate;
import baecon.devgames.util.L;
import baecon.devgames.util.Utils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;

/**
 * An AsyncTask that logs out the user.
 */
public class LogoutTask extends RESTTask<Void, Void, Integer> {

    protected final boolean checkUnSyncedWork;

    /**
     * Creates a new instance of this REST task.
     *
     * @param context
     *         The context from which this task was created.
     * @param checkUnSyncedWork
     *         Whether the task should check if any model has un synchronized work left
     */
    public LogoutTask(Context context, boolean checkUnSyncedWork) {
        super(context);
        this.checkUnSyncedWork = checkUnSyncedWork;
    }

    @Override
    protected Integer doInBackground(Void... params) {

        if (checkUnSyncedWork) {

            // If one of the sync managers has work left, first warn the user before logging out

            boolean unsyncedWorkLeft = false;
            try {
                Dao<UserUpdate, Long> userUpdateDao = DBHelper.getUserUpdateDao(getDbHelper());

                if (userUpdateDao.countOf() != 0) {
                    unsyncedWorkLeft = true;
                    L.v("Un synchronized user updates: {0}", userUpdateDao.countOf());
                    L.v("{0}", Utils.collectionToString(userUpdateDao.queryForAll()));
                }

            }
            catch (SQLException e) {
                L.e(e, "Could not determine if there's any work left in the database");
            }

            if (unsyncedWorkLeft) {
                return APP_HAS_UN_SYNCHRONIZED_WORK;
            }
        }


        // Unregister from GCM, we should not get messages when logged out
        // The GCM key of the logged in user is also set to 'null' later on
        try {
            L.v("Trying to unregister from GCM");
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            gcm.unregister();
            L.i("Unregistered from GCM");
        }
        catch (IOException e) {
            L.e("Could not unregister from GCM");
        }

        DevGamesApplication app = DevGamesApplication.get(context);

        try {
            final User user = getLoggedInUser();
            User changes = new User();

            changes.setId(user.getId());
            changes.logout();

            user.logout();

            UserUpdate userUpdate = new UserUpdate(user.getId(), changes);

            userUpdate.sync(context, getApplication().getDbHelper(), createService());
        } catch (Exception e) {
            L.e("Could not save user state to the back-end!");
        }

        // Remove the session id from the application
        getApplication().setSession(null);

        // And delete the username, passwordHash, useruuid and session id from the database
        Dao<Setting, String> settings = DBHelper.getSettingDao(getDbHelper());
        try {
            settings.deleteBuilder().delete();
        }
        catch (SQLException e) {
            L.e(e, "Could not delete the session id from the database");
        }

        app.getDbHelper().cleanUpObjectCache();

        return OK;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        if (integer != null && integer == APP_HAS_UN_SYNCHRONIZED_WORK) {
            BusProvider.getBus().post(new LogoutEvent(true));
        }
        else {
            BusProvider.getBus().post(new LogoutEvent(false));
        }
    }
}