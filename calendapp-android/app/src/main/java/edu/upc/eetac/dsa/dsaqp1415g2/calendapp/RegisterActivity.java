package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
 * Created by Jordi on 10/06/2015.
 */
public class CreateUserActivity extends Activity {
    private final static String TAG = CreateUserActivity.class.getName();

    private class createUserTask extends AsyncTask<String, Void, User> {
        private ProgressDialog pd;

        @Override
        protected User doInBackground(String... params) {
            User user = new User();
            try {
                user = CalendappAPI.getInstance(CreateUserActivity.this).createUser(params[0], params[1], Integer.parseInt(params[2]), params[3], params[4]);
            } catch (AppException e) {
                e.printStackTrace();
            }
            return user;
        }

        @Override
        protected void onPostExecute(User result) {
            if (result.getPoints() == -1){
                Context context = getApplicationContext();
                CharSequence text = "Ese username ya existe";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                Context context = getApplicationContext();
                CharSequence text = "Usuario creado correcatmente\nPuedes iniciar sesión";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                finish();
            }
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(CreateUserActivity.this);
            pd.setTitle("Buscando...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        String ageS = etAge.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String password2 = etPassword2.getText().toString();

        if ((username.equals("")) || (name.equals("")) || (email.equals("")) || (ageS.equals("")) || (password.equals("")) || (password2.equals(""))) {
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
        } else if (username.length() > 20){
            Context context = getApplicationContext();
            CharSequence text = "El username es demasiado largo";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            (new createUserTask()).execute(username, name, ageS, email, password);
        }
    }
}