package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.AppException;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.CalendappAPI;
import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.Comment;

/**
 * Created by Jordi on 14/06/2015.
 */
public class CommentDetailActivity extends Activity {
    private final static String TAG = CommentDetailActivity.class.getName();

    private class FetchCommentTask extends AsyncTask<String, Void, Comment> {
        private ProgressDialog pd;

        @Override
        protected Comment doInBackground(String... params) {
            Comment comment = null;
            try {
                comment = CalendappAPI.getInstance(CommentDetailActivity.this)
                        .getComment(params[0]);
            } catch (AppException e) {
                Log.d(TAG, e.getMessage(), e);
            }
            return comment;
        }

        @Override
        protected void onPostExecute(Comment result) {
            loadComment(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(CommentDetailActivity.this);
            pd.setTitle("Loading...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }




    private class LikeDislikeCommentTask extends AsyncTask<String, Void, Comment> {
        private ProgressDialog pd;

        @Override
        protected Comment doInBackground(String... params) {
            Comment comment = null;
            try {
                String contentType = (String) getIntent().getExtras().get("type");
                String urlComment = (String) getIntent().getExtras().get("url");
                comment = CalendappAPI.getInstance(CommentDetailActivity.this)
                        .LikeDislikeComment(params[0], contentType, urlComment);
            } catch (AppException e) {
                Log.d(TAG, e.getMessage(), e);
            }
            return comment;
        }

        @Override
        protected void onPostExecute(Comment result) {
            loadComment(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
//            Do nothing
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_detail_layout);

        String urlComment = (String) getIntent().getExtras().get("url");
        (new FetchCommentTask()).execute(urlComment);
    }

    private void loadComment(Comment comment) {
        TextView tvDetailUsername = (TextView) findViewById(R.id.tvDetailUsername);
        TextView tvDetailContent = (TextView) findViewById(R.id.tvDetailContent);
        TextView tvDetailLikeCount = (TextView) findViewById(R.id.tvDetailLikeCount);
        TextView tvDetailDislikeCount = (TextView) findViewById(R.id.tvDetailDislikeCount);

        tvDetailUsername.setText(comment.getUsername());
        tvDetailContent.setText(comment.getContent());
        tvDetailLikeCount.setText(Integer.toString(comment.getLikes()));
        tvDetailDislikeCount.setText(Integer.toString(comment.getDislikes()));
    }

    public void likeComment(View v) {
       String urlLike = (String) getIntent().getExtras().get("url-like");
       (new LikeDislikeCommentTask()).execute(urlLike);
   }

  public void dislikeComment(View v) {
       String urlDislike = (String) getIntent().getExtras().get("url-dislike");
      (new LikeDislikeCommentTask()).execute(urlDislike);
   }
}