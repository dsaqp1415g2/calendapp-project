package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CalendappAPI;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.Event;

/**
 * Created by angel on 9/06/15.
 */
public class EventPrivateDetailActiviy extends Activity {
    private final static String TAG = EventPrivateDetailActiviy.class.getName();

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_detail_layout);
        String urlEvent = (String) getIntent().getExtras().get("url");
        (new FetchEventTask()).execute(urlEvent);
    }

    private class FetchEventTask extends AsyncTask<String, Void, Event> {
        private ProgressDialog pd;

        @Override
        protected Event doInBackground(String... params){
            Event event = null;
            try{
                event = CalendappAPI.getInstance(EventPrivateDetailActiviy.this).getEvent(params[0]);
            }catch (AppException e) {
                Log.d(TAG, e.getMessage(), e);
            }
            return event;
        }

        @Override
        protected void onPostExecute(Event result) {
            loadEvent(result);
            if (pd != null) {
                pd.dismiss();
            }
        }
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(EventPrivateDetailActiviy.this);
            pd.setTitle("Loading...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }

    public void cancel(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }
    private final static int WRITE_ACTIVITY = 0;


    private void loadEvent(Event event) {
        TextView tvNameEvent = (TextView) findViewById(R.id.tvNameEvent);
        TextView tvDateInitial = (TextView) findViewById(R.id.tvDateInitial);
        TextView tvDateFinish = (TextView) findViewById(R.id.tvDateFinish);

        tvNameEvent.setText(event.getName());
        tvDateInitial.setText(SimpleDateFormat.getInstance().format(event.getDateInitial()));
        tvDateFinish.setText(SimpleDateFormat.getInstance().format(event.getDateFinish()));
    }
}
