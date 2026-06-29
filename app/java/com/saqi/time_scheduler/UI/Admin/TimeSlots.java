package com.saqi.time_scheduler.UI.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.saqi.time_scheduler.R;

import java.util.Date;

public class TimeSlots extends AppCompatActivity {
    EditText name;
    String slot = "time";
    Button btnsubmit;
    FirebaseAuth auth;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_slots);
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        name = findViewById(R.id.txtname);
        btnsubmit = findViewById(R.id.submit);


        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                createDatabaseRecord();

            }
        });


    }

    public void createDatabaseRecord() {
        slot=name.getText().toString();
        Date date = new Date();
        FirebaseDatabase.getInstance()
                .getReference("Admin")
                .child("Time Slots")
                .child( date.getTime()+"")
                .setValue(slot)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            Toast.makeText(TimeSlots.this, "Data Added", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TimeSlots.this, "Data not added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}