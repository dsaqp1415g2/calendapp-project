package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CalendappAPI;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.Comment;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.Event;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.EventCollection;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.Group;

/**
 * Created by angel on 11/06/15.
 */
public class GroupDetailActivity extends ListActivity {
    private final static String TAG = GroupDetailActivity.class.toString();


    private ArrayList<Event> eventsList;
    private EventsAdapter adapter;
    private static String urlEvents;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_detail_layout);

        eventsList = new ArrayList<Event>();
        adapter = new EventsAdapter(this, eventsList);
        setListAdapter(adapter);
        String urlGroup = (String) getIntent().getExtras().get("urlGroup");
        urlEvents = (String) getIntent().getExtras().get("urlEvents");

        (new FetchEventsTask()).execute(urlEvents);
        (new FetchGroupTask()).execute(urlGroup);
    }

    private void loadGroup(Group group){
        TextView tvTextName = (TextView) findViewById(R.id.tvTextName);
        TextView tvTextAdmin = (TextView) findViewById(R.id.tvTextAdmin);
        TextView tvTextDescription = (TextView) findViewById(R.id.tvTextDescription);

        tvTextName.setText(group.getName());
        tvTextAdmin.setText(group.getAdmin());
        tvTextDescription.setText(group.getDescription());
    }

    private class FetchGroupTask extends AsyncTask<String, Void, Group> {
        private ProgressDialog pd;

        @Override
        protected Group doInBackground(String... params){
            Group group = null;
            try{
                group = CalendappAPI.getInstance(GroupDetailActivity.this).getGroup(params[0]);
            }catch (AppException e) {
                Log.d(TAG, e.getMessage(), e);
            }
            return group;
        }

        @Override
        protected void onPostExecute(Group result){
            loadGroup(result);

            if(pd != null){
                pd.dismiss();
            }
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(GroupDetailActivity.this);
            pd.setTitle("Loading...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();

        }
    }


    private class FetchEventsTask extends AsyncTask<String, Void, EventCollection> {

        private ProgressDialog pd;

        @Override
        protected EventCollection doInBackground(String... params) {
            EventCollection events = null;
            try {
                events = CalendappAPI.getInstance(GroupDetailActivity.this)
                        .getEvents(params[0]);
            } catch (AppException e) {
                e.printStackTrace();
            }
            if (events == null)
                return null;
            return events;
        }

        @Override
        protected void onPostExecute(EventCollection result) {
            addEvents(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(GroupDetailActivity.this);
            pd.setTitle("Searching...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        Event event = eventsList.get(position);

        Intent intent = new Intent(this, CommentsDetailActivity.class);
        intent.putExtra("url", event.getLinks().get("comments").getTarget());
        startActivity(intent);
    }


    private void addEvents(EventCollection events) {
        eventsList.addAll(events.getEvents());
        adapter.notifyDataSetChanged();
    }



}

