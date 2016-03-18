package baecon.devgames.connection.task;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;

import baecon.devgames.BuildConfig;
import baecon.devgames.connection.synchronization.UserManager;
import baecon.devgames.database.DBHelper;
import baecon.devgames.model.Setting;
import baecon.devgames.model.User;
import baecon.devgames.util.L;

/**
 * Checks if the logged in user is registered at Google Cloud Messaging. If not, it tries to register. The registration
 * key is saved in {@link User#gcmKey}.
 */
public class GcmRegistrationTask extends RESTTask<Void, Void, String> {

    public static final String REGISTERED = "registered";
    public static final String ERROR_USER_NOT_AVAILABLE = "error_user_not_available";
    public static final String ERROR_DATABASE_ERROR = "error_database_error";
    public static final String ERROR_MISSING_GCM_SENDER_ID = "error_missing_gcm_sender_id";
    public static final String ERROR_PLAY_SERVICES_NOT_AVAILABLE = "error_play_services_not_available";
    public static final String ERROR_GCM_REGISTER_ERROR = "error_gcm_register_error";
    public static final String ERROR_NOT_LOGGED_IN = "error_not_logged_in";

    private String newGCMKey = null;
    private String gcmError = null;
    private User loggedInUser;


    public GcmRegistrationTask(Context context) {
        super(context);
    }

    @Override
    protected String doInBackground(Void[] params) {

        if (!checkPlayServices()) {
            return ERROR_PLAY_SERVICES_NOT_AVAILABLE;
        }

        Dao<Setting, String> settingsDao = DBHelper.getSettingDao(getDbHelper());

        try {
            Setting userUuid = settingsDao.queryForId(Setting.USER_ID);

            if (userUuid == null
                    || TextUtils.isEmpty(userUuid.getValue())) {
                return ERROR_NOT_LOGGED_IN;
            }
        }
        catch (SQLException e) {
            L.e("Could not retrieve the user and password from settings. Aborting register at GCM.");
            return ERROR_NOT_LOGGED_IN;
        }

        Dao<User, Long> userDao = DBHelper.getUserDao(getDbHelper());
        loggedInUser = getLoggedInUser();

        if (loggedInUser == null) {
            L.e("Cannot determine if registered at GCM. User is null");
            return ERROR_USER_NOT_AVAILABLE;
        }

        try {
            userDao.refresh(loggedInUser);
        }
        catch (SQLException e) {
            L.e("Could not refresh User object");
            return ERROR_DATABASE_ERROR;
        }

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

        try {
            newGCMKey = gcm.register(BuildConfig.GCM_SENDER_ID);
            L.d("Registered to GCM, GCMKey : {0}", newGCMKey);
            return REGISTERED;
        }
        catch (IOException e) {

            L.e("Looks like Google Play Services is not available at this moment. Cannot register at GCM.");
            gcmError = e.getMessage();

            return ERROR_GCM_REGISTER_ERROR;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        L.v("called");

        if (loggedInUser == null) {
            L.d("Logged in user is null");
            return;
        }

        String oldKey = loggedInUser.getGcmKey();

        // Only update if the new key is not null or empty, and the new key does not equal the old key
        if (!TextUtils.isEmpty(newGCMKey) && !newGCMKey.equals(oldKey)) {

            L.v("Saving new GCM key!");

            loggedInUser.setGcmKey(newGCMKey);
            UserManager.get(getContext()).update(loggedInUser);

        } else {
            L.i("New GCM key is null, error code: {0}, error from Google Play Services: {1}", result, gcmError);
            // TODO: reschedule an update?
        }
    }

    protected boolean checkPlayServices() {

        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else {
            gcmError = GooglePlayServicesUtil.getErrorString(result);
            return false;
        }
    }
}