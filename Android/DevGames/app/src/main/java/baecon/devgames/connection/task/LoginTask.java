package baecon.devgames.connection.task;

import android.content.Context;
import android.content.res.Resources;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Map;

import baecon.devgames.DevGamesApplication;
import baecon.devgames.connection.client.DevGamesClient;
import baecon.devgames.connection.client.dto.UserDTO;
import baecon.devgames.database.DBHelper;
import baecon.devgames.database.DatabaseConfigUtil;
import baecon.devgames.events.BusProvider;
import baecon.devgames.events.LoginEvent;
import baecon.devgames.model.ISynchronizable;
import baecon.devgames.model.Setting;
import baecon.devgames.model.User;
import baecon.devgames.util.L;
import baecon.devgames.util.PreferenceManager;
import retrofit.RetrofitError;

public class LoginTask extends RESTTask< Void, Void, Integer> {

    // The prefix of the message identifier (see onPostExecute(...))
    private static final String ID_PREFIX = "login_";

    // The resource type of the message identifier (see onPostExecute(...))
    private static final String ID_TYPE = "string";

    // The username.
    private final String username;

    // The plain text password.
    private final String password;

    /**
     * Creates a new LoginTask.
     *
     * @param context
     *         the context from which this task is called.
     * @param username
     *         the username.
     * @param password
     *         the password.
     */
    public LoginTask(Context context, String username, String password) {
        super(context);
        this.username = username;
        this.password = password;
    }


    @Override
    protected Integer doInBackground(Void... params) {

        DevGamesApplication application = (DevGamesApplication) context.getApplicationContext();

        DevGamesClient client = super.createService();

        final String session;

        // If it is not the same user that logs in, first remove all the data
        Dao<Setting, String> settingDao = DBHelper.getSettingDao(getDbHelper());

        // First try to get the session ID from Ask.
        try {
            Map<String, String> map = client.login(username, password);
            session = map.get(DevGamesApplication.SESSION_HEADER_KEY);

            if (session == null) {
                L.wtf("this should never happen: response {0} returned OK " +
                        "but does not contain {1}", map, DevGamesApplication.SESSION_HEADER_KEY);
                return INTERNAL_SERVER_ERROR;
            }
        }
        catch (RetrofitError e) {
            return super.getStatus(e);
        }
        catch (NullPointerException e) {
            return RESTTask.BACK_END_OFFLINE;
        }

        L.d("retrieved session: {0}", session);

        // Check if the userUuid stored in the database matches the given userUuid from the LoginActivity.
        // If they match, keep all the data. Otherwise, first delete all data before continuing
        try {
            Setting dbUsername = settingDao.queryForId(Setting.USER_ID);
            if (dbUsername != null && dbUsername.getValue() != null
                    && username != null
                    && dbUsername.getValue().equals(username)) {
                L.d("User is the same, data is retained");

            }
            else {
                L.w("The user stored in the database does not match the given userUuid from the " +
                        "LoginActivity! Deleting all the data...");

                for (Class<?> clz : DatabaseConfigUtil.CLASSES) {
                    L.v("Clearing table {0}", clz.getSimpleName());
                    TableUtils.clearTable(settingDao.getConnectionSource(), clz);
                }

                // Before clearing all settings, we need to remember whether the user wanted its
                // username to be remembered
                PreferenceManager pm = PreferenceManager.get(context);
                boolean isRememberPasswordEnabled = pm.isRememberPasswordEnabled();
                String lastUsedUsername = pm.getLastUsedUsername();

                PreferenceManager.clearAllSettings(context);
                PreferenceManager.applyDefaultPreferences(context);

                // Put back the remembered username
                pm.setRememberPasswordEnabled(isRememberPasswordEnabled);
                pm.setLastUsedUsername(lastUsedUsername);

                application.setSession(session);

                try {
                    UserDTO dto = client.getCurrentUser();

                    User loggedInUser = dto.toModel();

                    Dao<User, Long> userDao = DBHelper.getUserDao(getDbHelper());

                    User existingLoggedInUser = userDao.queryBuilder().where().eq(ISynchronizable.Column.ID, loggedInUser.getId()).queryForFirst();
                    if (existingLoggedInUser != null) {
                        loggedInUser.setId(existingLoggedInUser.getId());
                    }

                    userDao.createOrUpdate(loggedInUser);
                    userDao.refresh(loggedInUser);

                    settingDao.createOrUpdate(new Setting(Setting.USERNAME, loggedInUser.getUsername()));
                    settingDao.createOrUpdate(new Setting(Setting.USER_ID, String.valueOf(loggedInUser.getId())));
                    settingDao.createOrUpdate(new Setting(Setting.SESSION_ID, session));

                    application.setLoggedInUser(loggedInUser);
                }
                catch (RetrofitError e) {
                    L.e(e, "could not retrieve user with username={0}:", username);
                    return INTERNAL_SERVER_ERROR;
                }
                catch (SQLException e) {
                    L.e(e, "something went wrong while storing {0} into the local db:", username);
                    return LOCAL_DB_ERROR;
                }
                catch (Exception e) {
                    L.e(e, "something went wrong");
                    return 0;
                }

                // TODO
                // get moar needed data
            }
        }
        catch (Exception e) {
            L.e(e, "Something went wrong while checking the current saved user");
            return LOCAL_DB_ERROR;
        }


        return OK;
    }

    @Override
    protected void onPostExecute(Integer httpStatus) {
        super.onPostExecute(httpStatus);
        L.d("onPostExecute, httpStatus={0}", httpStatus);

        Resources resources = this.context.getResources();
        boolean success = (httpStatus == OK);
        String message = null;

        // See if there's a predefined message for `httpStatus`.
        int messageId = resources.getIdentifier(ID_PREFIX + httpStatus,
                ID_TYPE, context.getPackageName());

        if (!success && messageId > 0) {
            message = resources.getString(messageId);
        }

        BusProvider.getBus().post(new LoginEvent(success, message));
    }
}
