package baecon.devgames;

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
    public static final String SESSION_HEADER_KEY = "r";

    private static final long DEFAULT_CONNECTION_TIMEOUT = 60 * 1000L;
    private static final long DEFAULT_READ_TIMEOUT = 60 * 1000L;

    /**
     * Date formats used throughout the entire app to keep it on a consistent basis
     */
    public SimpleDateFormat formatterHourMinute, formatterDayMonthYear, formatterDayMonthHourMinute;

    private PreferenceManager preferenceManager;
    private DevGamesClient devGamesClient;
    private DBHelper dbHelper;
    private String session = null;
    private User loggedInUser;

    @Override
    public void onCreate() {
        super.onCreate();

        L.v("Called");

        preferenceManager = PreferenceManager.get(this);

        dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        Dao<Setting, String> settingDao = DBHelper.getSettingDao(dbHelper);

        try {
            String loggedInUserUuid = settingDao.queryForId(Setting.USERNAME).getValue();

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

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        L.v("Called");

        dbHelper.cleanUpObjectCache();
    }


    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        L.v("loggedInUser={0}", loggedInUser != null ? loggedInUser.toString() : "null");
        this.loggedInUser = loggedInUser;
    }

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

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

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        L.v("session={0}", session);
        this.session = session;
    }

    private static OkHttpClient getOkHttpClient(long connectTimeoutMillis, long readTimeoutMillis) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(connectTimeoutMillis, TimeUnit.MILLISECONDS);
        client.setReadTimeout(readTimeoutMillis, TimeUnit.MILLISECONDS);
        return client;
    }

    public static DevGamesApplication get(Context context) {
        return (DevGamesApplication) context.getApplicationContext();
    }

    public static DevGamesApplication get(Fragment context) {
        return (DevGamesApplication) context.getActivity().getApplicationContext();
    }
}
