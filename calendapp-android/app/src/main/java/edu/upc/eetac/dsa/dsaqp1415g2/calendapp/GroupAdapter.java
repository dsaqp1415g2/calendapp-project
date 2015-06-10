package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.Group;

/**
 * Created by angel on 10/06/15.
 */
public class GroupAdapter extends BaseAdapter {
    ArrayList<Group> data;
    LayoutInflater inflater;

    public GroupAdapter(Context context, ArrayList<Group> data){
        super();
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    private static class ViewHolder{
        TextView tvNameGroup;
        TextView tvAdmin;
        TextView tvShared;
    }

    @Override
    public int getCount(){
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position){
        return ((Group) getItem(position)).getGroupid();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row_groups, null);
            viewHolder = new ViewHolder();
            viewHolder.tvNameGroup = (TextView) convertView
                    .findViewById(R.id.tvNameGroup);
            viewHolder.tvAdmin = (TextView) convertView
                    .findViewById(R.id.tvAdmin);
            viewHolder.tvShared = (TextView) convertView
                    .findViewById(R.id.tvShared);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String name = data.get(position).getName();
        String admin = data.get(position).getAdmin();
        String shared;
        if (data.get(position).isShared())
            shared = "Compartido";
        else
            shared = "Privado";

        viewHolder.tvNameGroup.setText(name);
        viewHolder.tvAdmin.setText(admin);
        viewHolder.tvShared.setText(shared);
        return convertView;
    }


}
