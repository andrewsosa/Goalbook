package com.andrewsosa.bounce;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Created by andrewsosa on 3/7/15.
 */
public class DrawerListAdapter extends ArrayAdapter<ParseTaskList> {

    int[] icons;
    Context context;

    public DrawerListAdapter(Context context, int resource, java.util.List<ParseTaskList> objects, int[] icons) {
        super(context, resource, objects);
        this.context = context;
        this.icons = icons;
    }
}
