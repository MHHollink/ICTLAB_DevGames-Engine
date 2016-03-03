package baecon.devgames.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import baecon.devgames.R;

public class ProfileFragment extends TabFragment {

    public static ProfileFragment getInstance(Context context) {
        ProfileFragment fragment = new ProfileFragment();
        title = context.getString(R.string.profile);

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
