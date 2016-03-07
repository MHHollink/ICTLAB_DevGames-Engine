package baecon.devgames.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Utils {

    private static final String TAG = Utils.class.getName();

    private Utils(){}

    /**
     * Returns the version code from the manifest
     *
     * @return The version code from the manifest
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getAppVersion: Could not retrieve versionName");
            return -1;
        }
    }

    /**
     * Returns the version name from the manifest.
     *
     * @return The version name from the manifest
     */
    public static String getAppVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getAppVersionName: Could not retrieve versionName");
            return "";
        }
    }


    /**
     * Returns true when there's a working internet connection available, otherwise false. Uses the
     * {@link android.net.ConnectivityManager}
     *
     * @param context
     *         Context to determine whether a working connection is available
     *
     * @return Returns true when there's a working internet connection available, otherwise false
     */
    public static boolean hasInternetConnection(Context context) {

        if (context == null) {
            return false;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
