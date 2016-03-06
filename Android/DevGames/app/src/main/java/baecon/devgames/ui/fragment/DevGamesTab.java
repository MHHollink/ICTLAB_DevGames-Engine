package baecon.devgames.ui.fragment;

import android.support.v4.app.Fragment;

public interface DevGamesTab{
    Fragment getFragmentFromTab();
    String getTitle();
    DevGamesTab setTitle(String s);
}
