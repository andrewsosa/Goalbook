package com.andrewsosa.goalbook;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerViewAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {
    private static final String LIST_TYPE = "list_type";

    private String listType;


    // Query types
    Query queryRef;
    Firebase tasksRef;

    public RecyclerView mRecyclerView;
    private TaskInteractionListener mListener;
    private FirebaseRecyclerViewAdapter mAdapter;
    private View mEmptyView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param listType Filter for tasks shown on list.
     * @return A new instance of fragment ListFragment.
     */
    public static ListFragment newInstance(String listType) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(LIST_TYPE, listType);
        fragment.setArguments(args);
        return fragment;
    }

    // Required empty public constructor
    public ListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listType = getArguments().getString(LIST_TYPE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // If we need to do anything, we do it here.

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext())
                        .color(getResources().getColor(R.color.dividerColor))
                        .sizeResId(R.dimen.div_height)
                        .marginResId(R.dimen.div_right_margin, R.dimen.div_right_margin)
                        .build()
        );

        String uid = mListener.getSharedPreferences().getString(Goalbook.UID, "");
        Firebase ref = new Firebase(Goalbook.URL);
        Firebase userRef = ref.child("users").child(uid);
        tasksRef = userRef.child("tasks");

        // Handle type
        queryRef = tasksRef.orderByChild("priority").equalTo(listType);

        mAdapter = new GoalCatagoryAdapter(Goal.class, R.layout.recycler_tile_normal, TaskViewHolder.class, queryRef);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateEmptyView();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateEmptyView();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                updateEmptyView();
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        updateEmptyView();




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout, get items, return
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_tasks);
        mEmptyView = v.findViewById(R.id.rv_bg);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (TaskInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TaskInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public RelativeLayout tile;
        public TextView titleText;
        public TextView subtitleText;
        public CheckBox checkbox;

        // Constructor
        public TaskViewHolder(View v) {
            super(v);
            tile = (RelativeLayout) v.findViewById(R.id.tile);
            titleText = (TextView) v.findViewById(R.id.tile_header);
            subtitleText = (TextView) v.findViewById(R.id.tile_subheader);
            checkbox = (CheckBox) v.findViewById(R.id.tile_checkbox);
        }
    }

    public void updateEmptyView() {
        mEmptyView.setVisibility((mAdapter.getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE));
    }

    public interface TaskInteractionListener {
        void onTaskClick(String key);
        SharedPreferences getSharedPreferences();
    }

    public class GoalCatagoryAdapter extends FirebaseRecyclerViewAdapter<Goal, TaskViewHolder> {

        public GoalCatagoryAdapter(Class<Goal> modelClass, int modelLayout, Class<TaskViewHolder> viewHolderClass, Query ref) {
            super(modelClass, modelLayout, viewHolderClass, ref);
        }

        @Override
        public void populateViewHolder(TaskViewHolder viewHolder, final Goal Goal) {
            viewHolder.titleText.setText(Goal.getName());
            viewHolder.checkbox.setChecked(Goal.isDone());
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (buttonView.isPressed()) {
                        Goal.setDone(isChecked);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tasksRef.child(Goal.getUuid()).setValue(Goal);
                            }
                        }, 500);
                    }
                }
            });
            viewHolder.subtitleText.setText(GoalTools.shortTimestampString(Goal));
            viewHolder.tile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onTaskClick(Goal.getUuid());
                }
            });
        }
    }




}
