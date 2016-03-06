package baecon.devgames.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;

/**
 * A manager class that is responsible for all preferences that are stored in the {@link
 * android.content.SharedPreferences}. Each setting has a get/set function and a {@code public static final String}
 * declaration with the key, prefixed with PREF_. For example:
 * <p/>
 * <pre>
 *   // Boolean value. Whether the username should be remembered the next time the user visits the LoginActivity.
 *   public static final String PREF_REMEMBER_USERNAME = "pref_remember_username";
 * </pre>
 * <p/>
 * You can use some convenience methods to prevent a lot of boiler plate code.
 *
 * @see #putString(String, String)
 */
public class PreferenceManager {

    SharedPreferences sharedPreferences;

    static PreferenceManager instance;

    private PreferenceManager(Context context) {
        sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void applyDefaultPreferences(Context context) {

        PreferenceManager preferenceManager = get(context);

        // RESET ALL PREFERENCES
        // ...

    }

    public static void clearAllSettings(Context context) {

        PreferenceManager prefs = get(context);
        prefs.sharedPreferences.edit().clear().commit();

    }

    /**
     * @param context
     *         Context to access {@link android.content.SharedPreferences}
     *
     * @return Returns an instance of this PreferenceManager
     */
    public static PreferenceManager get(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context);
        }
        return instance;
    }

    /**
     * @param fragment
     *         Context to access {@link android.content.SharedPreferences}
     *
     * @return Returns an instance of this PreferenceManager
     */
    public static PreferenceManager get(Fragment fragment) {
        if (instance == null) {
            instance = new PreferenceManager(fragment.getActivity());
        }
        return instance;
    }






    /**
     * Boolean value. Whether the username should be saved when a login request succeeds. The password should never be
     * saved. Default false.
     *
     * @see #isRememberPasswordEnabled()
     */
    public static final String PREF_REMEMBER_PASSWORD_ENABLED = "pref_remember_password_enabled";

    /**
     * Returns whether the username should be saved when a login request succeeds.
     *
     * @return whether the username should be saved when a login request succeeds.
     */
    public boolean isRememberPasswordEnabled() {
        return sharedPreferences.getBoolean(PREF_REMEMBER_PASSWORD_ENABLED, false);
    }

    /**
     * Set whether the the username should be saved when a login request succeeds.
     *
     * @param enabled
     *         whether the the username should be saved when a login request succeeds
     */
    public void setRememberPasswordEnabled(boolean enabled) {
        putBoolean(PREF_REMEMBER_PASSWORD_ENABLED, enabled);
    }

    /**
     * String value. The username that was used with the last successful login request. {@code null} when none found. To
     * remove the saved username, use {@link #removeLastUsedUsername()}.
     *
     * @see #getLastUsedUsername()
     * @see #setLastUsedUsername(String)
     * @see #removeLastUsedUsername()
     */
    public static final String PREF_LAST_USED_USERNAME = "pref_last_used_username";

    /**
     * Returns the username that was used with the last successful login request. {@code null} when none found.
     *
     * @return The username that was used with the last successful login request. {@code null} when none found.
     *
     * @see #setLastUsedUsername(String)
     * @see #removeLastUsedUsername()
     */
    public String getLastUsedUsername() {
        return sharedPreferences.getString(PREF_LAST_USED_USERNAME, null);
    }

    /**
     * Saves the username that was used with the last successful login request. {@code null} when none found.
     *
     * @param lastUsedUsername
     *         The username that was used with the last successful login request
     *
     * @see #getLastUsedUsername()
     * @see #removeLastUsedUsername()
     */
    public void setLastUsedUsername(String lastUsedUsername) {
        putString(PREF_LAST_USED_USERNAME, lastUsedUsername);
    }

    /**
     * Removes the {@link #PREF_LAST_USED_USERNAME} from the shared preferences
     *
     * @see #getLastUsedUsername()
     * @see #setLastUsedUsername(String)
     */
    public void removeLastUsedUsername() {
        remove(PREF_LAST_USED_USERNAME);
    }






    /* Convenience methods */

    /**
     * Convenience method that stores a String with the supplied key in {@link #sharedPreferences} object.
     * {@link android.content.SharedPreferences.Editor#commit()} is directly called afterwards.
     *
     * @param key
     *         The name of the preference to modify.
     * @param value
     *         The new value for the preference.
     */
    private void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    /**
     * Convenience method that stores a Integer with the supplied key in {@link #sharedPreferences} object.
     * {@link android.content.SharedPreferences.Editor#commit()} is directly called afterwards.
     *
     * @param key
     *         The name of the preference to modify.
     * @param value
     *         The new value for the preference.
     */
    private void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    /**
     * Convenience method that stores a Long with the supplied key in {@link #sharedPreferences} object.
     * {@link android.content.SharedPreferences.Editor#commit()} is directly called afterwards.
     *
     * @param key
     *         The name of the preference to modify.
     * @param value
     *         The new value for the preference.
     */
    private void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    /**
     * Convenience method that stores a Float with the supplied key in {@link #sharedPreferences} object.
     * {@link android.content.SharedPreferences.Editor#commit()} is directly called afterwards.
     *
     * @param key
     *         The name of the preference to modify.
     * @param value
     *         The new value for the preference.
     */
    private void putFloat(String key, float value) {
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    /**
     * Convenience method that stores a Boolean with the supplied key in {@link #sharedPreferences} object.
     * {@link android.content.SharedPreferences.Editor#commit()} is directly called afterwards.
     *
     * @param key
     *         The name of the preference to modify.
     * @param value
     *         The new value for the preference.
     */
    private void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    /**
     * Convenience method that removes a preference from the {@link #sharedPreferences} object.
     * {@link android.content.SharedPreferences.Editor#commit()} is directly called afterwards.
     *
     * @param key
     *         The name of the preference to remove.
     */
    private void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }
}
