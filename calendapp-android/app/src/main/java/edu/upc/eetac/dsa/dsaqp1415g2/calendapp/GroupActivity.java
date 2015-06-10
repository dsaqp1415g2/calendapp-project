package edu.upc.eetac.dsa.dsaqp1415g2.calendapp;

import android.app.ListActivity;
import android.os.Bundle;

import java.util.ArrayList;

import edu.upc.eetac.dsa.dsaqp1415g2.calendapp.api.Group;

/**
 * Created by angel on 10/06/15.
 */
public class GroupActivity extends ListActivity {
    private ArrayList<Group> groupsList;
    private GroupAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_my_groups);

        groupsList = new ArrayList<Group>();
        adapter = new GroupAdapter(this, groupsList);
        setListAdapter(adapter);

        (new FetchGroupsTask()).execute();
    }
}
