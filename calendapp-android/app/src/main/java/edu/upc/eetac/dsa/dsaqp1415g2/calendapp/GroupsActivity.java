package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CalendappAPI;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.Group;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.GroupCollection;

/**
 * Created by angel on 10/06/15.
 */
public class GroupsActivity extends ListActivity {
    private ArrayList<Group> groupsList;
    private GroupAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_my_groups);

        groupsList = new ArrayList<Group>();
        adapter = new GroupAdapter(this, groupsList);
        setListAdapter(adapter);

        (new FetchGroupsTask()).execute();
    }

    private class FetchGroupsTask extends AsyncTask<Void, Void, GroupCollection>{
        private ProgressDialog pd;

        @Override
        protected GroupCollection doInBackground(Void... params) {
            GroupCollection groups = null;
            try{
                groups = CalendappAPI.getInstance(GroupsActivity.this).getGroups();

            } catch (AppException e) {
                e.printStackTrace();
            }
            return groups;
        }

        @Override
        protected void onPostExecute(GroupCollection result){
            addGroups(result);
            if(pd != null){
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(GroupsActivity.this);
            pd.setTitle("Searching...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }
    }

    private void addGroups(GroupCollection groups){
        groupsList.addAll(groups.getGroups());
        adapter.notifyDataSetChanged();
    }

}
