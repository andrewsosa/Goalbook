package com.andrewsosa.bounce;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnTaskInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment implements
        TaskRecyclerAdapterBase.TaskEventListener {
    // the fragment initialization parameters
    private static final String FRAGMENT_TAG = "TAG";

    /** Tags for fragment types */
    public static final String TODAY = "TAG_TODAY";
    public static final String UPCOMING = "TAG_UPCOMING";
    public static final String OVERDUE = "TAG_OVERDUE";
    public static final String COMPLETED = "TAG_COMPLETED";
    public static final String ALL_TASKS = "TAG_ALL_TASKS";
    public static final String UNASSIGNED = "TAG_UNASSIGNED";
    public static final String OTHER_LIST = "TAG_OTHER_LIST";

    private String mTag;
    private ParseQuery<ParseTask> query;
    private OnTaskInteractionListener mListener;
    private TaskRecyclerAdapterBase mAdapter;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mEmptyView;
    //private TextView dateDisplay;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param query A pre-build query for certain tasks.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String tag, ParseQuery<ParseTask> query) {
        DashboardFragment fragment = new DashboardFragment();
        fragment.setQuery(query);
        Bundle args = new Bundle();
        args.putString(FRAGMENT_TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTag = getArguments().getString(FRAGMENT_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.primary_recycler);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        mEmptyView = (LinearLayout) v.findViewById(R.id.empty_view);
        //dateDisplay = (TextView) v.findViewById(R.id.date_text);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Things for recyclerviews
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null, true, true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify my base adapter
        mAdapter = new TaskRecyclerAdapterBase(new ArrayList<ParseTask>(), this);

        TaskRecyclerAdapterSectioned mSectionedAdapter =
                new TaskRecyclerAdapterSectioned(getActivity(), R.layout.recycler_tile_subheader,
                        R.id.date_text, mAdapter);

        //Apply this adapter to the RecyclerView
        //mRecyclerView.setAdapter(mAdapter);
        if(mTag.equals(DashboardFragment.TODAY)) {
            mSectionedAdapter.isToday(true);
        } else {
            mSectionedAdapter.isToday(false);
        }
        mRecyclerView.setAdapter(mSectionedAdapter);

        if(savedInstanceState != null) {
            ArrayList<? extends Parcelable> parcelables =
                    savedInstanceState.getParcelableArrayList("tasks");
            if(parcelables != null) {
                ArrayList<ParseTask> parseTasks = new ArrayList<>();
                try {
                    for (Object o : parcelables) {
                        parseTasks.add((ParseTask) o);
                    }
                    mAdapter.replaceData(parseTasks);
                } catch (Exception e) {
                    Log.e("Type Error", "Error converting Parcelables to Tasks");
                }
            }
        }

        // Refresher view
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeListener());
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary);

    }

    public List<SimpleSectionedRecyclerViewAdapter.Section> detectSections(List<ParseTask> dataset) {
        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();




        return sections;
    }

    public void setQuery(ParseQuery<ParseTask> query) {
        this.query = query;
    }

    public void onTaskSelect(ParseTask parseTask) {
        if (mListener != null) {
            mAdapter.setActiveElementFromTask(parseTask);
            mListener.launchActivityFromTask(parseTask);
        }
    }

    public void onTaskCheckboxInteraction(ParseTask parseTask) {
        if (mListener != null) {
            // Have DashboardActivity handle saving
            mListener.saveTask(parseTask);
        }
    }

    public TaskRecyclerAdapterBase relayAdapter() {
        return mAdapter;
    }

    public SwipeRefreshLayout relaySwipeLayout() {
        return mSwipeRefreshLayout;
    }

    public void showEmptyView(boolean empty) {

        if(empty) {
            mEmptyView.setVisibility(View.VISIBLE);
            //mSwipeRefreshLayout.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            //mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }

    }

    public void doQuery() {
        if(query != null) {

            try {
                // Load the query results into list
                query.findInBackground(new FindCallback<ParseTask>() {
                    @Override
                    public void done(List<ParseTask> list, ParseException e) {
                        if (e == null) {
                            if (list.size() == 0) {
                                showEmptyView(true);
                            } else {
                                showEmptyView(false);
                            }

                            mAdapter.replaceData(list);

                        } else {
                            Log.e("ParseQuery", "Error:" + e.getMessage());
                        }
                    }
                });

            } catch (Exception e) {
                Log.e("doQuery", e.getMessage());
            }

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTaskInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTaskInteractionListener");
        }

        doQuery();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<? extends Parcelable> list = mAdapter.getDataset();
        outState.putParcelableArrayList("tasks", list);

    }

    /**
     *  SwipeListener for refresh layout
     */
    public class SwipeListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            mListener.onRefresh();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTaskInteractionListener {
        void launchActivityFromTask(ParseTask parseTask);
        void saveTask(ParseTask parseTask);
        void onRefresh();
    }

}
