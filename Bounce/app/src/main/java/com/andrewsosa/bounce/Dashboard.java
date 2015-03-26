package com.andrewsosa.bounce;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;

import java.util.GregorianCalendar;


public class Dashboard extends Activity implements Toolbar.OnMenuItemClickListener,
        DatePickerReceiver {


    // Actionbar and Navdrawer nonsense
    ActionBarDrawerToggle mDrawerToggle;

    // Recyclerview things
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private static TaskRecyclerAdapter mAdapter;
    static EditText editText;

    // Data sources
    private static TaskDataSource taskDataSource;
    private static ListDataSource listDataSource;

    // Temp holder for a picked date
    private int tempDay;
    private int tempMonth;
    private int tempYear;

    // Toggle view for the add menu
    private boolean showingInput = false;

    // UI Components
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    FloatingActionButton actionButton;


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

        // Open datasources
        initDatasources();

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
        mAdapter = new TaskRecyclerAdapter(taskDataSource.getAllTasks(), this);
        mRecyclerView.setAdapter(mAdapter);

        // Assemble Cursor for ListView
        Cursor c = assembleCursor(listDataSource.getListCursor());

        // Load the lists
        ListView listView = (ListView) findViewById(R.id.drawer_list);
        listView.setAdapter(new DrawerListAdapter(this,
                R.layout.drawer_item_view,
                assembleCursor(listDataSource.getListCursor()),
                prepareListIcons(),
                new String[]{ListOpenHelper.COLUMN_NAME},
                new int[]{R.id.list_name},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
        listView.setOnItemClickListener(new DrawerListListener());

        // Header view
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.drawer_header, listView, false);
        listView.addHeaderView(header, null, false);

        // Footer view
        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.drawer_footer, listView, false);
        listView.addFooterView(footer, null, false);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewListDialog();
            }
        });

        listView.setItemChecked(1, true);
        setTitle("Inbox");

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
                mAdapter.removeActiveElement();
            }
            else {
                Task temp = taskDataSource.getTask(mAdapter.getActiveItem().getId());
                mAdapter.changeElement(mAdapter.getActiveItemNumber(), temp);
                mAdapter.notifyItemChanged(mAdapter.getActiveItemNumber());
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Toast.makeText(this, "Hello, world!", Toast.LENGTH_SHORT).show();
            //taskDataSource.deleteTask(mAdapter.getItem(0));
            //mAdapter.removeElementAt(0);
            return true;
        }
        if (id == R.id.action_create_list) {
            createNewListDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    public void receiveDate(int year, int month, int day) {

        GregorianCalendar temp = new GregorianCalendar(year, month, day);
        Log.d("Bounce", "The date created was: " + TaskDataSource.dateToString(temp));

        if (editText.getText() != null) {
            mAdapter.addElement(taskDataSource.createTask(editText.getText().toString(),
                    TaskDataSource.dateToString(temp)));
        }
    }

    private EditText nameInput;
    private View positiveAction;
    private void createNewListDialog() {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .customView(R.layout.create_dialog_view, true)
                .title("List Name")
                .positiveText("Create")
                .negativeText(android.R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (nameInput != null) {
                            //Toast.makeText(getApplicationContext(), "Password: " + nameInput.getText().toString(), Toast.LENGTH_SHORT).show();
                            listDataSource.createList(nameInput.getText().toString());
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

    private Cursor assembleCursor(Cursor cursorFromDatabase) {

        String[] columns = new String[] {
                ListOpenHelper.COLUMN_ID,
                ListOpenHelper.COLUMN_NAME
        };
        MatrixCursor matrixCursor= new MatrixCursor(columns);
        matrixCursor.addRow(new Object[]{-1, "Inbox"});
        matrixCursor.addRow(new Object[]{-1, "Upcoming"});
        matrixCursor.addRow(new Object[]{-1, "All Tasks"});
        matrixCursor.addRow(new Object[]{-1, "Completed"});
        matrixCursor.addRow(new Object[]{-1, "Unassigned"});

        return new MergeCursor(new Cursor[]{matrixCursor, cursorFromDatabase});

    }

    private int[] prepareListIcons() {
        return new int[] {
                R.drawable.ic_inbox_color,
                R.drawable.ic_upcoming_color,
                R.drawable.ic_alltasks_color,
                R.drawable.ic_completed_color,
                R.drawable.ic_unassigned_color
        };
    }

    private void updateUIcolors(int position){
        toolbar.setBackgroundColor(getToolbarColor(position));
        drawerLayout.setStatusBarBackgroundColor(getStatusbarColor(position));
        updateActionButton(position);
    }

    private int getToolbarColor(int position){
        int i = position - 1;
        switch(i) {
            case 0: return getResources().getColor(R.color.inbox);
            case 1: return getResources().getColor(R.color.upcoming);
            case 2: return getResources().getColor(R.color.alltasks);
            case 3: return getResources().getColor(R.color.completed);
            case 4: return getResources().getColor(R.color.unassigned);
        }

        return getResources().getColor(R.color.unassigned);
    }

    private int getStatusbarColor(int position){
        int i = position - 1;
        switch(i) {
            case 0: return getResources().getColor(R.color.inboxDark);
            case 1: return getResources().getColor(R.color.upcomingDark);
            case 2: return getResources().getColor(R.color.alltasksDark);
            case 3: return getResources().getColor(R.color.completedDark);
            case 4: return getResources().getColor(R.color.unassignedDark);
        }

        return getResources().getColor(R.color.unassignedDark);
    }

    private void updateActionButton(int position){
        int i = position - 1;
        if(i == 0) {
            actionButton.setBackgroundColor(getResources().getColor(R.color.accentColor));
            actionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
        } else {
            actionButton.setBackgroundColor(getResources().getColor(R.color.windowBackground));
            actionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_grey600_24dp));
        }

    }

    private void setTitle(String title) {
        toolbar.setTitle(title);
    }


    private void initDatasources() {
        taskDataSource = new TaskDataSource(this);
        taskDataSource.open();
        listDataSource = new ListDataSource(this);
        listDataSource.open();
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
            Toast.makeText(Dashboard.this, "Clicked position " + position, Toast.LENGTH_SHORT)
                    .show();

            setTitle(((TextView) view.findViewById(R.id.list_name)).getText().toString());
            updateUIcolors(position);
            drawerLayout.closeDrawer(findViewById(R.id.scrimInsetsFrameLayout));
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
                    mAdapter.addElement(taskDataSource.createTask(v.getText().toString()));
                    v.setText("");

                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    LinearLayout addBox = (LinearLayout) findViewById(R.id.add_box);
                    addBox.setVisibility(View.GONE);

                    LinearLayout decoy  = (LinearLayout) findViewById(R.id.decoy);
                    decoy.requestFocus();

                    actionButton.show();
                    //actionButton.setVisibility(View.VISIBLE);

                    return true;
                }
            }

            return false;
        }
    }

}
