package com.saqi.time_scheduler.UI.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saqi.time_scheduler.Constants.Const;
import com.saqi.time_scheduler.Models.ClassDetail;
import com.saqi.time_scheduler.R;

import java.util.ArrayList;
import java.util.List;


public class TimeTableFragment extends Fragment {
    private final String TAG = "TimeTableFragment";
    List<Fragment> listOfFragments;
    List<String> listOfTitles;
    List<ClassDetail> TIME_TABLE;

    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;
    TabLayout tabs;

    LinearLayout LL_NA;
    SpinKitView spinKitView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_table, container, false);
        setting(view);
        fetchTimeTable();
        return view;
    }

    private void fetchTimeTable() {
        FirebaseDatabase.getInstance()
                .getReference("Admin")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        spinKitView.setVisibility(View.GONE);
                        listOfTitles.clear();
                        TIME_TABLE.clear();

                        if (!snapshot.exists()) return;

                        DataSnapshot snapProgram = snapshot.child("Programes");
                        if (snapProgram.exists()) {
                            for (DataSnapshot item : snapProgram.getChildren()) {
                                String prog = item.getValue().toString();
                                listOfTitles.add(prog);
                            }
                        } else {
                            Toast.makeText(getActivity(), "No program found", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        DataSnapshot snapTimeTable = snapshot.child(Const.FB_DB_TIME_TABLE);
                        if (snapTimeTable.exists()) {
                            for (DataSnapshot item : snapTimeTable.getChildren()) {
                                ClassDetail cd = item.getValue(ClassDetail.class);
                                TIME_TABLE.add(cd);
                            }
                        } else {
                            Toast.makeText(getActivity(), "No Time table found", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        fillFragments();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setting(View view) {
        listOfFragments = new ArrayList<>();
        listOfTitles = new ArrayList<>();
        TIME_TABLE = new ArrayList<>();
        viewPager = view.findViewById(R.id.view_pager);
        tabs = view.findViewById(R.id.tabs);
        spinKitView = view.findViewById(R.id.spin_kit);
        LL_NA = view.findViewById(R.id.LL_NA);
    }

    private void fillFragments() {
        if (listOfTitles.size() <= 0) {
            LL_NA.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
            return;
        }
        LL_NA.setVisibility(View.GONE);

        for (int i = 0; i < listOfTitles.size(); i++) {
            listOfFragments.add(new ProgramFragment(listOfTitles.get(i), TIME_TABLE));
        }
        resetAdapter();
    }

    private void resetAdapter() {
        sectionsPagerAdapter = new SectionsPagerAdapter(getActivity(), getActivity().getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        tabs.setupWithViewPager(viewPager);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);

        for (int i = 0; i < tabs.getTabCount(); i++) {
            TabLayout.Tab tab = tabs.getTabAt(i);
            RelativeLayout relativeLayout = (RelativeLayout)
                    LayoutInflater.from(getActivity()).inflate(R.layout.custom_tabs_scroll_layout, tabs, false);

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