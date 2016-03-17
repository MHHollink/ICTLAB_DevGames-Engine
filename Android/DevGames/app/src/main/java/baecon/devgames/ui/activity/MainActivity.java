package baecon.devgames.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.squareup.otto.Subscribe;

import baecon.devgames.DevGamesApplication;
import baecon.devgames.R;
import baecon.devgames.connection.task.GcmRegistrationTask;
import baecon.devgames.events.LogoutEvent;
import baecon.devgames.ui.fragment.ProfileFragment;
import baecon.devgames.ui.fragment.ProjectsFragment;
import baecon.devgames.ui.widget.SlidingTabLayout;
import baecon.devgames.util.L;
import baecon.devgames.util.Utils;
import baecon.devgames.util.ViewPageAdapter;

public class MainActivity extends DevGamesActivity {

    /**
     * The class responsible for transitions between tab-fragments.
     */
    private SlidingTabLayout indicator = null;
    private ViewPager adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);


        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        viewPageAdapter.addTab(
                new ProfileFragment().setTitle(DevGamesApplication.get(this).getString(R.string.profile)),
                new ProjectsFragment().setTitle(DevGamesApplication.get(this).getString(R.string.project))
        );


        adapter = (ViewPager) findViewById(R.id.activity_main_viewpager);
        adapter.setAdapter(viewPageAdapter);

        indicator = (SlidingTabLayout) findViewById(R.id.tabs);

        indicator.setSelectedIndicatorColors(getResources().getColor(R.color.cornflower_light));
        indicator.setDistributeEvenly(true);
        indicator.setViewPager(adapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        adapter = null;
        indicator = null;

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Utils.playServicesAvailable(this)) {

            // Check registration id
            new GcmRegistrationTask(this).executeThreaded();

        }
        else {

            L.w("Google Play Services is not available on this device! Showing dialog = {0}",
                    getPreferenceManager().getShowPlayServicesDialog());

            // Don't display the dialog again if the user already dismissed it earlier
            if (!getPreferenceManager().getShowPlayServicesDialog()) {
                return;
            }

            final int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MainActivity.this);

            if (GooglePlayServicesUtil.isUserRecoverableError(result)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.common_google_play_services_unsupported_title)
                        .setMessage(R.string.play_services_user_recoverable)
                        .setPositiveButton(R.string.play_services_install, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                GooglePlayServicesUtil.getErrorDialog(result, MainActivity.this, 0);

                                // Hide this dialog next time
                                getPreferenceManager().setShowPlayServicesDialog(false);

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.play_services_continue_without_installing, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Hide this dialog next time
                                getPreferenceManager().setShowPlayServicesDialog(false);

                                dialog.dismiss();
                            }
                        })
                        .show();
            }
            else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.common_google_play_services_unsupported_title)
                        .setMessage(R.string.play_services_non_user_recoverable)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Hide this dialog next time
                                getPreferenceManager().setShowPlayServicesDialog(false);

                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        }
    }

    @Subscribe
    @Override
    public void onLogoutEvent(LogoutEvent event) {
        super.onLogoutEvent(event);
    }
}
