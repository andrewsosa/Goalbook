package com.andrewsosa.goalbook;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerViewAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {
    private static final String LIST_TYPE = "list_type";
    public static final String ARCHIVE = "archive";
    public static final String NOTIFICATIONS = "notifications";

    private String listType;


    // Query types
    Query queryRef;
    Firebase tasksRef;
    Firebase archiveRef;
    Firebase remindersRef;

    public RecyclerView mRecyclerView;
    private TaskInteractionListener mListener;
    private FirebaseRecyclerViewAdapter mAdapter;
    private ImageView mEmptyView;

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
    // If we need to do anything, we do it here.
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if(listType.equals(ARCHIVE)) mEmptyView.setImageResource(R.drawable.ic_empty_archive_96dp);

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
        tasksRef = ref.child("tasks").child(uid);
        archiveRef = ref.child("archive").child(uid);

        // Handle type
        if(listType.equals(NOTIFICATIONS)) {
            queryRef = new Firebase(Goalbook.URL).child("messages").child(uid).limitToLast(10);
            mAdapter = new ReminderAdapter(Reminder.class, R.layout.recycler_tile_normal, ReminderViewHolder.class, queryRef);
        } else if (listType.equals(ARCHIVE)) {
            queryRef = archiveRef.orderByChild("timestamp");
            mAdapter = new ArchiveAdapter(Goal.class, R.layout.recycler_tile_normal, GoalViewHolder.class, queryRef);
        } else {
            // TODO break down tasks db into groups
            queryRef = tasksRef.orderByChild("priority").equalTo(listType);
            mAdapter = new GoalCatagoryAdapter(Goal.class, R.layout.recycler_tile_normal, GoalViewHolder.class, queryRef);
        }


        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateEmptyView();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                //mAdapter.notifyItemRangeInserted(positionStart, itemCount);
                updateEmptyView();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                //mAdapter.notifyItemRangeRemoved(positionStart, itemCount);
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
        mEmptyView = (ImageView) v.findViewById(R.id.rv_bg);
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

    public static class GoalViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public RelativeLayout tile;
        public TextView titleText;
        public TextView subtitleText;
        public CheckBox checkbox;

        // Constructor
        public GoalViewHolder(View v) {
            super(v);
            tile = (RelativeLayout) v.findViewById(R.id.tile);
            titleText = (TextView) v.findViewById(R.id.tile_header);
            subtitleText = (TextView) v.findViewById(R.id.tile_subheader);
            checkbox = (CheckBox) v.findViewById(R.id.tile_checkbox);
        }
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        public TextView titleText;
        public TextView subtitleText;

        public ReminderViewHolder(View v) {
            super(v);
            titleText = (TextView) v.findViewById(R.id.tile_header);
            subtitleText = (TextView) v.findViewById(R.id.tile_subheader);
        }
    }

    public void updateEmptyView() {
        mEmptyView.setVisibility((mAdapter.getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE));
    }

    public interface TaskInteractionListener {
        void onTaskClick(String key);
        void onTaskLongClick(String key);
        SharedPreferences getSharedPreferences();
    }

    public class GoalCatagoryAdapter extends FirebaseRecyclerViewAdapter<Goal, GoalViewHolder> {

        public GoalCatagoryAdapter(Class<Goal> modelClass, int modelLayout, Class<GoalViewHolder> viewHolderClass, Query ref) {
            super(modelClass, modelLayout, viewHolderClass, ref);
        }

        @Override
        public void populateViewHolder(GoalViewHolder viewHolder, final Goal Goal) {
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
            String subtitle = "Created " + GoalTools.shortTimestampString(Goal);
            viewHolder.subtitleText.setText(subtitle);
            viewHolder.tile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onTaskClick(Goal.getUuid());
                }
            });
            viewHolder.tile.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mListener.onTaskLongClick(Goal.getUuid());
                    return true;
                }
            });
        }
    }

    public class ArchiveAdapter extends GoalCatagoryAdapter {
        public ArchiveAdapter(Class<Goal> modelClass, int modelLayout, Class<GoalViewHolder> viewHolderClass, Query ref) {
            super(modelClass, modelLayout, viewHolderClass, ref);
        }

        @Override
        public void populateViewHolder(GoalViewHolder viewHolder, final Goal goal) {
            viewHolder.titleText.setText(goal.getName());
            viewHolder.checkbox.setClickable(false);
            viewHolder.checkbox.setChecked(goal.isDone());
            String subtitle = "Created " + GoalTools.shortTimestampString(goal);
            viewHolder.subtitleText.setText(subtitle);
            // TODO enable archive editing

            viewHolder.tile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MaterialDialog.Builder(getContext())
                            .content("Restore goal?")
                            .positiveText("Restore")
                            .negativeText("Cancel")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    tasksRef.child(goal.getUuid()).setValue(goal);
                                    archiveRef.child(goal.getUuid()).removeValue();
                                }
                            })
                            .show();
                }
            });
        }

    }

    public class ReminderAdapter extends FirebaseRecyclerViewAdapter<Reminder, ReminderViewHolder> {
        public ReminderAdapter(Class<Reminder> modelClass, int modelLayout, Class<ReminderViewHolder> viewHolderClass, Query ref) {
            super(modelClass, modelLayout, viewHolderClass, ref);
        }

        @Override
        protected void populateViewHolder(ReminderViewHolder viewHolder, Reminder model) {
            //super.populateViewHolder(viewHolder, model);
        }
    }




}
