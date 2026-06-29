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
import com.saqi.time_scheduler.Models.Teacher;
import com.saqi.time_scheduler.R;

import java.util.Date;

public class AddTeacher extends AppCompatActivity {
    Button btnsubmit;
    FirebaseAuth auth;
    EditText txtname, txtdep, txtarrival, txtleave;
    String teachername = "", department = "", arrivaltime = "", leavetime = "";
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        txtname = findViewById(R.id.txtname);
        txtdep = findViewById(R.id.txtdep);
        txtarrival = findViewById(R.id.arrivaltime);
        txtleave = findViewById(R.id.leavetime);

        btnsubmit = findViewById(R.id.submit);

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teachername = txtname.getText().toString().trim();
                department = txtdep.getText().toString().trim();
                arrivaltime = txtarrival.getText().toString().trim();
                leavetime = txtleave.getText().toString().trim();
                Date date = new Date();
                FirebaseDatabase.getInstance()
                        .getReference("Admin")
                        .child("Teachers")
                        .child(teachername + " " + date.getTime())
                        .setValue(new Teacher(teachername, department, arrivaltime, leavetime))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                if (task.isSuccessful()) {
                                    Toast.makeText(AddTeacher.this, "Data Added", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddTeacher.this, "Data not added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}