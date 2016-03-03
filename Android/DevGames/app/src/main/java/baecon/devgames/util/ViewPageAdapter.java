package baecon.devgames.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import baecon.devgames.ui.fragment.TabFragment;


public class ViewPageAdapter extends FragmentPagerAdapter {

    List<TabFragment> tabs;

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
        return tabs.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }

    public void addTab(TabFragment tab) {
        if(tabs == null) tabs = new ArrayList<>();
        tabs.add(tab);
    }
}
