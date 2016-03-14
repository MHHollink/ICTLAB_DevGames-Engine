package baecon.devgames.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import baecon.devgames.DevGamesApplication;
import baecon.devgames.R;
import baecon.devgames.model.Project;

public class ProjectsFragment extends DevGamesFragment implements DevGamesTab{

    private String title = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.projects_fragment, container, false);

        List<Project> projects = new ArrayList<>();

        projects.addAll(
                DevGamesApplication.get(this)
                        .getLoggedInUser()
                        .getProjects()
                        .values()
        );

        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(new ProjectsListAdapter(projects));

        return view;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public ProjectsFragment setTitle(String s) {
        title = s;
        return this;
    }

    @Override
    public Fragment getFragmentFromTab() {
        return getFragment();
    }


    protected class ProjectsListAdapter extends BaseAdapter {


        private LayoutInflater inflater = null;

        private List projects;
        private Project tempValues = null;

        public ProjectsListAdapter(List<Project> projects) {

            setProjects(projects);

            inflater = ( LayoutInflater ) getActivity().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        public void setProjects(List<Project> projects) {

            this.projects = projects;
        }

        public int getCount() {
            if(projects == null || projects.size()<=0)
                return 1;
            return projects.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        /********* Create a holder Class to contain inflated xml file elements *********/
        private class ViewHolder{

            public TextView name;
            public TextView score;
            public TextView description;
            public TextView developers;
            public TextView commits;
        }

        /****** Depends upon data size called for each row , Create each ListView row *****/
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            ViewHolder holder;

            if( convertView == null){

                view = inflater.inflate(R.layout.project_listview_item, null);

                holder = new ViewHolder();
                holder.name    = (TextView) view.findViewById(R.id.project_name);
                holder.score = (TextView) view.findViewById(R.id.project_score);
                holder.description = (TextView) view.findViewById(R.id.project_description);
                holder.developers = (TextView) view.findViewById(R.id.project_dev_count);
                holder.commits = (TextView) view.findViewById(R.id.project_commit_count);

                view.setTag(holder);
            }
            else {
                holder = (ViewHolder) view.getTag();
            }

            if(projects == null || projects.size()<=0)
            {
                holder.score.setText("No Data");
            }
            else
            {
                tempValues = null;
                tempValues = (Project) projects.get(position);

                holder.name.setText(tempValues.getName());
                holder.description.setText(tempValues.getDescription());
                holder.developers.setText(String.valueOf(tempValues.getDevelopers().size()));
                holder.commits.setText(String.valueOf(tempValues.getCommits().size()));
                holder.score.setText(String.valueOf(0));
            }
            return view;
        }
    }
}
