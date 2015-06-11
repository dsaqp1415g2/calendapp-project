package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
 * Created by Jordi on 10/06/2015.
 */
public class RegisterActivity extends Activity {
    private final static String TAG = RegisterActivity.class.getName();

    User user;

/*    private void startCalendappActivity(String password) {
        Intent intent = new Intent(this, CalendappMainActivity.class);
        SharedPreferences prefs = getSharedPreferences("calendapp-profile",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putString("username", user.getName());
        editor.putString("password", password);
        editor.putString("urlUser", user.getLinks().get("self").getTarget());
        startActivity(intent);

    }*/
    private class createUserTask extends AsyncTask<String, Void, User> {
        private ProgressDialog pd;

        private String pass;
        @Override
        protected User doInBackground(String... params) {
            user = new User();
            try {
                int age = Integer.valueOf(params[2]);
                user = CalendappAPI.getInstance(RegisterActivity.this).createUser(params[0], params[1], age, params[3], params[4]);
            } catch (AppException e) {
                e.printStackTrace();
            }
            pass = params[4];
            return user;
        }

        @Override
        protected void onPostExecute(User result) {

                Context context = getApplicationContext();
                CharSequence text = "Usuario creado correcatmente\nPuedes iniciar sesión";
                int duration = Toast.LENGTH_SHORT;


             //   Toast toast = Toast.makeText(context, text, duration);
               //6 toast.setGravity(Gravity.CENTER, 0, 0);
                //toast.show();
                //finish();
            //startCalendappActivity(pass);

            if (pd != null) {
                pd.dismiss();
            }
        }



        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(RegisterActivity.this);
            pd.setTitle("Buscando...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_register);
    }

    public void createUser(View v) {
        EditText etUsername = (EditText) findViewById(R.id.etCreateUserUsername);
        EditText etName = (EditText) findViewById(R.id.etCreateUserName);
        EditText etAge = (EditText) findViewById(R.id.etCreateUserAge);
        EditText etEmail = (EditText) findViewById(R.id.etCreateUserEmail);
        EditText etPassword = (EditText) findViewById(R.id.etCreateUserPassword);
        EditText etPassword2 = (EditText) findViewById(R.id.etCreateUserPassword2);

        String username = etUsername.getText().toString();
        String name = etName.getText().toString();
        String ages = etAge.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String password2 = etPassword2.getText().toString();

        if ((username.equals("")) || (name.equals("")) || (email.equals("")) || (ages.equals("")) || (password.equals("")) || (password2.equals(""))) {
            Context context = getApplicationContext();
            CharSequence text = "Todos los campos son obligatorios";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (!password.equals(password2)){
            Context context = getApplicationContext();
            CharSequence text = "Las contraseñas no coinciden";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (username.length() > 30){
            Context context = getApplicationContext();
            CharSequence text = "El username es demasiado largo";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            (new createUserTask()).execute(username, name, ages, email, password);
            Context context = getApplicationContext();
            CharSequence text = "Registrado!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}