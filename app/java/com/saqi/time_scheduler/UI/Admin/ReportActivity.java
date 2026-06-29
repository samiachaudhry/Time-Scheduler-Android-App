package com.saqi.time_scheduler.UI.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.saqi.time_scheduler.R;
import com.saqi.time_scheduler.UI.Fragments.ReportFragments.ReportFragment;
import com.saqi.time_scheduler.UI.Fragments.TimeTableFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReportActivity extends AppCompatActivity {
    private final String TAG = ReportActivity.class.getSimpleName();

    List<Fragment> listOfFragments;
    List<String> listOfTitles;

    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;
    TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);


        fillFragmentAndTitle();
        setting();

    }

    private void fillFragmentAndTitle() {
        listOfFragments = new ArrayList<>();
        listOfTitles = new ArrayList<>();

        listOfTitles.add("Teacher");
        listOfTitles.add("Class");
        listOfTitles.add("Subject");

        listOfFragments.add(new ReportFragment("Teacher"));
        listOfFragments.add(new ReportFragment("Class"));
        listOfFragments.add(new ReportFragment("Subject"));

    }

    private void setting() {
        viewPager = findViewById(R.id.view_pager);
        tabs = findViewById(R.id.tabs);

        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        tabs.setupWithViewPager(viewPager);

        for (int i = 0; i < tabs.getTabCount(); i++) {
            TabLayout.Tab tab = tabs.getTabAt(i);
            RelativeLayout relativeLayout = (RelativeLayout)
                    LayoutInflater.from(this).inflate(R.layout.custom_tabs_layout, tabs, false);

            TextView tabTextView = relativeLayout.findViewById(R.id.tab_title);
            tabTextView.setText(tab.getText());
            tab.setCustomView(relativeLayout);
            //tab.select();
        }
    }

    class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final Context mContext;

        public SectionsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return listOfFragments.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return listOfTitles.get(position);
        }

        @Override
        public int getCount() {
            return listOfTitles.size();
        }
    }

}