package com.saqi.time_scheduler.UI.Fragments.ReportFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saqi.time_scheduler.Adapters.ReportAdapter;
import com.saqi.time_scheduler.Constants.Const;
import com.saqi.time_scheduler.Models.ClassDetail;
import com.saqi.time_scheduler.Models.Report;
import com.saqi.time_scheduler.R;
import com.saqi.time_scheduler.Utils.ListUtils;

import java.util.ArrayList;
import java.util.List;


public class ReportFragment extends Fragment {
    private final String TAG = "MyTimeTableFragment";

    RecyclerView RV;
    LinearLayoutManager layoutManager;
    ReportAdapter adapter;
    List<Report> list;
    List<ClassDetail> TIMETABLE;

    LinearLayout LL_NA;
    SpinKitView spinKitView;

    private String report_type;

    public ReportFragment(String reportType) {
        this.report_type = reportType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        setting(view);
        fetchTimeTable();

        return view;
    }

    private void resetAdapter() {
        spinKitView.setVisibility(View.GONE);
        if (list.size() > 0) {
            LL_NA.setVisibility(View.GONE);
        } else {
            LL_NA.setVisibility(View.VISIBLE);
        }
        adapter = new ReportAdapter(getActivity(), list);
        RV.setAdapter(adapter);
    }

    private void setting(View view) {
        RV = view.findViewById(R.id.RV);
        layoutManager = new LinearLayoutManager(getActivity());
        RV.setLayoutManager(layoutManager);
        list = new ArrayList<>();
        TIMETABLE = new ArrayList<>();
        LL_NA = view.findViewById(R.id.LL_NA);
        spinKitView = view.findViewById(R.id.spin_kit);
    }

    private void fetchTimeTable() {
        FirebaseDatabase.getInstance()
                .getReference("Admin")
                .child(Const.FB_DB_TIME_TABLE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        TIMETABLE.clear();
                        if (!snapshot.exists()) {
                            spinKitView.setVisibility(View.GONE);
                            return;
                        }

                        for (DataSnapshot snap : snapshot.getChildren()) {
                            ClassDetail classDetail = snap.getValue(ClassDetail.class);
                            TIMETABLE.add(classDetail);
                        }
                        checkForTeachersWeeklyClasses();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkForTeachersWeeklyClasses() {
        list = ListUtils.getInstance(getActivity()).getReportComplete(TIMETABLE, report_type);
        resetAdapter();
    }
}