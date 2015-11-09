package com.andrewsosa.bounce;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.*;
import java.util.List;

public class TaskRecyclerAdapterBase extends RecyclerView.Adapter<TaskRecyclerAdapterBase.ViewHolder> {

    // The Dataset
    private static List<ParseTask> mDataset;
    private static TaskEventListener taskEventListener;
    private int activeItemNumber = -1;
    private boolean useSmallTiles = false;

    // Constructor for setting up the dataset
    public TaskRecyclerAdapterBase(ArrayList<ParseTask> myDataset, TaskEventListener listener) {
        mDataset = new ArrayList<>(myDataset);
        taskEventListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TaskRecyclerAdapterBase.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // This is our view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_tile_normal, parent, false);

        // Return the new view
        return new ViewHolder(v);

    }

    public void replaceData(List<ParseTask> parseTasks) {
        mDataset = parseTasks;
        notifyDataSetChanged();
    }

    public ArrayList<ParseTask> getDataset() {
        return new ArrayList<>(mDataset);
    }


    // Add new items to the dataset
    public void addElement(ParseTask e) {
        mDataset.add(e);
        notifyItemInserted(mDataset.size()-1);
        Log.d("Bounce", "Adding ParseTask called ");

    }

    public void changeElement(int i, ParseTask e){
        mDataset.set(i, e);
        notifyItemChanged(i);
    }

    public void removeElementAt(int i) {
        mDataset.remove(mDataset.get(i));
        notifyItemRemoved(i);

    }

    public void removeElement(ParseTask t){
        notifyItemRemoved(mDataset.indexOf(t));
        mDataset.remove(t);
    }

    public void setActiveElement(int i) {
        activeItemNumber =  i;
    }

    public void setActiveElementFromTask(ParseTask t) {
        this.activeItemNumber = mDataset.indexOf(t);
    }

    public void removeActiveElement() {

        mDataset.remove(activeItemNumber);
        notifyItemRemoved(activeItemNumber);
        Log.d("Bounce", "Removed postion " + activeItemNumber);

    }

    public int getActiveItemNumber() {
        return activeItemNumber;
    }

    public ParseTask getActiveItem() {
        if(activeItemNumber!= -1) return mDataset.get(activeItemNumber);
        else return null;
    }

    public void setUseSmallTiles(boolean useSmallTiles) {
        this.useSmallTiles = useSmallTiles;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final ParseTask parseTask = mDataset.get(position);

        String taskName = parseTask.getName();
        /*if(taskName.length() > 16) {
            taskName = taskName.substring(0, 15) + "...";
        } */
        holder.titleText.setText(taskName);
        holder.checkbox.setChecked(parseTask.isDone());
        holder.parseTask = parseTask;

        // Hide date text if there is not one set, else show it again
        if(parseTask.getDeadline() == null || useSmallTiles || parseTask.getDeadlineAsTime().equals("Unspecified")) {
            holder.subtitleText.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams)holder.titleText.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            holder.titleText.setLayoutParams(layoutParams);

        } else {
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams)holder.titleText.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.CENTER_VERTICAL);
            holder.titleText.setLayoutParams(layoutParams);
            holder.subtitleText.setVisibility(View.VISIBLE);
            holder.subtitleText.setText(parseTask.getDeadlineAsTime());// + " on "
            //+ parseTask.getDeadlineAsSimpleDateString());
        }

        /*if(useSmallTiles) {
            // TODO HANDLE SMALL TILES
        }*/

    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public ParseTask getItem(int i) {
        return mDataset.get(i);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Reference for the Event that needs to be opened on the click listener
        ParseTask parseTask;

        // each data item is just a string in this case
        public RelativeLayout tile;
        public TextView titleText;
        public TextView subtitleText;
        public CheckBox checkbox;

        // Constructor
        public ViewHolder(View v) {
            super(v);
            tile = (RelativeLayout) v.findViewById(R.id.tile);
            titleText = (TextView) v.findViewById(R.id.tile_header);
            subtitleText = (TextView) v.findViewById(R.id.tile_subheader);
            checkbox = (CheckBox) v.findViewById(R.id.tile_checkbox);

            v.setOnClickListener(this);

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkbox.isChecked()) {
                        parseTask.setDone(true);
                    } else {
                        parseTask.setDone(false);
                    }
                    taskEventListener.onTaskCheckboxInteraction(parseTask);
                    //ViewHolder.this.t.removeElement(parseTask); TODO REINTRODUCE CHECKBOX RESPONSE
                }
            });

        }

        @Override
        public void onClick(View v) {
            Log.d("Bounce", "Tile onClick!");
            taskEventListener.onTaskSelect(parseTask);
        }
    }

    public interface TaskEventListener {
        void onTaskCheckboxInteraction(ParseTask parseTask);
        void onTaskSelect(ParseTask parseTask);
    }

}