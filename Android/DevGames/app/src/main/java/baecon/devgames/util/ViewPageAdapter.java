package baecon.devgames.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import baecon.devgames.ui.fragment.DevGamesTab;


public class ViewPageAdapter extends FragmentPagerAdapter {

    List<DevGamesTab> tabs;

    public ViewPageAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);

        tabs = new ArrayList<>();
    }

    /**
     * Gets the count of fragments used in this ViewPager
     *
     * @return Integer representation of the amount of fragments in this viewPager
     */
    @Override
    public int getCount() {
        return tabs.size();
    }

    /**
     * Gets the fragment in the ViewPager on the given position
     *
     * @param position integer representation of position you want to get
     * @return Fragment on the given position
     */
    @Override
    public Fragment getItem(int position) {
        return tabs.get(position).getFragmentFromTab();
    }

    /**
     * Gets the title of the Fragment on the given position
     *
     * @param position integer representation of position you want to get
     * @return Title of the fragment on the given position
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }

    /**
     * addTab is used to add a fragment (or multiple) to the current viewpager
     * @param tab an array of tabs that should be added to the ViewPager
     */
    public void addTab(DevGamesTab... tab) {
        if(tabs == null) tabs = new ArrayList<>();
        Collections.addAll(tabs, tab);
    }
}
