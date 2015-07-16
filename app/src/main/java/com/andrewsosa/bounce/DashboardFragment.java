package com.andrewsosa.bounce;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


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
    public static final String INBOX = "TAG_INBOX";
    public static final String UPCOMING = "TAG_UPCOMING";
    public static final String OVERDUE = "TAG_OVERDUE";
    public static final String COMPLETED = "TAG_COMPLETED";
    public static final String ALL_TASKS = "TAG_ALL_TASKS";
    public static final String UNASSIGNED = "TAG_UNASSIGNED";
    public static final String OTHER_LIST = "TAG_OTHER_LIST";

    private String mTag;
    private ParseQuery<Task> query;
    private OnTaskInteractionListener mListener;
    private TaskRecyclerAdapterBase mAdapter;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //private TextView dateDisplay;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param query A pre-build query for certain tasks.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String tag, ParseQuery<Task> query) {
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
        mAdapter = new TaskRecyclerAdapterBase(new ArrayList<Task>(), this);

        /*//This is the code to provide a sectioned list
        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();

        // Handle section titles and where
        sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0,today));

        //Add your adapter to the sectionAdapter
        SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new
                SimpleSectionedRecyclerViewAdapter(getActivity(), R.layout.recycler_tile_subheader,
                R.id.date_text,mAdapter);

        SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
        mSectionedAdapter.setSections(sections.toArray(dummy));*/

        TaskRecyclerAdapterSectioned mSectionedAdapter =
                new TaskRecyclerAdapterSectioned(getActivity(), R.layout.recycler_tile_subheader,
                        R.id.date_text, mAdapter);

        //Apply this adapter to the RecyclerView
        //mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setAdapter(mSectionedAdapter);

        if(savedInstanceState != null) {
            ArrayList<? extends Parcelable> parcelables =
                    savedInstanceState.getParcelableArrayList("tasks");
            if(parcelables != null) {
                ArrayList<Task> tasks = new ArrayList<>();
                try {
                    for (Object o : parcelables) {
                        tasks.add((Task) o);
                    }
                    mAdapter.replaceData(tasks);
                } catch (Exception e) {
                    Log.e("Type Error", "Error converting Parcelables to Tasks");
                }
            }
        }

        // Refresher view
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeListener());
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary);
    }

    public List<SimpleSectionedRecyclerViewAdapter.Section> detectSections(List<Task> dataset) {
        List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();




        return sections;
    }

    public void setQuery(ParseQuery<Task> query) {
        this.query = query;
    }

    public void onTaskSelect(Task task) {
        if (mListener != null) {
            mAdapter.setActiveElementFromTask(task);
            mListener.launchActivityFromTask(task);
        }
    }

    public void onTaskCheckboxInteraction(Task task) {
        if (mListener != null) {
            // Have Dashboard handle saving
            mListener.saveTask(task);
        }
    }

    public TaskRecyclerAdapterBase relayAdapter() {
        return mAdapter;
    }

    public SwipeRefreshLayout relaySwipeLayout() {
        return mSwipeRefreshLayout;
    }

    public void doQuery() {
        if(query != null) {
            // Load the query results into list
            query.findInBackground(new FindCallback<Task>() {
                @Override
                public void done(List<Task> list, ParseException e) {
                    if (e == null) {
                        mAdapter.replaceData(list);
                    } else {
                        Log.e("ParseQuery", "Error:" + e.getMessage());
                    }
                }
            });
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
        void launchActivityFromTask(Task task);
        void saveTask(Task task);
        void onRefresh();
    }

}
