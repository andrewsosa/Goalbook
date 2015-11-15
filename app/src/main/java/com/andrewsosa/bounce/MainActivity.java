package com.andrewsosa.bounce;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
        Toolbar.OnMenuItemClickListener{

    DrawerLayout drawerLayout;
    Toolbar mToolbar;
    TabLayout mTabs;
    ViewPager mViewPager;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        /*mToolbar.setNavigationIcon(R.drawable.ic_menu_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });*/
        //mToolbar.setTitle("Bounce");
        //mToolbar.inflateMenu(R.menu.main);
        //mToolbar.setOnMenuItemClickListener(this);
        setSupportActionBar(mToolbar);
        setTitle("Bounce To-Do");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Firebase ref = new Firebase(Bounce.URL);
                Firebase tasksRef = ref.child("users").child(ref.getAuth().getUid()).child("tasks");

                String key = tasksRef.push().getKey();
                FirebaseTask t = new FirebaseTask(key, "New Task");
                tasksRef.child(key).setValue(t);


                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                intent.putExtra("key", key);
                intent.putExtra("mode", TaskActivity.CREATE);
                startActivity(intent);
            }
        });

        mTabs = (TabLayout) findViewById(R.id.tabs);
        //mTabs.addTab(mTabs.newTab().setIcon(R.drawable.ic_inbox_24dp));
        //mTabs.addTab(mTabs.newTab().setIcon(R.drawable.ic_schedule_24dp));
        //mTabs.addTab(mTabs.newTab().setIcon(R.drawable.ic_done_24dp));

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(ListFragment.newInstance(ListFragment.INBOX));
        fragments.add(ListFragment.newInstance(ListFragment.DONE));
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), fragments));
        mTabs.setupWithViewPager(mViewPager);


        mViewPager.setPageMarginDrawable(R.drawable.page_padding);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0) fab.show();
                else fab.hide();


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);
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

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_sort_alpha:
                PagerAdapter adapter = (PagerAdapter) mViewPager.getAdapter();
                ListFragment fragment = (ListFragment) adapter.getItem(adapter.getPos());
                fragment.sort(ListFragment.ALPHABET);
                break;
            case R.id.action_sign_out:
                Firebase ref = new Firebase(Bounce.URL);
                ref.unauth();
                startActivity(new Intent(this, DispatchActivity.class));
                finish();
        }

        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTaskClick(String key) {
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra("key", key);
        intent.putExtra("mode", TaskActivity.EDIT);
        startActivity(intent);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_sort_alpha:
                PagerAdapter adapter = (PagerAdapter) mViewPager.getAdapter();
                ListFragment fragment = (ListFragment) adapter.getItem(adapter.getPos());
                fragment.sort(ListFragment.ALPHABET);
                break;
            case R.id.action_sign_out:
                Firebase ref = new Firebase(Bounce.URL);
                ref.unauth();
                startActivity(new Intent(this, DispatchActivity.class));
                finish();
        }

        return false;
    }
}
