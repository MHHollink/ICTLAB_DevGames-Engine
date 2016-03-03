package baecon.devgames.ui.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by Marcel on 03-3-2016.
 */
public abstract class TabFragment extends Fragment {
    static protected String title;

    public String getTitle() {
        return title;
    }
}
