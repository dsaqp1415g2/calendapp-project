package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CalendappAPI;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.Group;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.GroupCollection;

/**
 * Created by angel on 10/06/15.
 */
public class GroupsActivity extends ListActivity {
    private final static String TAG = GroupsActivity.class.toString();


    private ArrayList<Group> groupsList;
    private GroupAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_my_groups);
        String urlMyGroups = (String) getIntent().getExtras().get("urlmygroups");
        groupsList = new ArrayList<Group>();
        adapter = new GroupAdapter(this, groupsList);
        setListAdapter(adapter);

        (new FetchGroupsTask()).execute(urlMyGroups);
    }

    private class FetchGroupsTask extends AsyncTask<String, Void, GroupCollection>{
        private ProgressDialog pd;

        @Override
        protected GroupCollection doInBackground(String... params) {
            GroupCollection groups = null;
            try{
                groups = CalendappAPI.getInstance(GroupsActivity.this).getGroups(params[0]);

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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){
        Group group = groupsList.get(position);
        Log.d(TAG, group.getLinks().get("self").getTarget());
        Log.d(TAG, group.getLinks().get("events-of-group").getTarget());

        Intent intent = new Intent(this, GroupDetailActivity.class);
        intent.putExtra("urlGroup", group.getLinks().get("self").getTarget());
        intent.putExtra("urlEvents", group.getLinks().get("events-of-group").getTarget());
        startActivity(intent);
    }

}
