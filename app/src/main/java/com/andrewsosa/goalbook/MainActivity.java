package com.andrewsosa.goalbook;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import android.view.Window;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ListFragment.TaskInteractionListener,
        Toolbar.OnMenuItemClickListener, PagerFragment.PagerFragmentInteractionListener{

    // Navigation fragment tags
    final static String GOALS = "goals";
    final static String NOTIFACTIONS = "notifications";
    final static String ARCHIVE = "archive";

    // Views
    DrawerLayout drawerLayout;
    Toolbar mToolbar;
    TabLayout mTabs;
    FloatingActionButton fab;
    String activeMode = Goal.DAILY;

    // Firebase references data
    Firebase tasksRef;
    Firebase archiveRef;
    Firebase userRef;

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

        String uid = getSharedPreferences().getString(Goalbook.UID, "");

        Firebase ref = new Firebase(Goalbook.URL);
        userRef = ref.child("users").child(uid);
        tasksRef = ref.child("tasks").child(uid);
        archiveRef = ref.child("archive").child(uid);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String key = tasksRef.push().getKey();
                Goal t = new Goal(key, "New Goal");
                t.setPriority(activeMode);
                tasksRef.child(key).setValue(t);

                Intent i = new Intent(MainActivity.this, GoalActivity.class);
                i.putExtra("key", key);
                i.putExtra("mode", GoalActivity.CREATE);
                startActivity(i);

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

        if(id == R.id.nav_help || id == R.id.nav_settings) {
            Snackbar.make(mToolbar, "Coming soon!", Snackbar.LENGTH_LONG).show();
        }

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
                fragment = ListFragment.newInstance(ListFragment.NOTIFICATIONS);
                tag = NOTIFACTIONS;
                break;
            case R.id.nav_archive:
                fragment = ListFragment.newInstance(ListFragment.ARCHIVE);
                tag = ARCHIVE;
                break;
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
                fab.show();
                mToolbar.setTitle("Goals");
                break;
            case R.id.nav_reminders:
                showTabs(false);
                fab.hide();
                mToolbar.setTitle("Reminders");
                Snackbar.make(mToolbar, "Reminders coming soon!", Snackbar.LENGTH_LONG).show();
                break;
            case R.id.nav_archive:
                showTabs(false);
                fab.hide();
                mToolbar.setTitle("Archive");
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

        Intent i = new Intent(this, GoalActivity.class);
        i.putExtra("key", key);
        startActivity(i);

    }

    @Override
    public void onTaskLongClick(String key) {
        /*
        // Move from
        final Firebase ref = tasksRef.child(key);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Goal g = dataSnapshot.getValue(Goal.class);
                archiveRef.child(g.getUuid()).setValue(g);
                ref.removeValue();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        */
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences(Goalbook.PREFS, MODE_PRIVATE);
    }

    @Override
    public TabLayout getTabs() {
        return mTabs;
    }

    @Override
    public void updateActive(String mode) {
        activeMode = mode;
    }
}
