package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CalendappAPI;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.User;

/**
 * Created by Jordi on 29/05/2015.
 */

public class LoginActivity extends Activity {
    private final static String TAG = LoginActivity.class.getName();

User user = null;

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

    public void signIn(View v) throws AppException {


        EditText etUsername = (EditText) findViewById(R.id.etUsername); // Obtener campos de texto de usuario y contraseña
        EditText etPassword = (EditText) findViewById(R.id.etPassword);

        //final String username = etUsername.getText().toString();
        //final String password = etPassword.getText().toString();
        final String username = "angel";
        final String password = "angel";
        // Se debería acceder a la API y comprobar que las credenciales son correctas

        Log.d("mytag", "username: " + username + " y pass: " + password);
        (new checkLoginTask()).execute(username, password);

    }


    private void evaluateLogin(Boolean loginOK) {
        if (loginOK) {
            EditText etUsername = (EditText) findViewById(R.id.etUsername);
            EditText etPassword = (EditText) findViewById(R.id.etPassword);

            final String username = etUsername.getText().toString();
            final String password = etPassword.getText().toString();

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
        } else {
            Context context = getApplicationContext();
            CharSequence text = "El usuario o la contraseña son incorrectos";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);

            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void startCalendappActivity() {
        Intent intent = new Intent(this, CalendappMainActivity.class);
        startActivity(intent);
        finish();
    }


    private class checkLoginTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog pd;

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean correctLogin = false;
            try {
                user = CalendappAPI.getInstance(LoginActivity.this)
                            .checkLogin(params[0], params[1]);
                    correctLogin = user.isLoginSuccesfull();
                } catch (AppException e) {
                    e.printStackTrace();
                }
                return correctLogin;
            }
        @Override
        protected void onPostExecute(Boolean loginOK) {
            evaluateLogin(loginOK);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(LoginActivity.this);
            pd.setTitle("Buscando...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }
    }

}
