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

public class AddClass extends AppCompatActivity {
   EditText name;
    String programme = "prog";
    Button btnsubmit;
    FirebaseAuth auth;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programms);
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        name = findViewById(R.id.txtname);
        btnsubmit = findViewById(R.id.submit);

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                programme=name.getText().toString();
                Date date = new Date();
                FirebaseDatabase.getInstance()
                        .getReference("Admin")
                        .child("Programes")
                        .child(date.getTime() + "")
                        .setValue(programme)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    Toast.makeText(AddClass.this, "Data Added", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddClass.this, "Data not added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}