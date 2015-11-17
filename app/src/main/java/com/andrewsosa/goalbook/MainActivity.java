package com.andrewsosa.goalbook;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.Firebase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ListFragment.TaskInteractionListener,
        Toolbar.OnMenuItemClickListener, PagerFragment.PagerFragmentInteractionListener{

    // Navigation fragment tags
    final static String GOALS = "goals";
    final static String NOTIFACTIONS = "notifications";
    final static String ARCHIVE = "archive";

    DrawerLayout drawerLayout;
    Toolbar mToolbar;
    TabLayout mTabs;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        mToolbar.setTitle("Goalbook");
        mToolbar.inflateMenu(R.menu.main);
        mToolbar.setOnMenuItemClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uid = getSharedPreferences().getString(Goalbook.UID, "");
                Firebase ref = new Firebase(Goalbook.URL);
                Firebase userRef = ref.child("users").child(uid);
                Firebase tasksRef = userRef.child("tasks");

                String key = tasksRef.push().getKey();
                Goal t = new Goal(key, "New Task");
                tasksRef.child(key).setValue(t);


                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                intent.putExtra("key", key);
                intent.putExtra("mode", TaskActivity.CREATE);
                startActivity(intent);
            }
        });

        mTabs = (TabLayout) findViewById(R.id.tabs);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_goalbook);
        performFragmentTransaction(R.id.nav_goalbook);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {


        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        performFragmentTransaction(id);
        updateUI(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
     *  Custom Methods
     */

    public boolean performFragmentTransaction(int id) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        String tag = null;

        switch (id) {
            case R.id.nav_goalbook:
                fragment = PagerFragment.newInstance();
                tag = GOALS;
                break;
            case R.id.nav_reminders:
        }

        // Confirm transaction
        if(fragment == null || (fragmentManager.findFragmentByTag(tag) != null)) return false;

        // JUST DO IT
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_anchor, fragment, tag)
                .commit();

        return true;
    }

    public void updateUI(int id) {
        switch (id) {
            case R.id.nav_goalbook:
                showTabs(true);
                break;
            case R.id.nav_reminders:
                showTabs(false);
                break;
            default:

        }
    }

    public void showTabs(boolean show) {
        if(show && mTabs.getVisibility() != View.VISIBLE) mTabs.setVisibility(View.VISIBLE);
        else if(!show && mTabs.getVisibility() != View.GONE) mTabs.setVisibility(View.GONE);
    }


    /*
     *  Interface Methods
     */

    @Override
    public void onTaskClick(String key) {
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra("key", key);
        intent.putExtra("mode", TaskActivity.EDIT);
        startActivity(intent);
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(Goalbook.PREFS, MODE_PRIVATE);
    }

    @Override
    public TabLayout getTabs() {
        return mTabs;
    }
}
