/*package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CalendappAPI;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.User;

/**
 * Created by Jordi on 10/06/2015.

public class CreateEventActivity extends FragmentActivity {
    private final static String TAG = CreateEventActivity.class.getName();
    private static TextView tvDate = null;
    private static TextView tvTime = null;
    private static TextView tvDateClosing = null;
    private static TextView tvTimeClosing = null;
    User user = null;

    private class CreateGroupTask extends AsyncTask<String, Void, Group> {
        private ProgressDialog pd;

        @Override
        protected Group doInBackground(String... params) {
            Group group = null;
            try {
                String groupname = params[0];
                int price = Integer.parseInt(params[1]);

                String date = params[2];
                String dateClosing = params[3];

                user = CalendappAPI.getInstance(CreateEventActivity.this).getUser(params[4]);


                   Context context = getApplicationContext();
                    int duration = Toast.LENGTH_LONG;
                    CharSequence text = "El grupo cuesta más de lo que tienes";
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                    group.setPrice(price);
                }
            } catch (AppException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return group;
        }

        @Override
        protected void onPostExecute(Group result) {
                showGroups(result);

            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(CreateEventActivity.this);

            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_layout);
    }

    public class DatePickerFragment extends DialogFragment
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
            date.setTime(date.getTime() - 1000);
            dialog.getDatePicker().setMinDate(date.getTime());
            Date date1 = new Date();
            date1.setTime(date.getTime() + 172800000);
            dialog.getDatePicker().setMaxDate(date1.getTime());

            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            tvDate = (TextView) findViewById(R.id.tvCreateGroupDate);

            tvDate.setText(new StringBuilder()
                    // Month is 0 based, just add 1
                    .append(year).append("-").append(month + 1).append("-")
                    .append(day));
        }
    }

    public void setDate(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public class TimePickerFragment extends DialogFragment
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
            tvTime = (TextView) findViewById(R.id.tvCreateGroupTime);

            tvTime.setText(new StringBuilder().append(hourOfDay).append(":").append(minute).append(":00"));
        }
    }

    public void setTime(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public class DateClosingPickerFragment extends DialogFragment
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
            date.setTime(date.getTime() - 1000);
            dialog.getDatePicker().setMinDate(date.getTime());
            Date date1 = new Date();
            date1.setTime(date.getTime() + 604800000);
            dialog.getDatePicker().setMaxDate(date1.getTime());

            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            tvDate = (TextView) findViewById(R.id.tvCreateGroupDateClosing);

            tvDate.setText(new StringBuilder()
                    // Month is 0 based, just add 1
                    .append(year).append("-").append(month + 1).append("-")
                    .append(day));
        }
    }

    public void setDateClosing(View v) {
        DialogFragment newFragment = new DateClosingPickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public class TimeClosingPickerFragment extends DialogFragment
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
            tvTime = (TextView) findViewById(R.id.tvCreateGroupTimeClosing);

            tvTime.setText(new StringBuilder().append(hourOfDay).append(":").append(minute).append(":00"));
        }
    }

    public void setTimeClosing(View v) {
        DialogFragment newFragment = new TimeClosingPickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void cancel(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void post(View v) {
        EditText etGroupname = (EditText) findViewById(R.id.etCreateGroupGroupname);
        EditText etPrice = (EditText) findViewById(R.id.etCreateGroupPrice);
        tvDate = (TextView) findViewById(R.id.tvCreateGroupDate);
        tvTime = (TextView) findViewById(R.id.tvCreateGroupTime);
        tvDateClosing = (TextView) findViewById(R.id.tvCreateGroupDateClosing);
        tvTimeClosing = (TextView) findViewById(R.id.tvCreateGroupTimeClosing);

        String groupname = etGroupname.getText().toString();
        String price = etPrice.getText().toString();
        String endingTimestamp = tvDate.getText().toString() + " " + tvTime.getText().toString();
        String closingTimestamp = tvDateClosing.getText().toString() + " " + tvTimeClosing.getText().toString();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
        Date fechaEnding = null;
        Date fechaClosing = null;
        Date ahora = null;
        Calendar cal = Calendar.getInstance();
        try {
            fechaEnding = format.parse(endingTimestamp);
            fechaClosing = format.parse(closingTimestamp);
            ahora = cal.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;

        if (groupname.equals("")) {
            CharSequence text = "No has escrito el nombre del grupo";
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (price.equals("")) {
            CharSequence text = "No has escrito el precio de entrada";
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (fechaClosing.before(fechaEnding)) {
            CharSequence text = "La fecha de cierre debe ser posterior a la de fin";
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (fechaEnding.before(ahora)) {
            CharSequence text = "Las fechas deben ser posteriores a este momento";
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            String userURL = (String) getIntent().getExtras().get("user");
            (new CreateGroupTask()).execute(groupname, price, endingTimestamp, closingTimestamp, userURL);
        }
    }

    private void showGroups(Group result) {
        String json = new Gson().toJson(result);
        Bundle data = new Bundle();
        data.putString("json-group", json);
        Intent intent = new Intent();
        intent.putExtras(data);
        setResult(RESULT_OK, intent);
        finish();
    }
*/
