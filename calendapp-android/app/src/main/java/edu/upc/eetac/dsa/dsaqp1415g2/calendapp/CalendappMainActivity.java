package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.Event;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.User;


public class CalendappMainActivity extends ActionBarActivity {

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
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password
                        .toCharArray());
            }
        });
        (new FetchStingsTask()).execute();

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
}
