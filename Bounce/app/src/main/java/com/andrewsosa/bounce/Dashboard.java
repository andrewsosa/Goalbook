package com.andrewsosa.bounce;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
//import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.melnykov.fab.FloatingActionButton;

import java.util.Calendar;


public class Dashboard extends Activity implements Toolbar.OnMenuItemClickListener{


    // Actionbar and Navdrawer nonsense
    ActionBarDrawerToggle mDrawerToggle;

    // Recyclerview things
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private TaskRecyclerAdapter mAdapter;

    // Data sources
    private TaskDataSource dataSource;

    // Temp holder for a picked date
    private int tempDay;
    private int tempMonth;
    private int tempYear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Toolbar craziness
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Dashboard");
        toolbar.inflateMenu(R.menu.menu_dashboard);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setTitleTextColor(getResources().getColor(R.color.abc_primary_text_material_dark));

        // Other toolbar craziness
        Toolbar sidebar = (Toolbar) findViewById(R.id.nav_toolbar);
        sidebar.inflateMenu(R.menu.menu_nav_drawer);
        sidebar.setOnMenuItemClickListener(this);

        // Open datasource
        dataSource = new TaskDataSource(this);
        dataSource.open();

        // Drawer craziness
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(mDrawerToggle);

        // Add button stuff
        final FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.add_button);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout addBox = (LinearLayout) findViewById(R.id.add_box);

                if(addBox == null) {
                    Log.d("Bounce", "addBox's reference is null");
                } else {
                    addBox.setVisibility(View.VISIBLE);
                    Log.d("Bounce", "addBox's reference is NOT null");

                }

                EditText editText = (EditText) findViewById(R.id.task_name_edittext);
                editText.requestFocus();

                //actionButton.setVisibility(View.GONE);
                actionButton.hide();

            }
        });


        // Things for recyclerviews
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, null, false, true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter 
        mAdapter = new TaskRecyclerAdapter(dataSource.getAllTasks(), this);
        mRecyclerView.setAdapter(mAdapter);

        // Things for adding tasks
        EditText editText = (EditText) findViewById(R.id.task_name_edittext);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    if(v.getText().toString().length() > 0) {
                        mAdapter.addElement(dataSource.createTask(v.getText().toString()));
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
        });

        ImageButton calendarButton = (ImageButton) findViewById(R.id.calendar_button);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
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
            if(data.getStringExtra("Action").equals("delete")) {
                Log.d("Bounce", "Action == Delete");
                mAdapter.removeActiveElement();
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
            dataSource.deleteTask(mAdapter.getItem(0));
            mAdapter.removeElementAt(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void launchCreateDialog() {
        new MaterialDialog.Builder(this)
                .customView(R.layout.create_dialog_view, false)
                .positiveText("Create")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {

                        View view = dialog.getCustomView();
                        EditText editText = (EditText) view.findViewById(R.id.namingEditText);

                        if(editText == null) {
                            Log.d("Bounce", "edidtText = null");
                        }

                        if(editText != null && !editText.getText().toString().equals("")) {
                            Log.d("Bounce", "Adding Task called " + editText.getText().toString());
                            mAdapter.addElement(dataSource.createTask(editText.getText().toString()));

                        }

                    }
                })
                .build()
                .show();


    }


    // Inner class for date picker dialog
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {



        }
    }
}
