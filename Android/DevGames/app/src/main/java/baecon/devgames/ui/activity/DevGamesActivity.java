package baecon.devgames.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import baecon.devgames.DevGamesApplication;
import baecon.devgames.R;
import baecon.devgames.events.BusProvider;
import baecon.devgames.util.L;
import baecon.devgames.util.PreferenceManager;

/**
 * The base Activity all other activities extend from.
 */
public abstract class DevGamesActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private ProgressDialog logoutProgressDialog;

    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = PreferenceManager.get(this);

        doLoginCheck();

        logoutProgressDialog = new ProgressDialog(this);
        logoutProgressDialog.setMessage(getString(R.string.logging_out));
        logoutProgressDialog.setIndeterminate(true);
        logoutProgressDialog.setCancelable(false);
    }

    /**
     * Called when Android resumes this Activity.
     */
    @Override
    protected void onResume() {

        super.onResume();

        L.d("* onResume");

        doLoginCheck();

        BusProvider.getBus().register(this);
    }

    /**
     * Called when Android pauses this Activity.
     */
    @Override
    protected void onPause() {

        super.onPause();

        L.d("* onPause");

        BusProvider.getBus().unregister(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int i = item.getItemId();
        if (i == R.id.menu_logout) {
            doLogout();
        }
        else if (i == android.R.id.home) {
            onBackPressed();
        }
        else {
            return false;
        }

        return true;
    }

    private void doLogout() {
        logoutProgressDialog.show();

        preferenceManager.setRememberPasswordEnabled(false);
        preferenceManager.setLastUsedUsername(null);

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    protected void doLoginCheck() {
        if (!isLoggedIn()) {

            // If the user is not logged in, start the login Activity.
            startActivity(new Intent(this, LoginActivity.class));

            // Call finish() and return so that the back button cannot
            // take the user back to this activity.
            finish();
        }
    }

    /**
     * Returns true iff the user is logged in. I.e. if the session
     * id is not empty, or null.
     *
     * @return true iff the user is logged in.
     */
    public boolean isLoggedIn() {
        return getDevGamesApplication().getLoggedInUser() != null;
    }

    protected void displayBackButtonAsNavigationInToolbar() {

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Returns the {@link baecon.devgames.util.PreferenceManager} for this app. This is meant for retrieval and saving
     * small preferences in the {@link android.content.SharedPreferences}. This is initialized in {@link
     * #onCreate(android.os.Bundle)}.
     *
     * @return The {@link baecon.devgames.util.PreferenceManager} for this app
     */
    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    public DevGamesApplication getDevGamesApplication() {
        return (DevGamesApplication) getApplication();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}
