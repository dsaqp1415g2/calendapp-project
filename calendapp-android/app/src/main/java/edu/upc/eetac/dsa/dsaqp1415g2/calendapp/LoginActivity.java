package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

/**
 * Created by Jordi on 29/05/2015.
 */
public class LoginActivity extends Activity {
    private final static String TAG = LoginActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        SharedPreferences prefs = getSharedPreferences("calendapp-profile",
                Context.MODE_PRIVATE);
        String username = prefs.getString("username", null);
        String password = prefs.getString("password", null);
         //Uncomment the next two lines to test the application without login
       //  each time
      //   username = "jordi";
        // password = "jordi";
        if ((username != null) && (password != null)) {
            Intent intent = new Intent(this, CalendappMainActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.login_layout);
    }

    public void signIn(View v) {
        EditText etUsername = (EditText) findViewById(R.id.etUsername);
        EditText etPassword = (EditText) findViewById(R.id.etPassword);

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        // Launch a background task to check if credentials are correct
        // If correct, store username and password and start Beeter activity
        // else, handle error

        // I'll suppose that u/p are correct:
        SharedPreferences prefs = getSharedPreferences("calendapp-profile",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putString("username", username);
        editor.putString("password", password);
        boolean done = editor.commit();
        if (done)
            Log.d(TAG, "preferences set");
        else
            Log.d(TAG, "preferences not set. THIS A SEVERE PROBLEM");

        startCalendappActivity();

         }
    private void startCalendappActivity() {
        Intent intent = new Intent(this, CalendappMainActivity.class);
        startActivity(intent);
        finish();
    }
}
