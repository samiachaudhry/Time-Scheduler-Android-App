package com.saqi.time_scheduler.UI.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saqi.time_scheduler.Adapters.ClassDetailAdapter;
import com.saqi.time_scheduler.Constants.Const;
import com.saqi.time_scheduler.Models.ClassDetail;
import com.saqi.time_scheduler.Models.User;
import com.saqi.time_scheduler.R;
import com.saqi.time_scheduler.Utils.ListUtils;
import com.saqi.time_scheduler.Utils.SaveUser;

import java.util.ArrayList;
import java.util.List;


public class MyTimeTableFragment extends Fragment {
    private final String TAG = "MyTimeTableFragment";

    RecyclerView RV;
    GridLayoutManager layoutManager;
    ClassDetailAdapter adapter;
    List<ClassDetail> list;
    List<ClassDetail> TIMETABLE;

    LinearLayout LL_NA;
    SpinKitView spinKitView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_time_table, container, false);
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
        adapter = new ClassDetailAdapter(getActivity(), list);
        RV.setAdapter(adapter);
    }

    private void setting(View view) {
        RV = view.findViewById(R.id.RV_frag_MyTimeTable);
        layoutManager = new GridLayoutManager(getActivity(), 2);
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
                        checkForUserProgram();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkForUserProgram() {
        String program = SaveUser.getInstance(getActivity()).fetchUser().getProgram();
        if (program.equals("")) {
            checkProgramFromCloud();
        } else {
            list = ListUtils.getInstance(getActivity()).getClassByProgram(TIMETABLE, program);
            resetAdapter();
        }

    }

    private void checkProgramFromCloud() {
        String UID = SaveUser.getInstance(getActivity()).fetchUser().getUid();
        FirebaseDatabase.getInstance()
                .getReference(Const.FB_DB_USERS)
                .child(UID)
                .child(Const.FB_DB_USERS_PROGRAM)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            spinKitView.setVisibility(View.GONE);
                            return;
                        }
                        String program = snapshot.getValue().toString();
                        User user = SaveUser.getInstance(getActivity()).fetchUser();
                        user.setProgram(program);
                        SaveUser.getInstance(getActivity()).save(user);
                        list = ListUtils.getInstance(getActivity()).getClassByProgram(TIMETABLE, program);
                        resetAdapter();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, error.toString());
                    }
                });
    }
}