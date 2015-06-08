package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.Event;

/**
 * Created by jordi on 8/06/15.
 */
public class EventAdapter extends BaseAdapter {

    ArrayList<Event> data;
    LayoutInflater inflater;


    public EventAdapter(Context context, ArrayList<Event> data) {
        super();
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    private static class ViewHolder {
        TextView tvname;
        TextView tvUsername;
        TextView tvDate;
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
        return ((Event) getItem(position)).getEventid();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row_sting, null);
            viewHolder = new ViewHolder();
            viewHolder.tvname = (TextView) convertView
                    .findViewById(R.id.tvname);
            viewHolder.tvUsername = (TextView) convertView
                    .findViewById(R.id.tvUsername);
            viewHolder.tvDate = (TextView) convertView
                    .findViewById(R.id.tvDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String subject = data.get(position).getSubject();
        String username = data.get(position).getUsername();
        String date = SimpleDateFormat.getInstance().format(
                data.get(position).getLastModified());
        viewHolder.tvSubject.setText(subject);
        viewHolder.tvUsername.setText(username);
        viewHolder.tvDate.setText(date);
        return convertView;
    }
