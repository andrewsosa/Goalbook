package com.andrewsosa.bounce;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {

    public int pos = 0;
    private List<Fragment> fragments;

    public PagerAdapter(FragmentManager fm, List<Fragment> myFrags) {
        super(fm);
        fragments = myFrags;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        setPos(position);
        String pageTitle = "";
        switch(pos)
        {
            case 0:
                pageTitle = "Inbox";
                break;
            case 1:
                pageTitle = "Done";
                break;
        }
        return pageTitle;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
