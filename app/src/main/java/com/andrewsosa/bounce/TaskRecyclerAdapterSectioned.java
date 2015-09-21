package com.andrewsosa.bounce;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by andrewsosa on 6/29/15.
 */
public class TaskRecyclerAdapterSectioned extends SimpleSectionedRecyclerViewAdapter {

    TaskRecyclerAdapterBase mBaseAdapter;

    boolean today; // TODO this needs to be reworked to support ALL preset views

    public TaskRecyclerAdapterSectioned(Context context, int sectionResourceId, int textResourceId, TaskRecyclerAdapterBase baseAdapter) {
        super(context, sectionResourceId, textResourceId, baseAdapter);

        mBaseAdapter = baseAdapter;

        today = false;

        mBaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyDataSetChanged();
                autoSections();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyItemRangeChanged(positionStart, itemCount);
                autoSections();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyItemRangeInserted(positionStart, itemCount);
                autoSections();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyItemRangeRemoved(positionStart, itemCount);
                autoSections();
            }
        });
    }

    public void isToday(boolean today) {
        this.today = today;
    }


    public void autoSections() {
        List<Task> tasks = mBaseAdapter.getDataset();

        if(today) {

            // Calculate today's date for later
            String today = new SimpleDateFormat(
                    "MMMM dd", Locale.getDefault()).format(new GregorianCalendar().getTime());


            //This is the code to provide a sectioned list
            List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                    new ArrayList<Section>();


            // Handle section titles and where
            sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, today));
            //sections.add(new SimpleSectionedRecyclerViewAdapter.Section(2,today));
            SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
            setSections(sections.toArray(dummy));
        } else {
            //This is the code to provide a sectioned list
            List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                    new ArrayList<Section>();


            // Handle section titles and where
            //sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, today));
            //sections.add(new SimpleSectionedRecyclerViewAdapter.Section(2,today));
            SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
            setSections(sections.toArray(dummy));
        }
    }


}
