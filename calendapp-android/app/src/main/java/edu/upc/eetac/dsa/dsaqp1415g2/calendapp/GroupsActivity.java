package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CalendappAPI;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.Group;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.GroupCollection;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.User;

/**
 * Created by angel on 10/06/15.
 */
public class GroupsActivity extends ListActivity {
    private final static String TAG = GroupsActivity.class.toString();

    User user = null;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calend_app_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_my_groups:
                Intent intent = new Intent(this, GroupsActivity.class);
                intent.putExtra("urlmygroups", user.getLinks().get("my-groups").getTarget());
                startActivity(intent);
                return true;
            case R.id.action_create_event:
                Intent intent_create = new Intent(this, CreateEventActivity.class);
                intent_create.putExtra("userid", user.getUserid());
                startActivity(intent_create);
                finish();
                return true;

            case R.id.action_salir:
                SharedPreferences prefs = getSharedPreferences("Calendapp-profile",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit(); //Esto siempre se hace así -> obtener editor + clear
                editor.clear();
                editor.commit();
                Intent intent1 = new Intent(this, LoginActivity.class);
                startActivity(intent1);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
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
