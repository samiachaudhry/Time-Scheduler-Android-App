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
import com.saqi.time_scheduler.Models.Subject;
import com.saqi.time_scheduler.R;

import java.util.Date;

public class AddSubject extends AppCompatActivity {

    Button btnsubmit;
    FirebaseAuth auth;
    EditText txtname,txtcode;
    String subname="",subcode="";
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_entry);
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        txtname=findViewById(R.id.txt_sub_name);
        txtcode=findViewById(R.id.txt_sub_code);

        btnsubmit = findViewById(R.id.submit);

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subname = txtname.getText().toString().trim();
                subcode = txtcode.getText().toString().trim();
                Date date = new Date();
                FirebaseDatabase.getInstance()
                        .getReference("Admin")
                        .child("Subjects")
                        .child(subname+ " "+date.getTime() )
                        .setValue(new Subject(subname,subcode))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                if (task.isSuccessful()) {
                                    Toast.makeText(AddSubject.this, "Data Added", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddSubject.this, "Data not added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}