package com.saqi.time_scheduler.UI.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saqi.time_scheduler.Adapters.AssignTeachersAdapter;

import com.saqi.time_scheduler.Models.AssignTeacherToClass;
import com.saqi.time_scheduler.R;

import java.util.ArrayList;
import java.util.List;

public class GetAssignSubjects extends AppCompatActivity {
    RecyclerView RV;
    RecyclerView.LayoutManager layoutManager;
    List<AssignTeacherToClass> list;
    AssignTeachersAdapter adapter;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_assign_subjects);
        RV = findViewById(R.id.RV_fetch_data);

        layoutManager = new GridLayoutManager(this, 2);
        RV.setLayoutManager(layoutManager);
        list = new ArrayList<>();
        resetAdapter();
        getData();
    }

    private void resetAdapter() {
        adapter = new AssignTeachersAdapter(this, list);
        RV.setAdapter(adapter);
        db = FirebaseDatabase.getInstance().getReference("Admin").child("Assign Subjects");
    }

    private void getData() {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String id = snap.getKey().toString();
                    AssignTeacherToClass a = snap.getValue(AssignTeacherToClass.class);
                    a.setId(id);
                    list.add(a);
                }
                resetAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GetAssignSubjects.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}