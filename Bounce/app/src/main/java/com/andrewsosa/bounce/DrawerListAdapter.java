package com.andrewsosa.bounce;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by andrewsosa on 3/7/15.
 */
public class DrawerListAdapter extends SimpleCursorAdapter {

    int[] icons;
    Context context;

    public DrawerListAdapter(Context context, int layout, Cursor c, int[] icons, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.icons = icons;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        try {
            ImageView imageView = (ImageView) view.findViewById(R.id.list_icon);
            String name = ((TextView)view.findViewById(R.id.list_name)).getText().toString();

            switch(name) {
                case "Inbox":
                    imageView.setImageDrawable(context.getResources().getDrawable(icons[0]));
                    break;
                case "Upcoming":
                    imageView.setImageDrawable(context.getResources().getDrawable(icons[1]));
                    break;
                case "All Tasks":
                    imageView.setImageDrawable(context.getResources().getDrawable(icons[2]));
                    break;
                case "Completed":
                    imageView.setImageDrawable(context.getResources().getDrawable(icons[3]));
                    break;
                case "Unassigned":
                    imageView.setImageDrawable(context.getResources().getDrawable(icons[4]));
                    break;
                case "Divider":
                    view = ((Dashboard)context).getLayoutInflater().inflate(R.layout.drawer_divider, parent, false);
                    return view;

            }

        } catch (NullPointerException e) {
            Log.e("Bounce", "Could not find R.id.list_icon in layout");
        } catch (IndexOutOfBoundsException i) {
            Log.e("Bounce", "Index out of bounds error, could not find icon for position given.");
        }

        try {
            TextView textView = (TextView) view.findViewById(R.id.list_name);
            //textView.setTextColor(makeColorStateListForItem(position));
        } catch (NullPointerException n) {
            Log.e("Bounce", "Could not find 'list_name' view by id. ");
        } catch (Exception e) {
            Log.e("Bounce", "Could not make color state list for position: " + position);
        }

        return view;
    }





}
