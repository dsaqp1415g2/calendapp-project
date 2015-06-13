package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CalendappAPI;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.Event;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.User;

/**
 * Created by Jordi on 10/06/2015.
 */
public class CreateEventActivity extends FragmentActivity {

    private final static String TAG = CreateEventActivity.class.getName();
    private static TextView tvDateInitial = null;
    private static TextView tvDateFinish = null;
    private static TextView tvTimeInitial = null;
    private static TextView tvTimeFinish= null;
    private static int userid = 0;
    User user = null;


    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        userid = (int) getIntent().getExtras().get("userid");
        setContentView(R.layout.create_event_layout);
    }
    public void cancel(View v) {
        setResult(RESULT_CANCELED);

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

            case R.id.action_salir:
                SharedPreferences prefs = getSharedPreferences("Calendapp-profile",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit(); //Esto siempre se hace así -> obtener editor + clear
                editor.clear();
                editor.commit();
                Intent intent1 = new Intent(this, LoginActivity.class);
                startActivity(intent1);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }




    public void putEvent(){
        EditText etCreateEventName = (EditText) findViewById(R.id.etCreateEventName);
        // EditText etCreateDateInitial = (EditText) findViewById(R.id.etCreateEventDateInitial);
        // EditText etCreateDateFinish = (EditText) findViewById(R.id.etCreateEventDateFinish);

    }

    public class TimeInitialPickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            tvTimeInitial = (TextView) findViewById(R.id.tvSetTimeInitial);

            tvTimeInitial.setText(new StringBuilder().append(hourOfDay).append(":").append(minute).append(":00"));
        }
    }

    public void setTimeInitial(View v){
        DialogFragment newFragment = new TimeInitialPickerFragment();
        newFragment.show(getFragmentManager().beginTransaction(), "TimeInitialPicker");
    }

    public class TimeFinishPickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            tvTimeFinish = (TextView) findViewById(R.id.tvSetTimeInitial);

            tvTimeFinish.setText(new StringBuilder().append(hourOfDay).append(":").append(minute).append(":00"));
        }
    }

    public void setTimeFinish(View v){
        DialogFragment newFragment = new TimeFinishPickerFragment();
        newFragment.show(getFragmentManager().beginTransaction(), "TimeFinishPicker");
    }

    public class DateInitialPickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            Date date = new Date();
            date.setTime(date.getTime());
            dialog.getDatePicker().setMinDate(date.getTime());
            Date date1 = new Date();
            date1.setTime(date.getTime());
            dialog.getDatePicker().setMaxDate(date1.getTime());

            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            tvDateInitial = (TextView) findViewById(R.id.tvDateInitial);

            tvDateInitial.setText(new StringBuilder()
                    // Month is 0 based, just add 1
                    .append(year).append("-").append(month + 1).append("-")
                    .append(day));
        }
    }

    public void setDateInitial(View v) {
        DialogFragment newFragment = new DateInitialPickerFragment();
        newFragment.show(getFragmentManager().beginTransaction(), "dateInitialPicker");
    }

    public class DateFinishPickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            Date date = new Date();
            date.setTime(date.getTime());
            dialog.getDatePicker().setMinDate(date.getTime());
            Date date1 = new Date();
            date1.setTime(date.getTime());
            dialog.getDatePicker().setMaxDate(date1.getTime());

            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            tvDateFinish = (TextView) findViewById(R.id.tvDateInitial);

            tvDateFinish.setText(new StringBuilder()
                    // Month is 0 based, just add 1
                    .append(year).append("-").append(month + 1).append("-")
                    .append(day));
        }
    }

    public void setDateFinish(View v) {
        DialogFragment newFragment = new DateFinishPickerFragment();
        newFragment.show(getFragmentManager(), "DateFinishPicker");
    }


    public void putEvent(View v){
        EditText etCreateEventName = (EditText) findViewById(R.id.etCreateEventName);
        tvTimeInitial = (TextView) findViewById(R.id.tvSetTimeInitial);
        tvDateInitial = (TextView) findViewById(R.id.tvDateInitial);
        tvTimeFinish = (TextView) findViewById(R.id.tvSetTimeFinish);
        tvDateFinish = (TextView) findViewById(R.id.tvDateFinish);

        String name = etCreateEventName.getText().toString();

        String DateInitial = tvDateInitial.getText().toString() + " " + tvTimeInitial.getText().toString();
        String DateFinish = tvDateFinish.getText().toString() + " " + tvTimeFinish.getText().toString();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
        Date fechaInicial = null;
        Date fechaFinish = null;
        try{
            fechaInicial = format.parse(DateInitial);
            fechaFinish = format.parse(DateFinish);
        }catch (ParseException e) {
            e.printStackTrace();
        }

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;

        if(name.equals("")){
            CharSequence text = "No has escrito el nombre del evento";
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (fechaFinish.before(fechaInicial)) {
            CharSequence text = "La fecha de inicio tiene que ser antes del de final";
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else{
            //(new CreateEventTask()).execute(name, DateInitial, DateFinish);
        }
    }
/*
    private class CreateEventTask extends AsyncTask<String, Void, Event> {
        private ProgressDialog pd;
        @Override
        protected Event doInBackground(String... params){
         Event event = null;
            try{
                String name = params[0];
                String dateInitial = params[1];
                String dateFinish = params[2];
                event = CalendappAPI.getInstance(CreateEventActivity.this).createEventPrivate(name, userid, dateInitial, dateFinish);
            }catch (AppException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return event;
        }
    }*/

}