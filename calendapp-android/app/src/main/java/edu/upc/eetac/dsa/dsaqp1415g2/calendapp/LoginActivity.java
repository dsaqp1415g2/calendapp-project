package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

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
}
