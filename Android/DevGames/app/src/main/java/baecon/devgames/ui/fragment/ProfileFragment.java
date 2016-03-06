package baecon.devgames.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import baecon.devgames.DevGamesApplication;
import baecon.devgames.R;

public class ProfileFragment extends DevGamesFragment implements DevGamesTab {

    private String title = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);


        return view;
    }

    @Override
    public Fragment getFragmentFromTab() {
        return getFragment();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public ProfileFragment setTitle(String s) {
        title = s;
        return this;
    }
}
