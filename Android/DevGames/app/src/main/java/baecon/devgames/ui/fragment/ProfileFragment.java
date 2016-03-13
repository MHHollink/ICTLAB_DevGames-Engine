package baecon.devgames.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import baecon.devgames.DevGamesApplication;
import baecon.devgames.R;
import baecon.devgames.model.User;

public class ProfileFragment extends DevGamesFragment implements DevGamesTab {

    private String title = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        User user = DevGamesApplication.get(getActivity()).getLoggedInUser();

        TextView nameView = (TextView) view.findViewById(R.id.txt_name);
        nameView.setText(user.getUsername());

        TextView scoreView = (TextView) view.findViewById(R.id.txt_score);
        scoreView.setText(String.valueOf(user.getScore()));

        TextView projectsView = (TextView) view.findViewById(R.id.txt_projects);
        projectsView.setText(String.valueOf(user.getProjects().size()));

        TextView commitsView = (TextView) view.findViewById(R.id.txt_commits);
        commitsView.setText(String.valueOf(user.getCommits().size()));

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
