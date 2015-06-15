package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.Comment;

/**
 * Created by Jordi on 14/06/2015.
 */
public class CommentAdapter extends BaseAdapter{

    ArrayList<Comment> data;
    LayoutInflater inflater;

    public CommentAdapter(Context context, ArrayList<Comment> data){
        super();
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    private static class ViewHolder{
        TextView tvDetailUsername;
        TextView tvDetailContent;
        TextView tvDetailLikeCount;
        TextView tvDetailDislikeCount;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ((Comment) getItem(position)).getCommentid();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.list_comment_detail_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.tvDetailUsername = (TextView) convertView.findViewById(R.id.tvDetailUsername);
            viewHolder.tvDetailContent = (TextView) convertView.findViewById(R.id.tvDetailContent);
            viewHolder.tvDetailLikeCount = (TextView) convertView.findViewById(R.id.tvDetailLikeCount);
            viewHolder.tvDetailDislikeCount = (TextView) convertView.findViewById(R.id.tvDetailDislikeCount);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String username = data.get(position).getUsername();
        String content = data.get(position).getContent();
        int likes = data.get(position).getLikes();
        int dislikes = data.get(position).getDislikes();

        viewHolder.tvDetailUsername.setText(username);
        viewHolder.tvDetailContent.setText(content);
        viewHolder.tvDetailLikeCount.setText(Integer.toString(likes));
        viewHolder.tvDetailDislikeCount.setText(Integer.toString(dislikes));
        return convertView;
    }

}