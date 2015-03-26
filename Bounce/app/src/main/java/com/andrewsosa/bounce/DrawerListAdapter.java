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

            if (position < 5) {
                imageView.setImageDrawable(context.getResources().getDrawable(icons[position]));
            }

        } catch (NullPointerException e) {
            Log.e("Beacon", "Could not find R.id.list_icon in layout");
        } catch (IndexOutOfBoundsException i) {
            Log.e("Beacon", "Index out of bounds error, could not find icon for position given.");
        }

        try {
            TextView textView = (TextView) view.findViewById(R.id.list_name);
            textView.setTextColor(makeColorStateListForItem(position));
        } catch (Exception e) {
            Log.e("Beacon", "butts");
        }

        return view;
    }

    private ColorStateList makeColorStateListForItem(int position){
        int pressedColor = pressedColorForItem(position);
        int checkedColor = checkedColorForItem(position);
        int defaultColor = defaultColorForItem(position);
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_activated},
                        new int[]{0},
                },
                new int[]{
                        pressedColor, //use when state is pressed
                        checkedColor, //use when state is checked, but not pressed
                        defaultColor}); //used when state is not pressed, nor checked

        return colorStateList;
    }

    private int pressedColorForItem(int position){
        switch(position) {
            case 0: return context.getResources().getColor(R.color.inbox);
            case 1: return context.getResources().getColor(R.color.upcoming);
            case 2: return context.getResources().getColor(R.color.alltasks);
            case 3: return context.getResources().getColor(R.color.completed);
            case 4: return context.getResources().getColor(R.color.unassigned);
        }

        return context.getResources().getColor(R.color.primaryTextDark);
    }

    private int checkedColorForItem(int position){
        switch(position) {
            case 0: return context.getResources().getColor(R.color.inbox);
            case 1: return context.getResources().getColor(R.color.upcoming);
            case 2: return context.getResources().getColor(R.color.alltasks);
            case 3: return context.getResources().getColor(R.color.completed);
            case 4: return context.getResources().getColor(R.color.unassigned);
        }

        return context.getResources().getColor(R.color.primaryTextDark);
    }

    private int defaultColorForItem(int position){
        return context.getResources().getColor(R.color.primaryTextDark);
    }



}
