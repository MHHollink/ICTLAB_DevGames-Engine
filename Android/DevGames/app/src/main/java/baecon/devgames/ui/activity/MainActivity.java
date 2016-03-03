package baecon.devgames.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.Stack;

import baecon.devgames.DevGamesApplication;
import baecon.devgames.R;
import baecon.devgames.ui.fragment.ProfileFragment;
import baecon.devgames.ui.widget.SlidingTabLayout;
import baecon.devgames.util.ViewPageAdapter;

public class MainActivity extends DevGamesActivity {

    /**
     * The class responsible for transitions between tab-fragments.
     */
    private SlidingTabLayout tabStrip = null;
    private ViewPager pager;

    private ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {

            previousFragmentPositions.push(position);

            supportInvalidateOptionsMenu();
        }
    };

    /**
     * A stack keeping track of the position indexes of the tab-fragments.
     */
    private Stack<Integer> previousFragmentPositions = new Stack<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);


        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        viewPageAdapter.addTab(ProfileFragment.getInstance(this));





        pager = (ViewPager) findViewById(R.id.activity_main_viewpager);
        pager.setAdapter(viewPageAdapter);

        tabStrip = (SlidingTabLayout) findViewById(R.id.tabs);
        tabStrip.setViewPager(pager);


    }
}
