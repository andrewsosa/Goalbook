package com.andrewsosa.goalbook;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class PagerFragment extends Fragment implements ListFragment.TaskInteractionListener{


    private ListFragment.TaskInteractionListener mListListener;
    private PagerFragmentInteractionListener mPagerListener;
    ViewPager mViewPager;



    public static PagerFragment newInstance() {
        return new PagerFragment();
    }

    // Required empty public constructor
    public PagerFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_pager, container, false);
        mViewPager = (ViewPager) v.findViewById(R.id.viewPager);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayList<android.support.v4.app.Fragment> fragments = new ArrayList<>();
        fragments.add(ListFragment.newInstance(Goal.DAILY));
        fragments.add(ListFragment.newInstance(Goal.WEEKLY));
        fragments.add(ListFragment.newInstance(Goal.INTERMEDIATE));
        fragments.add(ListFragment.newInstance(Goal.LONGTERM));
        mViewPager.setAdapter(new PagerAdapter(getChildFragmentManager(), fragments));
        mPagerListener.getTabs().setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPagerListener.updateActive(new String[]{
                        Goal.DAILY,
                        Goal.WEEKLY,
                        Goal.INTERMEDIATE,
                        Goal.LONGTERM
                }[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListListener = (ListFragment.TaskInteractionListener) context;
            mPagerListener = (PagerFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TaskInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListListener = null;
        mPagerListener = null;
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return mListListener.getSharedPreferences();
    }

    @Override
    public void onTaskClick(String key) {
        mListListener.onTaskClick(key);
    }

    @Override
    public void onTaskLongClick(String key) {
        //mListListener.onTaskLongClick(key);
    }

    public interface PagerFragmentInteractionListener {
        TabLayout getTabs();
        void updateActive(String mode);
    }

}
