package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CalendappAPI;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.Comment;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CommentCollection;

/**
 * Created by Jordi on 14/06/2015.
 */
public class CommentsDetailActivity extends ListActivity {
    private final static String TAG = CommentsDetailActivity.class.getName();

    private CommentAdapter adapter;
    private ArrayList<Comment> commentsList;


    private class FetchCommentTask extends AsyncTask<String, Void, CommentCollection> {
        private ProgressDialog pd;

        @Override
        protected CommentCollection doInBackground(String... params) {
            CommentCollection comments = null;
            try {
                comments = CalendappAPI.getInstance(CommentsDetailActivity.this)
                        .getComments(params[0]);
            } catch (AppException e) {
                Log.d(TAG, e.getMessage(), e);
            }
            return comments;
        }

        @Override
        protected void onPostExecute(CommentCollection result) {
            loadComments(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(CommentsDetailActivity.this);
            pd.setTitle("Loading...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_of_event_detail);

        commentsList = new ArrayList<Comment>();
        adapter = new CommentAdapter(this, commentsList);
        setListAdapter(adapter);

        String urlComments = (String) getIntent().getExtras().get("url");
        (new FetchCommentTask()).execute(urlComments);
    }

    private void loadComments(CommentCollection comments) {
        commentsList.addAll(comments.getComments());
        adapter.notifyDataSetChanged();
    }

}