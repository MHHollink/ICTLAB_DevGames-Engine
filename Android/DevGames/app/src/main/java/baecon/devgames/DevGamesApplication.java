package baecon.devgames;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.squareup.okhttp.OkHttpClient;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import baecon.devgames.connection.client.DevGamesClient;
import baecon.devgames.database.DBHelper;
import baecon.devgames.model.ISynchronizable;
import baecon.devgames.model.Setting;
import baecon.devgames.model.User;
import baecon.devgames.util.L;
import baecon.devgames.util.PreferenceManager;

import retrofit.Endpoint;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * The App's context holding some data like the logged in user
 */
public class DevGamesApplication extends Application {

    /**
     * The key point to the key-value pair for the session in each request header
     */
    public static final String SESSION_HEADER_KEY = "SESSION_ID";
    private String session = null;

    private static final long DEFAULT_CONNECTION_TIMEOUT = 60 * 1000L;
    private static final long DEFAULT_READ_TIMEOUT = 60 * 1000L;

    /**
     * Date formats used throughout the entire app to keep it on a consistent basis
     */
    public SimpleDateFormat formatterHourMinute, formatterDayMonthYear, formatterDayMonthHourMinute;

    private PreferenceManager preferenceManager;
    private DevGamesClient devGamesClient;
    private DBHelper dbHelper;
    private User loggedInUser;

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onCreate() {
        super.onCreate();

        L.v("Called");

        preferenceManager = PreferenceManager.get(this);

        dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        Dao<Setting, String> settingDao = DBHelper.getSettingDao(dbHelper);

        try {
            String loggedInUserUuid = settingDao.queryForId(Setting.USER_ID).getValue();

            loggedInUser = (loggedInUserUuid != null && !loggedInUserUuid.isEmpty()) ?
                    getUser(Long.valueOf(loggedInUserUuid)) :
                    getUser(0l);

            L.d("loaded app, loggedInUser={0}", loggedInUser);
        }
        catch (Exception e) {
            L.w(e, "could not get the user from the local DB. Probably not logged in yet");
        }

        formatterHourMinute = new SimpleDateFormat(getString(R.string.date_HH_mm));
        formatterDayMonthYear = new SimpleDateFormat(getString(R.string.date_dd_MM_yyyy));
        formatterDayMonthHourMinute = new SimpleDateFormat(getString(R.string.date_dd_MMM_HH_mm));

        devGamesClient = new RestAdapter.Builder()
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        if (session != null && !session.isEmpty()) {
                            request.addHeader(SESSION_HEADER_KEY, session);
                        }
                    }
                })
                .setClient(new OkClient(
                        getOkHttpClient(
                                DEFAULT_CONNECTION_TIMEOUT,
                                DEFAULT_READ_TIMEOUT
                        )
                ))
                .setEndpoint(new Endpoint() {
                    @Override
                    public String getUrl() {
                        return BuildConfig.ENDPOINT_URL;
                    }

                    @Override
                    public String getName() {
                        return "devgames-backend";
                    }
                })
                .setConverter(new GsonConverter(
                                new GsonBuilder()
                                        .serializeNulls()
                                        .create()
                        )
                )
                .setLog(new AndroidLog("retrofit"))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build()
                .create(DevGamesClient.class);

    }

    /**
     * Cleans the object cache in the database when the app is running on low memory
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();

        L.v("Called");

        dbHelper.cleanUpObjectCache();
    }


    /**
     * Gets the loggedInUser as {@link User} object from the app context
     *
     * @return currently logged in user
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Sets the loggedInUser as object in the app context for easier usage
     *
     * @param loggedInUser the new loggedInUser
     */
    public void setLoggedInUser(User loggedInUser) {
        L.v("loggedInUser={0}", loggedInUser != null ? loggedInUser.toString() : "null");
        this.loggedInUser = loggedInUser;
    }

    /**
     * Gets in instance of the PreferenceManager used in the app.
     * This preference manager contains calls used to and from the preferences and handles the tags used.
     *
     * @return Instance of the PreferenceManager
     */
    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    /**
     * Gets an instance of the DevGamesClient where all the REST calls are declared.
     *
     * @return instance of the DevGamesClient
     */
    public DevGamesClient getDevGamesClient() {
        return devGamesClient;
    }

    public DBHelper getDbHelper() {
        return dbHelper;
    }

    /**
     * Get a {@link User} by its id
     *
     * @param id
     *         The id of the User
     *
     * @return The User, otherwise null if not found or when an error occurred
     */
    private User getUser(Long id) {

        try {
            return DBHelper.getUserDao(dbHelper)
                    .queryBuilder()
                    .where()
                    .eq(ISynchronizable.Column.ID, id)
                    .queryForFirst();

        }
        catch (Exception e) {
            L.e(e, "could not get user with id {0} from the local DB", id);
        }
        return null;
    }

    /**
     * Gets the current session for the REST calls.
     *
     * @return String value of Session Token
     */
    public String getSession() {
        return session;
    }

    /**
     * Sets the session which was returned from the server after login
     * Each request (expect /login) uses this session token for identification and authentication for the call
     *
     * @param session String value of the Session token
     */
    public void setSession(String session) {
        L.v("session={0}", session);
        this.session = session;
    }

    /**
     * Creates and returns an instance of the OkHttpClient used for the REST calls
     */
    private static OkHttpClient getOkHttpClient(long connectTimeoutMillis, long readTimeoutMillis) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(connectTimeoutMillis, TimeUnit.MILLISECONDS);
        client.setReadTimeout(readTimeoutMillis, TimeUnit.MILLISECONDS);
        return client;
    }

    /**
     * Gets the instance of the DevGamesApplication by using the Context of the running activity/task.
     *
     * @param context Fragment which calls the method
     * @return instance of DevGamesApplication
     */
    public static DevGamesApplication get(Context context) {
        return (DevGamesApplication) context.getApplicationContext();
    }

    /**
     * Gets the instance of the DevGamesApplication by using the parent activity of the running fragment.
     *
     * @param context Fragment which calls the method
     * @return instance of DevGamesApplication
     */
    public static DevGamesApplication get(Fragment context) {
        return (DevGamesApplication) context.getActivity().getApplicationContext();
    }
}
