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

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position).getFragmentFromTab();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }

    public void addTab(DevGamesTab... tab) {
        if(tabs == null) tabs = new ArrayList<>();
        Collections.addAll(tabs, tab);
    }
}
