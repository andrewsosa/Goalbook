package com.andrewsosa.bounce;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder> {

    // The Dataset
    private static ArrayList<Task> mDataset;
    private static Activity activity;
    private static int activeItem = -1;

    // Constructor for setting up the dataset
    public TaskRecyclerAdapter(ArrayList<Task> myDataset, Activity c) {
        mDataset = new ArrayList<>(myDataset);
        activity = c;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TaskRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // This is our view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_tile, parent, false);

        // Return the new view
        return new ViewHolder(v);

    }

    // Add new items to the dataset
    public void addElement(Task e) {
        mDataset.add(e);
        notifyItemInserted(mDataset.size()-1);
        Log.d("Bounce", "Adding Task called ");

    }

    public void removeElementAt(int i) {

        mDataset.remove(mDataset.get(i));
        notifyItemRemoved(i);

    }

    public void setActiveElement(int i) {
        activeItem =  i;
    }

    public void removeActiveElement() {

        mDataset.remove(activeItem);
        notifyItemRemoved(activeItem);
        Log.d("Bounce", "Removed postion " + activeItem);

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final Task task = mDataset.get(position);

        // Handle name overflow
        String taskName = task.getName();
        /*if(taskName.length() > 16) {
            taskName = taskName.substring(0, 15);
        } */
        holder.titleText.setText(taskName);

        // Hide date text if there is not one set
        if(task.getDate() == null) {
            holder.subtitleText.setVisibility(View.GONE);

            // Fix header text now

            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams)holder.titleText.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            holder.titleText.setLayoutParams(layoutParams);

        } else {
            holder.subtitleText.setText(TaskDataSource.toDisplayFormat(task.getDate()));
        }


        // Handle checkbox clicks
        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Bounce", "Checkbox onClick!");
            }
        });

        // Store task for click listening and stuff
        holder.task = task;

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public Task getItem(int i) {
        return mDataset.get(i);
    }



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Reference for the Event that needs to be opened on the click listener
        Task task;

        // each data item is just a string in this case
        public TextView titleText;
        public TextView subtitleText;
        public CheckBox checkbox;

        // Constructor
        public ViewHolder(View v) {
            super(v);
            titleText = (TextView) v.findViewById(R.id.tile_header);
            subtitleText = (TextView) v.findViewById(R.id.tile_subheader);
            checkbox = (CheckBox) v.findViewById(R.id.tile_checkbox);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            Log.d("Bounce", "Tile onClick!");
            Intent intent = new Intent(activity, TaskViewActivity.class);
            intent.putExtra("Task", task);

            TaskRecyclerAdapter.activeItem = mDataset.indexOf(task);

            //activity.startActivity(intent);
            activity.startActivityForResult(intent, 1);


        }
    }

}