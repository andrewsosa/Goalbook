package com.andrewsosa.bounce;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class Dashboard extends Activity implements Toolbar.OnMenuItemClickListener,
        DatePickerReceiver {

    String TASKS_LABEL = "tasks";
    String LISTS_LABEL = "lists";

    // Actionbar and Navdrawer nonsense
    ActionBarDrawerToggle mDrawerToggle;

    // Recyclerview things
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    //private static TaskRecyclerAdapter mAdapter;
    private static ParseTaskRecyclerAdapter mParseAdapter;
    static EditText editText;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // Data sources
    //private static TaskDataSource taskDataSource;
    //private static ListDataSource listDataSource;

    // Toggle view for the add menu
    private boolean showingInput = false;

    // UI Components
    ListView drawerList;
    ArrayAdapter<ParseList> drawerListAdapter;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    FloatingActionButton actionButton;
    View emptyView;

    // Data for menu navigation
    Integer selectedPosition = 1;
    ArrayList<String> titles;
    String[] presetTitles = {
            "Inbox",
            "Upcoming",
            "Completed",
            "All Tasks",
            "Unassigned",
            "Divider"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Toolbar craziness
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Dashboard");
        toolbar.inflateMenu(R.menu.menu_dashboard);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setTitleTextColor(getResources().getColor(R.color.abc_primary_text_material_dark));

        // Drawer craziness
        drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(mDrawerToggle);
        drawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.primaryColorDark));

        // Add button stuff
        actionButton = (FloatingActionButton) findViewById(R.id.add_button);
        actionButton.setOnClickListener(new FloatingActionButtonListener());

        // Things for recyclerviews
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, null, false, true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter 
        mParseAdapter = new ParseTaskRecyclerAdapter(new ArrayList<ParseTask>(), this);
        mRecyclerView.setAdapter(mParseAdapter);

        // Refresher view
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeListener());
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryColor);

        // Drawer List things
        drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerListAdapter = new ArrayAdapter<>(this,
                R.layout.drawer_item_view,
                R.id.list_name,
                new ArrayList<ParseList>());
        drawerList.setAdapter(drawerListAdapter);

        refreshListTitles();

        drawerList.setOnItemClickListener(new DrawerListListener());

        // Add Extra views to ListView
        addExtraViews();

        // Recover from rotates
        if (savedInstanceState != null && selectedPosition !=null) {
            selectPosition(selectedPosition);
        } else {
            // Select either the default item (0) or the last selected item.
            selectPosition(1);
            //drawerList.setItemChecked(1, true);
        }

        // For empty lists
        emptyView = findViewById(R.id.empty_view);

        // For logout
        RelativeLayout settings = (RelativeLayout) findViewById(R.id.logout_layout);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, SettingsActivity.class));
            }
        });

        // Things for adding tasks
        EditText editText = (EditText) findViewById(R.id.task_name_edittext);
        editText.setOnEditorActionListener(new EditorListener());

        ImageButton calendarButton = (ImageButton) findViewById(R.id.calendar_button);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.assignMethod(Dashboard.this);
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("Bounce", "On Activity Result");
        if (resultCode == RESULT_OK) {
            Log.d("Bounce", "Result Ok");
            if((data.getStringExtra("Action") != null) && (data.getStringExtra("Action").equals("delete"))) {
                Log.d("Bounce", "Action == Delete");
                mParseAdapter.removeActiveElement();
            }
            else {
                ParseQuery<ParseTask> query = ParseQuery.getQuery("Task");
                query.fromLocalDatastore();
                query.whereEqualTo("uuid", mParseAdapter.getActiveItem().getId());
                try {
                    ParseTask temp = query.getFirst();
                    mParseAdapter.changeElement(mParseAdapter.getActiveItemNumber(), temp);
                    mParseAdapter.notifyItemChanged(mParseAdapter.getActiveItemNumber());
                } catch (Exception e) {
                    Log.e("onActivityResult", e.getMessage());
                }
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //saveAllPinsToParse();
        loadFromParse();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                return true;
            case R.id.action_search_list:
                return true;
            case R.id.action_rename_list:
                renameListDialog();
                return true;
            case R.id.action_delete_list:
                deleteListDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteList(int position) {

        // Locate list
        String name = getTitle(position);
        ParseList list = localListQueryByName(name);

        // Remove tasks on list
        ParseQuery<ParseTask> query = ParseTask.getQuery();
        query.whereEqualTo("parent", list);
        query.findInBackground(new FindCallback<ParseTask>() {
            @Override
            public void done(List<ParseTask> list, ParseException e) {
                if (e == null) {
                    for (ParseTask t : list) {
                        t.deleteEventually();
                    }
                } else {
                    Log.e("deleteList", e.getMessage());
                }
            }
        });

        // Finish delete
        list.deleteEventually();

        // Handle UI changes
        refreshListTitles();
        selectPosition(1);


        Toast.makeText(this, "Removed list " + name + ".", Toast.LENGTH_SHORT).show();
    }

    public static ParseList localListQueryByName(String name) {
        ParseQuery<ParseList> query = ParseList.getQuery();
        query.fromLocalDatastore();
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereEqualTo("name", name);
        ParseList list = null;
        try {
            list = query.getFirst();
        } catch (Exception e) {
            Log.e("localListQueryByName", e.getMessage() + "" + name);
        }

        return list;
    }

    private EditText nameInput;
    private View positiveAction;
    private void createNewListDialog() {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .customView(R.layout.create_dialog_view, true)
                .title("List Name")
                .positiveText("Create")
                .negativeText(android.R.string.cancel)
                .negativeColor(R.color.secondaryTextDark)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (nameInput != null) {
                            ParseList parseList = new ParseList(nameInput.getText().toString());
                            saveNewList(parseList);
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        nameInput = (EditText) dialog.getCustomView().findViewById(R.id.list_name_input);
        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }

    private void renameListDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .customView(R.layout.create_dialog_view, true)
                .title("List Name")
                .positiveText("Rename")
                .negativeText(android.R.string.cancel)
                .negativeColor(R.color.secondaryTextDark)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (nameInput != null) {
                            ParseList parseList = localListQueryByName(getTitle(selectedPosition));
                            parseList.setName(nameInput.getText().toString());
                            saveList(parseList);
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        nameInput = (EditText) dialog.getCustomView().findViewById(R.id.list_name_input);
        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }

    private void deleteListDialog() {
        new MaterialDialog.Builder(this)
                .content("Are you sure you want to delete list " + getTitle(selectedPosition) +
                "? This will delete all items on the list, and can not be undone.")
                .positiveText("Delete")
                .negativeText("Cancel")
                .negativeColor(R.color.secondaryTextDark)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        deleteList(selectedPosition);
                    }
                })
                .show();
    }

    private void setTitle(String title) {
        toolbar.setTitle(title);
    }

    private void refreshListTitles() {
        initTitleList();
        addParseListTitles();
    }

    private ArrayList<String> initTitleList() {
        titles = new ArrayList<>();
        titles.addAll(Arrays.asList(presetTitles));
        return titles;
    }

    private void addParseListTitles() {

        ParseQuery<ParseList> listQuery = ParseList.getQuery();
        listQuery.fromLocalDatastore();
        listQuery.orderByAscending("createdAt");
        listQuery.findInBackground(new FindCallback<ParseList>() {
            @Override
            public void done(List<ParseList> parseLists, ParseException e) {
                if (e == null) {

                    for (ParseList l : parseLists) {
                        titles.add(l.getName());
                        //Log.d("addParseListTitles", "Added " + l.getName() + " to titles at index " +
                        //titles.indexOf(l.getName()));
                    }

                    drawerListAdapter.clear();
                    drawerListAdapter.addAll(parseLists);
                    drawerListAdapter.notifyDataSetChanged();
                }
            }
        });
    }



    private void selectPosition(int position) {
        selectedPosition = position;

        drawerList.setItemChecked(position, true);

        // Update dataset
        loadFromLocal(updateDataSet(position));

        // UI Stuff
        setTitle(getTitle(position)); // header off-by-one issue
        updateUIcolors(position);
        updateDateButton(position);
        updateToolbarMenu(position);
        drawerLayout.closeDrawer(findViewById(R.id.scrimInsetsFrameLayout));

    }

    private String getTitle(int position) {
        try {
            return titles.get(position - 1);
        } catch (IndexOutOfBoundsException e) {
            Log.e("getTitle", e.getMessage());
            return null;
        }
    }

    private void updateDateButton(int position) {
        ImageButton dateButton = (ImageButton) findViewById(R.id.calendar_button);
        if(position <= 2) {
            dateButton.setVisibility(View.GONE);
        } else {
            dateButton.setVisibility(View.VISIBLE);
        }
    }

    private void updateToolbarMenu(int position) {
        toolbar.getMenu().clear();
        if(position < 6) {
            toolbar.inflateMenu(R.menu.menu_dashboard);
        } else if (position > 6) {
            toolbar.inflateMenu(R.menu.menu_list);
        }
    }

    private int[] prepareListIcons() {
        return new int[] {
                R.drawable.ic_drawer_inbox,
                R.drawable.ic_drawer_upcoming,
                R.drawable.ic_drawer_completed,
                R.drawable.ic_drawer_alltasks,
                R.drawable.ic_drawer_unassigned
        };
    }

    private void updateUIcolors(int position){
        toolbar.setBackgroundColor(getToolbarColor(position));
        drawerLayout.setStatusBarBackgroundColor(getStatusbarColor(position));
        updateActionButton(position);

        // For tablets
        LinearLayout v = (LinearLayout) findViewById(R.id.tablet_bar);
        if(v != null) {
            v.setBackgroundColor(getToolbarColor(position));
        }
    }

    public int getCurrentToolbarColor() {
        return getToolbarColor(selectedPosition);
    }

    public int getCurrentStatusbarColor() {
        return getStatusbarColor(selectedPosition);
    }

    private int getToolbarColor(int position){
        switch(position) {
            case 1: return getResources().getColor(R.color.inbox);
            case 2: return getResources().getColor(R.color.upcoming);
            case 3: return getResources().getColor(R.color.completed);
            case 4  : return getResources().getColor(R.color.alltasks);
            case 5: return getResources().getColor(R.color.unassigned);
        }

        return getResources().getColor(R.color.unassigned);
    }

    private int getStatusbarColor(int position){
        switch(position) {
            case 1: return getResources().getColor(R.color.inboxDark);
            case 2: return getResources().getColor(R.color.upcomingDark);
            case 3: return getResources().getColor(R.color.completedDark);
            case 4: return getResources().getColor(R.color.alltasksDark);
            case 5: return getResources().getColor(R.color.unassignedDark);
        }

        return getResources().getColor(R.color.unassignedDark);
    }

    private void updateActionButton(int position){
        if(position == 1) {
            actionButton.setBackgroundColor(getResources().getColor(R.color.accentColor));
            actionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
        } else {
            actionButton.setBackgroundColor(getResources().getColor(R.color.windowBackground));
            actionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_grey600_24dp));
        }

    }

    private ParseQuery<ParseTask> updateDataSet(int position) {
        ParseQuery<ParseTask> query = ParseTask.getQuery();
        query.whereEqualTo("done", false);

        ParseList parseList = localListQueryByName(getTitle(position));

        switch(position) {
            case 1: return query.whereLessThanOrEqualTo("deadline", new Date());
            case 2: return query.whereGreaterThan("deadline", new Date()); // TODO check out 4oclock thing
            case 3: return query.whereEqualTo("done", true);
            case 4: return query;
            case 5: return query.whereEqualTo("parent", null);
            default: return query.whereEqualTo("parent", parseList);
        }
    }

    private void addExtraViews() {

        int[] icons = prepareListIcons();

        // Header view
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.drawer_header, drawerList, false);
        displayUserData(header);
        drawerList.addHeaderView(header, null, false);

        // Add the 5 presets
        for(int i = 0; i < 5; ++i) {
            ViewGroup preset = (ViewGroup) inflater.inflate(R.layout.drawer_item_view, drawerList, false);
            ImageView icon = (ImageView) preset.findViewById(R.id.list_icon);
            TextView name = (TextView) preset.findViewById(R.id.list_name);
            icon.setImageDrawable(getResources().getDrawable(icons[i]));
            name.setText(presetTitles[i]);
            name.setTextColor(makeColorStateListForItem(i));
            drawerList.addHeaderView(preset, null, true);
        }

        // Add header divider
        ViewGroup divider = (ViewGroup) inflater.inflate(R.layout.drawer_divider, drawerList, false);
        drawerList.addHeaderView(divider, null, false);

        // Add footer divider
        ViewGroup divider2 = (ViewGroup) inflater.inflate(R.layout.drawer_divider, drawerList, false);
        drawerList.addFooterView(divider2, null, false);

        // Footer view
        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.drawer_footer, drawerList, false);
        drawerList.addFooterView(footer, null, false);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewListDialog();
            }
        });
    }

    private void displayUserData(ViewGroup header) {
        try {
            TextView name = (TextView) header.findViewById(R.id.header_name);
            //TextView email = (TextView) header.findViewById(R.id.header_email);

            name.setText(ParseUser.getCurrentUser().getUsername());
            //email.setText(ParseUser.getCurrentUser().getEmail());
        } catch (Exception e) {
            Log.e("displayUserData", "Had issue displaying User data, skipped.");
        }
    }

    private ColorStateList makeColorStateListForItem(int position){
        ++position; // Handles header off-by-one
        int pressedColor = pressedColorForItem(position);
        int checkedColor = checkedColorForItem(position);
        int defaultColor = defaultColorForItem();
        //ColorStateList colorStateList =
        return  new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_activated},
                        new int[]{0},
                },
                new int[]{
                        pressedColor, //use when state is pressed
                        checkedColor, //use when state is checked, but not pressed
                        defaultColor}); //used when state is not pressed, nor checked;
    }

    private int pressedColorForItem(int position){
        switch(position) {
            case 1: return getResources().getColor(R.color.inbox);
            case 2: return getResources().getColor(R.color.upcoming);
            case 3: return getResources().getColor(R.color.completed);
            case 4: return getResources().getColor(R.color.alltasks);
            case 5: return getResources().getColor(R.color.unassigned);
        }

        return getResources().getColor(R.color.primaryTextDark);
    }

    private int checkedColorForItem(int position){
        switch(position) {
            case 1: return getResources().getColor(R.color.inbox);
            case 2: return getResources().getColor(R.color.upcoming);
            case 3: return getResources().getColor(R.color.completed);
            case 4: return getResources().getColor(R.color.alltasks);
            case 5: return getResources().getColor(R.color.unassigned);
        }

        return getResources().getColor(R.color.primaryTextDark);
    }

    private int defaultColorForItem(){
        return getResources().getColor(R.color.primaryTextDark);
    }

    private void updateEmptyView(boolean isEmpty) {
        if (emptyView == null) return;
        if(isEmpty) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }


    /**
     *  onClickListener class for FloatingActionButton
     */
    private class FloatingActionButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            LinearLayout addBox = (LinearLayout) findViewById(R.id.add_box);
            editText = (EditText) findViewById(R.id.task_name_edittext);

            if(toolbar.getTitle().toString().equals("Inbox")) {
                if(showingInput) {
                    addBox.setVisibility(View.GONE);
                    actionButton.setImageResource(R.drawable.ic_add_white_24dp);
                } else {
                    addBox.setVisibility(View.VISIBLE);
                    actionButton.setImageResource(R.drawable.ic_close_white_24dp);
                    editText.requestFocus();
                }
            } else {
                if(showingInput) {
                    addBox.setVisibility(View.GONE);
                    actionButton.setImageResource(R.drawable.ic_add_grey600_24dp);
                } else {
                    addBox.setVisibility(View.VISIBLE);
                    actionButton.setImageResource(R.drawable.ic_close_grey600_24dp);
                    editText.requestFocus();
                }
            }

            showingInput = !showingInput;
        }
    }

    /**
     *  onItemClickListener class for Nav Drawer's ListView
     */
    private class DrawerListListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectPosition(position);
        }
    }

    /**
     *  SwipeListener for refresh layout
     */
    private class SwipeListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            loadFromParse();
        }
    }

    /**
     *  EditorListener class for the entry field
     */
    private class EditorListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            if(actionId == EditorInfo.IME_ACTION_DONE) {
                if(v.getText().toString().length() > 0) {
                    ParseTask task = new ParseTask(v.getText().toString());
                    saveNewTask(task);
                    if(emptyView != null) emptyView.setVisibility(View.GONE);

                    v.setText("");

                    LinearLayout decoy  = (LinearLayout) findViewById(R.id.decoy);
                    decoy.requestFocus();

                    return true;
                }
            }

            return false;
        }
    }

    public void saveTask(ParseTask task) {
        task.pinInBackground(TASKS_LABEL, new TaskSaveListener(task));
    }

    private void saveNewTask(ParseTask task) {

        // Special task circumstances
        if(selectedPosition == 1) {
            task.setDeadline(new GregorianCalendar());
        } else if (selectedPosition == 2) {
            GregorianCalendar c = new GregorianCalendar();
            c.add(Calendar.DATE, 1);
            task.setDeadline(c);
        }
        if(selectedPosition == 3) {
            task.setDone(true);
        }

        task.pinInBackground(TASKS_LABEL, new TaskSaveListener(task));
        mParseAdapter.addElement(task);

    }

    public void saveList(ParseList list) {
        list.pinInBackground(LISTS_LABEL, new ListSaveListener(list));
        setTitle(list.toString());
    }

    private void saveNewList(ParseList list) {
        ParseList parseList = localListQueryByName(list.getName());
        if(parseList == null) {
            list.pinInBackground(LISTS_LABEL, new ListSaveListener(list));
        } else {
            Toast.makeText(this, "List " + list.getName() + " already exists.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public class TaskSaveListener implements SaveCallback {

        ParseTask task;
        public TaskSaveListener(ParseTask task) {
            this.task = task;
        }

        @Override
        public void done(ParseException e) {
            if (isFinishing()) {
                return;
            }
            if (e == null) {
                //Toast.makeText(Dashboard.this, "Task saved.", Toast.LENGTH_SHORT).show();

                task.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d("TaskSaveListener", e.getMessage());
                        } else {
                            Log.d("saveAllPinstoParse", "Uploaded " + task.toString());
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(),
                        "Error saving: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private class ListSaveListener implements SaveCallback {

        ParseList list;
        public ListSaveListener(ParseList list) {
            this.list = list;
        }

        @Override
        public void done(ParseException e) {
            refreshListTitles();
            if (isFinishing()) {
                return;
            }
            if(e == null) {
                list.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("saveAllPinstoParse", "Uploaded " + list.toString());
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(),
                        "Error saving: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void receiveDate(int year, int month, int day) {

        if(editText == null) {
            return;
        }

        String name = editText.getText().toString();
        if (name.length() > 0) {
            /*mAdapter.addElement(taskDataSource.createTask(editText.getText().toString(),
                    TaskDataSource.dateToString(temp)));*/
            ParseTask task = new ParseTask(editText.getText().toString());
            task.setDeadline(year, month, day);
            saveNewTask(task);
            editText.setText("");
        } else {
            Toast.makeText(this, "Please provide a name before choosing a date.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void loadFromParse() {

        // query tasks
        ParseQuery<ParseTask> query = ParseTask.getQuery();
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.orderByAscending("createdAt");

        // Find our tasks from the server
        query.findInBackground(new FindCallback<ParseTask>() {
            public void done(final List<ParseTask> ParseTasks, ParseException e) {

                // If no error
                if (e == null) {
                    // Remove all the previosuly pinned tasks before adding the new ones
                    ParseObject.unpinAllInBackground(TASKS_LABEL, new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                endRefresh();
                                return;
                            }

                            // Add all the new tasks we brought down from the server
                            ParseObject.pinAllInBackground(TASKS_LABEL, ParseTasks,
                                    new SaveCallback() {
                                        public void done(ParseException e) {
                                            if (e != null) {
                                                Log.i("ParseTaskListActivity",
                                                        "Error pinning ParseTasks: "
                                                                + e.getMessage());
                                                endRefresh();
                                            }
                                            // Do lists next
                                            loadListsFromParse();
                                        }
                                    });
                        }
                    });
                } else {
                    Log.i("ParseTaskListActivity",
                            "loadFromParse: Error finding pinned ParseTasks: "
                                    + e.getMessage());

                    endRefresh();
                }
            }
        });
    }

    private void loadListsFromParse() {
        // query lists
        ParseQuery<ParseList> query = ParseList.getQuery();
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseList>() {
            @Override
            public void done(final List<ParseList> list, ParseException e) {
                // If no error
                if (e == null) {
                    // Remove all the previosuly pinned tasks before adding the new ones
                    ParseObject.unpinAllInBackground(LISTS_LABEL, new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                endRefresh();
                                return;
                            }

                            // Add all the new tasks we brought down from the server
                            ParseObject.pinAllInBackground(LISTS_LABEL, list,
                                    new SaveCallback() {
                                        public void done(ParseException e) {
                                            if (e != null) {
                                                Log.i("ParseList Query",
                                                        "Error pinning ParseLists: "
                                                                + e.getMessage());
                                            }

                                            endRefresh();
                                            refreshListTitles();
                                        }
                                    });
                        }
                    });
                } else {
                    Log.i("ParseList",
                            "loadFromParse: Error finding pinned ParseTasks: "
                                    + e.getMessage());
                    endRefresh();
                }
            }
        });
    }

    private void endRefresh() {
        // Finally done
        mSwipeRefreshLayout.setRefreshing(false);
        selectPosition(selectedPosition);
    }


    private void loadFromLocal(ParseQuery<ParseTask> query) {
        //ParseQuery<ParseTask> query = ParseQuery.getQuery("Task");
        query.fromLocalDatastore();
        query.orderByAscending("createdAt");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseTask>() {
            @Override
            public void done(List<ParseTask> list, ParseException e) {
                if(e == null) {
                    mParseAdapter.replaceData(list);
                    updateEmptyView(list.isEmpty());
                } else {
                    Log.e("ParseQuery", "Error:" + e.getMessage());
                }
            }
        });
    }
}
