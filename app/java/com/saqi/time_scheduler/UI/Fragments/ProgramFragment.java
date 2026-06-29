package com.saqi.time_scheduler.UI.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.saqi.time_scheduler.Adapters.ClassDetailAdapter;
import com.saqi.time_scheduler.Models.ClassDetail;
import com.saqi.time_scheduler.R;
import com.saqi.time_scheduler.Utils.ListUtils;
import com.saqi.time_scheduler.Utils.Sorting;

import java.util.ArrayList;
import java.util.List;


public class ProgramFragment extends Fragment {
    private final String TAG = "ProgramFragment";

    private String PROGRAM;
    private List<ClassDetail> TIME_TABLE;
    private List<ClassDetail> list;

    RecyclerView RV;
    GridLayoutManager layoutManager;
    ClassDetailAdapter adapter;

    SpinKitView spinKitView;
    LinearLayout LL_NA;

    public ProgramFragment(String program, List<ClassDetail> timetable) {
        this.PROGRAM = program;
        this.TIME_TABLE = timetable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_program, container, false);
        setting(view);
//
//        list = ListUtils.getInstance(getActivity()).getClassByProgram(TIME_TABLE, PROGRAM);
//        resetAdapter();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        list = ListUtils.getInstance(getActivity()).getClassByProgram(TIME_TABLE, PROGRAM);
//        if (PROGRAM.equals("BSSE 2nd Self"))
        list = Sorting.getInstance().sortAscendingByDay(list);
        resetAdapter();
    }

    private void setting(View view) {
        LL_NA = view.findViewById(R.id.LL_NA);
        spinKitView = view.findViewById(R.id.spin_kit);
        RV = view.findViewById(R.id.RV_frag_prog);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        RV.setLayoutManager(layoutManager);
        list = new ArrayList<>();
    }

    private void resetAdapter() {
        spinKitView.setVisibility(View.GONE);
        if (list.size() > 0) {
            LL_NA.setVisibility(View.GONE);
        } else {
            LL_NA.setVisibility(View.VISIBLE);
        }
        adapter = new ClassDetailAdapter(getActivity(), list);
        RV.setAdapter(adapter);
    }
}