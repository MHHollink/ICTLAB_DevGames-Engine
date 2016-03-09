package baecon.devgames.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import baecon.devgames.DevGamesApplication;
import baecon.devgames.R;
import baecon.devgames.ui.fragment.ProfileFragment;
import baecon.devgames.ui.fragment.ProjectsFragment;
import baecon.devgames.ui.widget.SlidingTabLayout;
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
}
