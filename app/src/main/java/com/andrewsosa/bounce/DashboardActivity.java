package com.andrewsosa.bounce;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;


public class DashboardActivity extends BounceActivity implements Toolbar.OnMenuItemClickListener,
        DatePickerReceiver, DashboardFragment.OnTaskInteractionListener {

    String TASKS_LABEL = "tasks";
    String LISTS_LABEL = "lists";

    static final int TODAY = 1;
    static final int UPCOMING = 2;
    static final int OVERDUE = 3;
    static final int COMPLETED = 4;
    static final int ALL_TASKS = 5;
    static final int UNASSIGNED = 6;
    static final int DIVIDER = 7;

    // Toggle view for the add menu
    private boolean showingInput = false;

    // UI Components
    ListView drawerList;
    ArrayAdapter<ParseTaskList> drawerListAdapter;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    FloatingActionButton actionButton;
    static EditText editText;


    // Reference to active fragment
    DashboardFragment activeFragment;

    // Data for menu navigation
    Integer selectedPosition = 1;
    ArrayList<String> titles;
    String[] presetTitles = {
            "Bounce",
            "Today",
            "Upcoming",
            "Overdue",
            "Completed",
            "All Tasks",
            "Unassigned",
            "Divider"
    };

    // State list for FAB
    int[][] states = new int[][] {
            new int[] { android.R.attr.state_enabled}, // enabled
            new int[] {-android.R.attr.state_enabled}, // disabled
            new int[] {-android.R.attr.state_checked}, // unchecked
            new int[] { android.R.attr.state_pressed}  // pressed
    };
    int[] colors = new int[] {
            Color.WHITE,
            Color.WHITE,
            Color.WHITE,
            Color.WHITE
    };
    ColorStateList fabStates = new ColorStateList(states, colors);


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
        toolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        // Drawer craziness
        drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        drawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.primaryDark));

        // Add button stuff
        actionButton = (FloatingActionButton) findViewById(R.id.add_button);
        actionButton.setOnClickListener(new FloatingActionButtonListener());
        actionButton.setOnLongClickListener(new FloatingActionButtonListener());
        actionButton.setBackgroundTintList(fabStates);

        // OLD RECYCLER VIEW STUFF WENT HERE

        // Drawer List things
        drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerListAdapter = new ArrayAdapter<>(this,
                R.layout.drawer_item_view,
                R.id.list_name,
                new ArrayList<ParseTaskList>());
        drawerList.setAdapter(drawerListAdapter);
        refreshListTitles();
        drawerList.setOnItemClickListener(new DrawerListListener());

        // Add Extra views to ListView
        addExtraViews();

        // Recover from rotates
        if (savedInstanceState != null) {
            selectPosition(savedInstanceState.getInt("selectedPosition"));
        } else {
            // Select either the default item (0) or the last selected item.
            selectPosition(1);
            //drawerList.setItemChecked(1, true);
        }

        // For Settings
        RelativeLayout settings = (RelativeLayout) findViewById(R.id.settings_layout);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
            }
        });

        // For About
        RelativeLayout about = (RelativeLayout) findViewById(R.id.about_layout);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAboutDialog();
            }
        });

        // Things for adding tasks
        EditText editText = (EditText) findViewById(R.id.task_name_edittext);
        editText.setOnEditorActionListener(new EditorListener());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("Bounce", "On Activity Result");

        if(activeFragment != null) {
            switch (resultCode) {
                case RESULT_OK:
                    activeFragment.doQuery();
                    break;
                case RESULT_DELETE_TASK:
                    activeFragment.relayAdapter().removeActiveElement();
                    break;
                case RESULT_MISSING_TASK:
                    activeFragment.doQuery();
                    break;
                default:
                    activeFragment.doQuery();
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryTasksFromServer();
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
            //case R.id.action_search_list:
            //    return true;
            case R.id.action_rename_list:
                renameListDialog();
                return true;
            case R.id.action_delete_list:
                deleteListDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("selectedPosition", selectedPosition);
    }

    public TaskRecyclerAdapterBase getActiveAdapter() {
        return activeFragment.relayAdapter();
    }

    public void deleteList(int position) {

        // Locate list
        String name = getTitle(position);
        ParseTaskList list = localListQueryByName(name);

        // Remove tasks on list
        ParseQuery<ParseTask> query = ParseTask.getQuery();
        query.whereEqualTo("user", ParseUser.getCurrentUser());
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

    public static ParseTaskList localListQueryByName(String name) {
        ParseQuery<ParseTaskList> query = ParseTaskList.getQuery();
        query.fromLocalDatastore();
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.whereEqualTo("name", name);
        ParseTaskList list = null;
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
                .negativeColor(getResources().getColor(R.color.secondaryTextDark))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (nameInput != null) {
                            ParseTaskList parseList = new ParseTaskList(nameInput.getText().toString());
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
                .negativeColor(getResources().getColor(R.color.secondaryTextDark))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (nameInput != null) {
                            ParseTaskList parseList = localListQueryByName(getTitle(selectedPosition));
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
                .negativeColor(getResources().getColor(R.color.secondaryTextDark))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        deleteList(selectedPosition);
                    }
                })
                .show();
    }

    private void displayAboutDialog() {
        new MaterialDialog.Builder(this)
                .title("About Bounce")
                .content(R.string.about_content)
                .positiveText("Done")
                .icon(getResources().getDrawable(R.mipmap.ic_launcher))
                .show();
    }

    private void setTitle(String title) {
        toolbar.setTitle(title);
    }

    private void refreshListTitles() {
        initTitleList();
        //addParseListTitles();
    }

    private ArrayList<String> initTitleList() {
        titles = new ArrayList<>();
        boolean temp = true;
        if(temp) titles.add(presetTitles[TODAY]);
        if(temp) titles.add(presetTitles[UPCOMING]);
        if(temp) titles.add(presetTitles[OVERDUE]);
        if(temp) titles.add(presetTitles[DIVIDER]);
        if(temp) titles.add(presetTitles[COMPLETED]);
        if(temp) titles.add(presetTitles[ALL_TASKS]);
        if(temp) titles.add(presetTitles[UNASSIGNED]);
        if(temp) titles.add(presetTitles[DIVIDER]);

        return titles;
    }

    private void addParseListTitles() {

        ParseQuery<ParseTaskList> listQuery = ParseTaskList.getQuery();
        listQuery.fromLocalDatastore();
        listQuery.orderByAscending("createdAt");
        listQuery.findInBackground(new FindCallback<ParseTaskList>() {
            @Override
            public void done(List<ParseTaskList> parseLists, ParseException e) {
                if (e == null) {

                    for (ParseTaskList l : parseLists) {
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

    private int tagFromPosition(int position) {

        try {
            switch (titles.get(position - 1)) {
                case "Today":
                    return TODAY;
                case "Upcoming":
                    return UPCOMING;
                case "Overdue":
                    return OVERDUE;
                case "Completed":
                    return COMPLETED;
                case "All Tasks":
                    return ALL_TASKS;
                case "Unassigned":
                    return UNASSIGNED;
                default:
                    return 0;
            }
        } catch (Exception e) {
            Log.e("tagFromPosition", e.getMessage());
            return 0;
        }
    }

    private void selectPosition(int position) {

        // Mark new position as selected
        selectedPosition = position;
        drawerList.setItemChecked(position, true);

        // Update Data
        //loadQueryToDisplay(prepareDataQuery(position), useSmallTiles(position));
        if(!handleFragmentTransaction(tagFromPosition(position))) {
            if(activeFragment != null) activeFragment.doQuery();
        }

        // Update UI components to match selection
        setTitle(getTitle(position)); // header off-by-one issue
        updateToolbarMenu(position);
        drawerLayout.closeDrawer(findViewById(R.id.scrimInsetsFrameLayout));

    }

    private boolean handleFragmentTransaction(int tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Check if we already have an active fragment
        Fragment existingFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (existingFragment == null || !existingFragment.getTag().equals(getFragmentTagByActivityTag(tag)))
        {
            Log.d("handleFragTransaction", "Replacing with new fragment");

            // I guess we need to add a new fragment
            activeFragment = DashboardFragment.newInstance(getFragmentTagByActivityTag(tag),
                    prepareDataQuery(tag));

            // Display the fragment as the main content.
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, activeFragment, getFragmentTagByActivityTag(tag))
                    .commit();

            return true;
        }

        else return false;
    }

    private String getFragmentTagByActivityTag(int position) {
        switch(position) {
            case TODAY:         return DashboardFragment.TODAY;
            case UPCOMING:      return DashboardFragment.UPCOMING;
            case OVERDUE:       return DashboardFragment.OVERDUE;
            case COMPLETED:     return DashboardFragment.COMPLETED;
            case ALL_TASKS:     return DashboardFragment.ALL_TASKS;
            case UNASSIGNED:    return DashboardFragment.UNASSIGNED;
            default:            return DashboardFragment.OTHER_LIST;

        }
    }

    private String getTitle(int position) {
        try {
            return titles.get(position - 1);
        } catch (IndexOutOfBoundsException e) {
            Log.e("getTitle", e.getMessage());
            return "Bounce";
        }
    }

    private void updateToolbarMenu(int position) {
        toolbar.getMenu().clear();
        if(position <= titles.indexOf("Unassigned")) {
            toolbar.inflateMenu(R.menu.menu_dashboard);
        } else {
            toolbar.inflateMenu(R.menu.menu_list);
        }
    }

    private HashMap<String, Integer> prepareListIcons() {
        HashMap<String, Integer> icons = new HashMap<>();


        //icons.put("Today", R.drawable.ic_drawer_inbox);
        //icons.put("Upcoming", R.drawable.ic_drawer_upcoming);
        //icons.put("Overdue", R.drawable.ic_drawer_overdue);
        //icons.put("Completed", R.drawable.ic_drawer_completed);
        //icons.put("All Tasks", R.drawable.ic_drawer_all_tasks);
        //icons.put("Unassigned", R.drawable.ic_drawer_unassigned);
        return icons;

    }

    private void updateUIcolors(int position){
        toolbar.setBackgroundColor(getToolbarColor(position));
        drawerLayout.setStatusBarBackgroundColor(getStatusbarColor(position));
        updateActionButton(position);

    }

    public int getCurrentToolbarColor() {
        return getToolbarColor(1);
    }

    public int getCurrentStatusbarColor() {
        return getStatusbarColor(1);
    }

    private int getToolbarColor(int position){
        switch(position) {
            case 1: return getResources().getColor(R.color.inbox);
            case 2: return getResources().getColor(R.color.upcoming);
            case 3: return getResources().getColor(R.color.completed);
            case 4: return getResources().getColor(R.color.alltasks);
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
            actionButton.setBackgroundColor(getResources().getColor(R.color.accent));
        } else {
            actionButton.setBackgroundColor(getResources().getColor(R.color.background));
        }

    }

    private ParseQuery<ParseTask> prepareDataQuery(int tag) {
        Log.d("prepareDataQuery", "Preparing query for tag " + tag);

        // Get the basic query
        ParseQuery<ParseTask> query = ParseTask.getQuery()
                .fromLocalDatastore()
                .whereEqualTo("user", ParseUser.getCurrentUser());

        // These handle time limits
        GregorianCalendar midnight = new GregorianCalendar();
        midnight.set(Calendar.HOUR_OF_DAY, 23);
        midnight.set(Calendar.MINUTE, 59);

        GregorianCalendar yesterday = new GregorianCalendar();
        yesterday.set(Calendar.HOUR_OF_DAY, 23);
        yesterday.set(Calendar.MINUTE, 59);
        yesterday.add(Calendar.DAY_OF_MONTH, -1);

        // Handle special case for 'Today'
        if(tag == TODAY) {
            ParseQuery<ParseTask> overdue = ParseTask.getQuery()
                    .fromLocalDatastore()
                    .whereEqualTo("user", ParseUser.getCurrentUser())
                    .whereLessThanOrEqualTo("deadline", midnight.getTime())
                    .whereEqualTo("done", false);
            ParseQuery<ParseTask> current = ParseTask.getQuery()
                    .fromLocalDatastore()
                    .whereEqualTo("user", ParseUser.getCurrentUser())
                    .whereGreaterThan("deadline", yesterday.getTime())
                    .whereLessThanOrEqualTo("deadline", midnight.getTime());
            List<ParseQuery<ParseTask>> queries = new ArrayList<>();
            queries.add(overdue);
            queries.add(current);
            return ParseQuery.or(queries)
                        .orderByAscending("done")
                        .addDescendingOrder("timeSpecified")
                        .addDescendingOrder("comparableTime")
                        .addAscendingOrder("deadline");
        }

        // Add not-done requirement
        //query.whereEqualTo("done", false);

        // Handle non-1 cases
        switch(tag) {
            case UPCOMING: return query.whereGreaterThan("deadline", midnight.getTime());
            case OVERDUE: return query.whereLessThanOrEqualTo("deadline", yesterday.getTime())
                    .whereEqualTo("done", false);
            case COMPLETED: return query.whereEqualTo("done", true);
                        //.whereLessThanOrEqualTo("deadline", yesterday.getTime());
            case ALL_TASKS: return query.orderByDescending("deadline");
            case UNASSIGNED: return query.whereEqualTo("parent", null)
                        .orderByDescending("deadline")
                        .whereEqualTo("done", false);
            default: ParseTaskList parseList = localListQueryByName(getTitle(tag));
                return query.whereEqualTo("parent", parseList)
                        .whereEqualTo("done", false);
        }
    }

    private void addExtraViews() {

        HashMap<String, Integer> icons = prepareListIcons();

        // Header view
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.drawer_header, drawerList, false);
        displayUserData(header);
        drawerList.addHeaderView(header, null, false);

        // Add the presets
        for(String s : titles) {
            if(!s.equals("Divider")) {
                ViewGroup preset = (ViewGroup) inflater.inflate(R.layout.drawer_item_view, drawerList, false);
                ImageView icon = (ImageView) preset.findViewById(R.id.list_icon);
                TextView name = (TextView) preset.findViewById(R.id.list_name);
                icon.setImageDrawable(getResources().getDrawable(icons.get(s)));
                name.setText(s);
                name.setTextColor(makeColorStateListForItem(1));
                drawerList.addHeaderView(preset, null, true);
            } else {
                ViewGroup divider = (ViewGroup) inflater.inflate(R.layout.drawer_divider, drawerList, false);
                drawerList.addHeaderView(divider, null, false);
            }
        }


        // Add footer divider
        //ViewGroup divider2 = (ViewGroup) inflater.inflate(R.layout.drawer_divider, drawerList, false);
        //drawerList.addFooterView(divider2, null, false);

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
        /*switch(position) {
            case 1: return getResources().getColor(R.color.inbox);
            case 2: return getResources().getColor(R.color.upcoming);
            case 3: return getResources().getColor(R.color.completed);
            case 4: return getResources().getColor(R.color.alltasks);
            case 5: return getResources().getColor(R.color.unassigned);
        }

        return getResources().getColor(R.color.primaryTextDark);*/
        return getResources().getColor(R.color.primary);
    }

    private int checkedColorForItem(int position){
        /*switch(position) {
            case 1: return getResources().getColor(R.color.inbox);
            case 2: return getResources().getColor(R.color.upcoming);
            case 3: return getResources().getColor(R.color.completed);
            case 4: return getResources().getColor(R.color.alltasks);
            case 5: return getResources().getColor(R.color.unassigned);
        }

        return getResources().getColor(R.color.primaryTextDark);*/
        return getResources().getColor(R.color.primary);
    }

    private int defaultColorForItem(){
        return getResources().getColor(R.color.primaryTextDark);
    }

    /**
     *  onClickListener class for FloatingActionButton
     */
    private class FloatingActionButtonListener implements View.OnClickListener, View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {

            LinearLayout addBox = (LinearLayout) findViewById(R.id.add_box);
            editText = (EditText) findViewById(R.id.task_name_edittext);

            if(toolbar.getTitle().toString().equals("Inbox")) {
                if(showingInput) {
                    addBox.setVisibility(View.GONE);
                    //actionButton.setImageResource(R.drawable.ic_add_grey600_24dp);
                } else {
                    addBox.setVisibility(View.VISIBLE);
                    //actionButton.setImageResource(R.drawable.ic_close_grey600_24dp);
                    editText.requestFocus();
                }
            } else {
                if(showingInput) {
                    addBox.setVisibility(View.GONE);
                    //actionButton.setImageResource(R.drawable.ic_add_grey600_24dp);
                } else {
                    addBox.setVisibility(View.VISIBLE);
                    //actionButton.setImageResource(R.drawable.ic_close_grey600_24dp);
                    editText.requestFocus();
                }
            }

            showingInput = !showingInput;

            return true;
        }

        @Override
        public void onClick(View v) {

            if(showingInput) {
                onLongClick(v);
            } else {
                ParseTask parseTask = new ParseTask("New ParseTask");
                saveNewTask(parseTask);
                launchActivityFromTask(parseTask);
            }
        }
    }

    public void onRefresh() {
        queryTasksFromServer();
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
     *  EditorListener class for the entry field
     */
    private class EditorListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            if(actionId == EditorInfo.IME_ACTION_DONE) {
                if(v.getText().toString().length() > 0) {
                    ParseTask parseTask = new ParseTask(v.getText().toString());
                    saveNewTask(parseTask);

                    v.setText("");

                    toolbar.requestFocus();

                    return true;
                }
            }

            return false;
        }
    }

    public void saveTask(ParseTask parseTask) {
        parseTask.pinInBackground(TASKS_LABEL, new TaskSaveListener(parseTask));
    }

    private void saveNewTask(ParseTask parseTask) {

        // Special parseTask circumstances
        if(selectedPosition == TODAY) {
            parseTask.setDeadline(new GregorianCalendar());
        } else if (selectedPosition == UPCOMING) {
            GregorianCalendar c = new GregorianCalendar();
            c.add(Calendar.DATE, 1);
            parseTask.setDeadline(c);
        } else if(selectedPosition == COMPLETED) {
            parseTask.setDone(true);
        } else if(selectedPosition == OVERDUE) {
            GregorianCalendar c = new GregorianCalendar();
            c.add(Calendar.DATE, -1);
            parseTask.setDeadline(c);
        }

        Log.d("saveNewTask", parseTask.getFullDeadline());
        parseTask.pinInBackground(TASKS_LABEL, new TaskSaveListener(parseTask));

        if(getActiveAdapter() != null) getActiveAdapter().addElement(parseTask);

        if(getActiveAdapter() != null && getActiveAdapter().getItemCount() == 1 && activeFragment != null) {
            activeFragment.showEmptyView(false);
        }

    }

    public void saveList(ParseTaskList list) {
        list.pinInBackground(LISTS_LABEL, new ListSaveListener(list));
        setTitle(list.toString());
    }

    private void saveNewList(ParseTaskList list) {
        ParseTaskList parseList = localListQueryByName(list.getName());
        if(parseList == null) {
            list.pinInBackground(LISTS_LABEL, new ListSaveListener(list));
        } else {
            Toast.makeText(this, "List " + list.getName() + " already exists.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public class TaskSaveListener implements SaveCallback {

        ParseTask parseTask;
        public TaskSaveListener(ParseTask parseTask) {
            this.parseTask = parseTask;
        }

        @Override
        public void done(ParseException e) {
            if (isFinishing()) {
                return;
            }
            if (e == null) {
                //Toast.makeText(DashboardActivity.this, "ParseTask saved.", Toast.LENGTH_SHORT).show();

                parseTask.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d("TaskSaveListener", e.getMessage());
                        } else {
                            Log.d("saveAllPinstoParse", "Uploaded " + parseTask.toString());
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

        ParseTaskList list;
        public ListSaveListener(ParseTaskList list) {
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
            ParseTask parseTask = new ParseTask(editText.getText().toString());
            parseTask.setDeadline(year, month, day);
            saveNewTask(parseTask);
            editText.setText("");
        } else {
            Toast.makeText(this, "Please provide a name before choosing a date.", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void launchActivityFromTask(ParseTask parseTask) {

        Intent intent = new Intent(this, TaskViewActivity.class);
        intent.putExtra("TaskID", parseTask.getId());

        this.startActivityForResult(intent, 1);

    }

    private void queryTasksFromServer() {

        // query tasks
        ParseQuery<ParseTask> query = ParseTask.getQuery();
        query.whereEqualTo("user", ParseUser.getCurrentUser());

        // Find our tasks from the server
        query.findInBackground(new FindCallback<ParseTask>() {
            public void done(final List<ParseTask> parseTasks, ParseException e) {

                // If no error
                if (e == null) {

                    // Add all the new parseTasks we brought down from the server
                    ParseObject.pinAllInBackground(TASKS_LABEL, parseTasks,
                            new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e != null) {
                                        Log.i("queryTasksFromServer()", "Error pinning parseTasks" + e.getMessage());
                                    }

                                    // End the visible refresh, then do lists
                                    endRefresh();
                                    queryListsFromServer();
                                }
                            });

                } else {
                    Log.i("queryTasksFromServer()", "Error querying server" + e.getMessage());
                    endRefresh();
                }
            }
        });
    }

    private void queryListsFromServer() {
        // query lists
        ParseQuery<ParseTaskList> query = ParseTaskList.getQuery();
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseTaskList>() {
            @Override
            public void done(final List<ParseTaskList> list, ParseException e) {
                // If no error
                if (e == null) {


                    // Add all the new tasks we brought down from the server
                    ParseObject.pinAllInBackground(LISTS_LABEL, list,
                            new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e != null) {
                                        Log.i("ParseTaskList Query",
                                                "Error pinning ParseLists: "
                                                        + e.getMessage());
                                    }

                                    endRefresh();
                                    refreshListTitles();
                                }
                            });

                } else {
                    Log.i("ParseTaskList",
                            "queryTasksFromServer: Error finding pinned ParseTasks: "
                                    + e.getMessage());
                    endRefresh();
                }
            }
        });
    }

    private void endRefresh() {
        // Finally done
        if(activeFragment != null) {
            activeFragment.relaySwipeLayout().setRefreshing(false);
            activeFragment.doQuery();
        }

    }
}
