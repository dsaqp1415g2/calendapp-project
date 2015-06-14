package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

/**
 * Created by Jordi on 13/06/2015.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CalendappAPI;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.User;


public class UpdateUserActivity extends Activity implements View.OnClickListener {
    private final static String TAG = UpdateUserActivity.class.getName();

    private class updateUserTask extends AsyncTask<String, Void, User> {
        private ProgressDialog pd;

        @Override
        protected User doInBackground(String... params) {
            User user = new User();
            try {
                int age;
                String name;
                String email;
                if (params[1].equals(""))
                    age = 0;
                else
                    age = Integer.parseInt(params[1]);

                if (params[0].equals(""))
                    name = null;
                else
                    name = params[0];

                if (params[2].equals(""))
                    email = null;
                else
                    email = params[2];

                user = CalendappAPI.getInstance(UpdateUserActivity.this).updateUser(name, age, email);
            } catch (AppException e) {
                e.printStackTrace();
            }
            return user;
        }

        @Override
        protected void onPostExecute(User result) {
            finish();
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(UpdateUserActivity.this);
            pd.setTitle("Buscando...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
            Context context = getApplicationContext();
            CharSequence text = "Tus datos han sido actualizados";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }
    public void updateUser(View v) {
        EditText etName = (EditText) findViewById(R.id.etUpdateUserName);
        EditText etAge = (EditText) findViewById(R.id.etUpdateUserAge);
        EditText etEmail = (EditText) findViewById(R.id.etUpdateUserEmail);

        String name = etName.getText().toString();
        String ageS = etAge.getText().toString();
        String email = etEmail.getText().toString();

        (new updateUserTask()).execute(name, ageS, email);
        Context context = getApplicationContext();
        CharSequence text = "Tus datos han sido actualizados";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
}

   Button btn;
    ImageView img;
    Intent i;
    Bitmap bmp;
    final static int cons = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_user_layout);
        init();
    }

    public void init() {
        btn = (Button) findViewById(R.id.btnCaptura);
        btn.setOnClickListener(this);
        img = (ImageView) findViewById(R.id.imagen);
    }

    @Override
    public void onClick(View v) {
        int id;
        id = v.getId();
        switch (id) {
            case R.id.btnCaptura:
                i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, cons);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bundle ext = data.getExtras();
            bmp = (Bitmap) ext.get("data");
            img.setImageBitmap(bmp);

        }
    }
}
