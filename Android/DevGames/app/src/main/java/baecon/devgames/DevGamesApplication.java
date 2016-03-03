package baecon.devgames;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;

import baecon.devgames.model.User;
import baecon.devgames.util.PreferenceManager;

/**
 * The App's context holding some data like the logged in user
 */
public class DevGamesApplication extends Application {

    private PreferenceManager preferenceManager;
    private User loggedInUser;

    @Override
    public void onCreate() {
        super.onCreate();

        preferenceManager = PreferenceManager.get(this);
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    public static DevGamesApplication get(Context context) {
        return (DevGamesApplication) context.getApplicationContext();
    }

    public static DevGamesApplication get(Fragment context) {
        return (DevGamesApplication) context.getActivity().getApplicationContext();
    }
}
