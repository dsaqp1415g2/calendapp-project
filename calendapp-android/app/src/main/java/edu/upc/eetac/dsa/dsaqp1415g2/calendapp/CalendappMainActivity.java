package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CalendappAPI;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.Event;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.EventCollection;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.User;


public class CalendappMainActivity extends ListActivity {

    private final static String TAG = CalendappMainActivity.class.toString();

    User user = null;

    private EventAdapter adapter;
    private ArrayList<Event> eventsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calend_app_main);

        eventsList = new ArrayList<Event>();
        adapter = new EventAdapter(this, eventsList);
        setListAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("calendapp-profile",
                Context.MODE_PRIVATE);
        final String username = prefs.getString("username", null);
        final String password = prefs.getString("password", null);
        final String urlUser = prefs.getString("urlUser", null);
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        });

        (new FetchEventsTask()).execute(urlUser);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calend_app_main, menu);
        return true;
    }

   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }




    private class FetchEventsTask extends AsyncTask<String, Void, EventCollection> {

        private ProgressDialog pd;

        @Override
        protected EventCollection doInBackground(String... params) {
            EventCollection events = null;
            try {
                user = CalendappAPI.getInstance(CalendappMainActivity.this)
                        .getUser(params[0]);
                Log.d("TAG", user.getLinks().get("my-events").getTarget());
                events = CalendappAPI.getInstance(CalendappMainActivity.this)
                        .getEvents(user.getLinks().get("my-events").getTarget());
            } catch (AppException e) {
                e.printStackTrace();
            }
            if (events == null)
                return null;
            return events;
        }

        @Override
        protected void onPostExecute(EventCollection result) {
            if (result == null){
                pd.setTitle("No tienes eventos privados");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();
            } else
                addEvents(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(CalendappMainActivity.this);
            pd.setTitle("Searching...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }


    }

    private void addEvents(EventCollection events) {
        eventsList.addAll(events.getEvents());
        adapter.notifyDataSetChanged();
    }

}
