package baecon.devgames.connection.task;


import android.content.Context;

import java.sql.SQLException;
import java.util.Map;

import baecon.devgames.DevGamesApplication;
import baecon.devgames.connection.client.DevGamesClient;
import baecon.devgames.database.DBHelper;
import baecon.devgames.model.ISynchronizable;
import baecon.devgames.model.Setting;
import baecon.devgames.model.User;
import baecon.devgames.util.L;
import baecon.devgames.util.MultiThreadedAsyncTask;
import retrofit.RetrofitError;

/**
 * A base class responsible for making asynchronous REST calls to the DevGames
 * backend. All Tasks in this package that extend the RESTTask are therefor
 * asynchronous tasks.
 *
 * This class can be executed from a back- or foreground service, or a GUI
 * component, like an Activity or Fragment.
 *
 * @param <P>
 *         the type of the Parameter provided to this class.
 * @param <I>
 *         the Increment type. For example, if a progress bar needs to
 *         show the progress of this task, this type would probably be an
 *         integer (going from 0% to 100%).
 * @param <R>
 *         the Return type of this task.
 */
@SuppressWarnings("unused")
public abstract class RESTTask<P, I, R> extends MultiThreadedAsyncTask<P, I, R> {

    public static int SESSION_REFRESH_COUNTER = 0;
    private static final long LOADED = System.currentTimeMillis();

    private static final long DEFAULT_CONNECTION_TIMEOUT = 60 * 1000L;
    private static final long DEFAULT_READ_TIMEOUT = 60 * 1000L;

    /**
     * Some HTTP status codes.
     */
    public final static int OK = 200;
    public final static int CREATED = 201;
    public final static int NO_CONTENT = 204;
    public final static int BAD_REQUEST = 400;
    public final static int UNAUTHORIZED = 401;
    public final static int FORBIDDEN = 403;
    public final static int NOT_FOUND = 404;
    public final static int INTERNAL_SERVER_ERROR = 500;

    /**
     * Some local error codes
     */
    public final static int LOCAL_DB_ERROR = 1000;
    public final static int VIEW_NOT_VISIBLE_ANYMORE = 1001;
    public final static int GENERAL_CONNECTION_ERROR = 1002;
    public final static int LOCAL_FILE_SYSTEM_ERROR = 1003;
    public final static int CACHED_COPY_AVAILABLE = 1004;
    public final static int APP_INITIALIZING = 1005;
    public final static int APP_NOT_LOGGED_IN = 1006;
    public final static int APP_HAS_UN_SYNCHRONIZED_WORK = 1007;
    public final static int BACK_END_OFFLINE = 1008;

    /**
     * The context from which this task is started.
     */
    final Context context;

    // A flag indicating if this Task recently tried to refresh
    // a stale session token.
    private boolean triedToRefreshSession;

    /**
     * Creates a new instance of this REST task.
     *
     * @param context the context from which this task was created.
     */
    public RESTTask(Context context) {
        this.context = context;
        this.triedToRefreshSession = false;
    }

    protected Context getContext() {
        return context;
    }

    /**
     * Creates an instance of a Retrofit service implementation. The
     * serviceClass-interface passed as a parameter are defined in the
     * child classes of this base REST class.
     *
     * @return an instance of a Retrofit service implementation.
     */
    public <T> DevGamesClient createService() {
        return getApplication().getDevGamesClient();
    }

    /**
     * Retrieves the HTTP status of a RetrofitError.
     *
     * @param error The error from which to retrieve the status.
     * @return the HTTP status of a RetrofitError.
     */
    public int getStatus(RetrofitError error) {

        // TODO fix this hack!
        if (error != null && error.getMessage() != null &&
                error.getMessage().contains("authentication challenge is null")) {
            return UNAUTHORIZED;
        }

        if (error == null || error.getResponse() == null) {
            return GENERAL_CONNECTION_ERROR;
        }

        return error.getResponse().getStatus();
    }

    /**
     * A method that tries to re-login and, if successful, stores the
     * new session in the local DB.
     *
     * @return true iff the session was successfully refreshed AND this
     * new session was successfully store in the local database.
     */
    public boolean refreshSession() {

        if (this.triedToRefreshSession) {
            // We already tried to refresh the session, but apparently failed.
            return false;
        }

        this.triedToRefreshSession = true;

        DevGamesApplication application = (DevGamesApplication) context.getApplicationContext();
        User loggedInUser = getLoggedInUser();
        String session;
        String passwordHash;

        try {
            passwordHash = DBHelper.getSettingDao(getDbHelper())
                    .queryBuilder()
                    .where()
                    .eq(Setting.Column.KEY, Setting.PASSWORD_HASH).queryForFirst()
                    .getValue();

            if (passwordHash == null) {
                L.e("Password hash is null, cannot refresh login");
                return false;
            }

            DevGamesClient client = this.createService();
            Map<String, String> response = client.login(loggedInUser.getUsername(), passwordHash);

            if (response == null || !response.containsKey(DevGamesApplication.SESSION_HEADER_KEY)) {
                L.w("refreshSession, response={0}", response);
                return false;
            }

            session = response.get(DevGamesApplication.SESSION_HEADER_KEY);
        } catch (Exception e) {
            L.e(e, "something went wrong trying to refresh the session");
            return false;
        }

        // We got a session, store it in the local database.
        try {
            DBHelper.getSettingDao(getDbHelper()).createOrUpdate(new Setting(Setting.SESSION_ID, session));
            application.setSession(session);
        } catch (SQLException e) {
            L.e(e, "something went wrong while saving data");
            return false;
        }

        L.d("successfully refreshed session");

        SESSION_REFRESH_COUNTER++;
        L.v("{0} refreshed sessions in {1} seconds", SESSION_REFRESH_COUNTER,
                ((System.currentTimeMillis() - LOADED) / 1000));

        return true;
    }

    /**
     * Returns the {@link DevGamesApplication}.
     *
     * @return The {@link DevGamesApplication}.
     */
    protected DevGamesApplication getApplication() {
        return DevGamesApplication.get(context);
    }

    protected DBHelper getDbHelper() {
        return getApplication().getDbHelper();
    }

    protected User getLoggedInUser() {
        try {
            Setting userUuid = DBHelper.getSettingDao(getDbHelper()).queryForId(Setting.USER_ID);

            if (userUuid == null) {
                return null;
            }

            return DBHelper.getUserDao(getDbHelper()).queryBuilder().where().eq(ISynchronizable.Column.ID, userUuid.getValue()).queryForFirst();
        } catch (SQLException e) {
            L.e(e, "Could not get logged in user");
            return null;
        }
    }
}