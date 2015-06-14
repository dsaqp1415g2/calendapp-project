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


            case R.id.action_updateUser:
                Intent intent_updateUser = new Intent(this, UpdateUserActivity.class);
                startActivity(intent_updateUser);
                return true;

            case R.id.action_salir:
                SharedPreferences prefs = getSharedPreferences("Calendapp-profile",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit(); //Esto siempre se hace así -> obtener editor + clear
                editor.remove("username");
                editor.remove("password");
                editor.clear();
                editor.commit();
                setContentView(R.layout.login_layout);
//                Intent intent1 = new Intent(this, LoginActivity.class);
//                startActivity(intent1);
//                finish();
                return true;


                        }
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Event event = eventsList.get(position);
        Log.d(TAG, event.getLinks().get("self-edit").getTarget());
        String data = event.getLinks().get("self-edit").getTarget();

        Intent intent = new Intent(this, EventPrivateDetailActiviy.class);
        intent.putExtra("url", event.getLinks().get("self-edit").getTarget());
        startActivity(intent);
    }
}
