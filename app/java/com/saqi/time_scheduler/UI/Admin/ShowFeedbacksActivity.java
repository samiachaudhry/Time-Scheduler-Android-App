package com.saqi.time_scheduler.UI.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saqi.time_scheduler.Adapters.AssignTeachersAdapter;
import com.saqi.time_scheduler.Adapters.FeedbackAdapter;
import com.saqi.time_scheduler.Constants.Const;
import com.saqi.time_scheduler.Models.AssignTeacherToClass;
import com.saqi.time_scheduler.Models.Feedback;
import com.saqi.time_scheduler.R;

import java.util.ArrayList;
import java.util.List;

public class ShowFeedbacksActivity extends AppCompatActivity {
    private final String TAG = ShowFeedbacksActivity.class.getSimpleName();
    RecyclerView RV;
    LinearLayoutManager layoutManager;
    List<Feedback> list;
    FeedbackAdapter adapter;

    LinearLayout LL_NA;
    SpinKitView spinKitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_feedbacks);
        RV = findViewById(R.id.RV);
        LL_NA = findViewById(R.id.LL_NA);
        spinKitView = findViewById(R.id.spin_kit);


        layoutManager = new LinearLayoutManager(this);
        RV.setLayoutManager(layoutManager);
        list = new ArrayList<>();


        getData();
    }

    private void resetAdapter() {
        spinKitView.setVisibility(View.GONE);
        if (list.size() <= 0) {
            LL_NA.setVisibility(View.VISIBLE);
        } else {
            LL_NA.setVisibility(View.GONE);
        }
        adapter = new FeedbackAdapter(this, list);
        RV.setAdapter(adapter);
    }

    private void getData() {
        FirebaseDatabase.getInstance()
                .getReference(Const.FB_FEEDBACKS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapUID : dataSnapshot.getChildren()) {
                                for (DataSnapshot snapTime : snapUID.getChildren()) {
                                    Feedback feedback = snapTime.getValue(Feedback.class);
                                    list.add(feedback);
                                }
                            }
                        }
                        resetAdapter();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ShowFeedbacksActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, databaseError.toString());
                    }
                });
    }
}