package com.saqi.time_scheduler.UI.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.saqi.time_scheduler.Adapters.SubjectsAdapter;
import com.saqi.time_scheduler.Models.AssignTeacherToClass;
import com.saqi.time_scheduler.Models.Subject;
import com.saqi.time_scheduler.R;
import com.saqi.time_scheduler.Utils.LinearSearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AssignTeachers extends AppCompatActivity {
    private final String TAG = "AssignTeaachers";
    private final String FB_UPLOAD_REF = "Assign Subjects";
    Spinner spinprog, spinsub, spinchr, spinteach;
    String programme = "prog";

    String subject = "EAD";
    String subcode = "CMP";
    String teachername = "example";
    String Chr = "3";
    Button btnsubmit,btndetail;
    FirebaseAuth auth;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ValueEventListener teacherListener, subjectListener, Programlistener;
    ArrayAdapter<String> adapterTeacher, programAdapter;
    ArrayList<String> listTeacher, listPrograms;
    List<Subject> listSubjects;
    SubjectsAdapter subjectAdapter;
    DatabaseReference dbTeacher, dbSubjects, dbProgram;
    ProgressDialog progressDialog;

    AssignTeacherToClass teacherToClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_entry);

        Intent intent = getIntent();
        Object atc = intent.getSerializableExtra("data");
        teacherToClass = (AssignTeacherToClass) atc;
        if (teacherToClass != null) {
            Toast.makeText(this, "Data received:" + teacherToClass.getId(), Toast.LENGTH_SHORT).show();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        spinteach = findViewById(R.id.spin_teacher);
        spinprog = findViewById(R.id.spin_programme);

        spinsub = findViewById(R.id.spin_sub_name);
        spinchr = findViewById(R.id.spin_sub_hr);
        btndetail=findViewById(R.id.btn_list);
        btnsubmit = findViewById(R.id.submit);
//        getsubjects();
//        getdata();
//        getprogrammes();
        getAndFillData();
        spinprog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                programme = ((String) spinprog.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinchr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Chr = ((String) spinchr.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinsub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (listSubjects.size() > 0) {
                    Subject subjectData = listSubjects.get(position);
                    subcode = subjectData.getSubcode();
                    subject = subjectData.getSubname();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinteach.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                teachername = ((String) spinteach.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                String key = teachername + " " + date.getTime();
                if (teacherToClass != null) {
                    key = teacherToClass.getId();
                }
                progressDialog.show();
                FirebaseDatabase.getInstance()
                        .getReference("Admin")
                        .child(FB_UPLOAD_REF)
                        .child(key)
                        .setValue(new AssignTeacherToClass(teachername, programme, subject, subcode, Chr))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(AssignTeachers.this, "Data Added", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AssignTeachers.this, "Data not added", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        btndetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(AssignTeachers.this, GetAssignSubjects.class);
                startActivity(intent1);
            }
        });
    }

    private void getAndFillData() {
        listPrograms = new ArrayList<>();
        programAdapter = new ArrayAdapter<String>(AssignTeachers.this, android.R.layout.simple_spinner_dropdown_item, listPrograms);
        listSubjects = new ArrayList<>();
        subjectAdapter = new SubjectsAdapter(this, listSubjects);
        listTeacher = new ArrayList<>();
        adapterTeacher = new ArrayAdapter<String>(AssignTeachers.this, android.R.layout.simple_spinner_dropdown_item, listTeacher);

        FirebaseDatabase.getInstance().getReference("Admin")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapAdmin) {
                        listTeacher.clear();
                        listSubjects.clear();
                        listPrograms.clear();
                        DataSnapshot snapTeachers = snapAdmin.child("Teachers");
                        DataSnapshot snapSubjects = snapAdmin.child("Subjects");
                        DataSnapshot snapProgram = snapAdmin.child("Programes");

                        for (DataSnapshot item : snapTeachers.getChildren()) {
                            String name = item.child("teachername").getValue().toString();
                            listTeacher.add(name);
                        }

                        for (DataSnapshot item : snapSubjects.getChildren()) {
                            String subname = item.child("subname").getValue().toString();
                            String code = item.child("subcode").getValue().toString();
                            listSubjects.add(new Subject(subname, code));
                        }

                        for (DataSnapshot item : snapProgram.getChildren()) {
                            String name = item.getValue().toString();
                            listPrograms.add(name);
                        }
                        subjectAdapter.notifyDataSetChanged();
                        spinsub.setAdapter(subjectAdapter);
                        adapterTeacher.notifyDataSetChanged();
                        spinteach.setAdapter(adapterTeacher);
                        programAdapter.notifyDataSetChanged();
                        spinprog.setAdapter(programAdapter);

                        if (teacherToClass != null) {
                            Toast.makeText(AssignTeachers.this, "fill data", Toast.LENGTH_SHORT).show();
                            spinsub.setSelection(LinearSearch.getInstance().getIndexOfSubject(listSubjects, teacherToClass.getSubject()));
                            spinteach.setSelection(LinearSearch.getInstance().getIndex(listTeacher, teacherToClass.getSubject()));
                            spinprog.setSelection(LinearSearch.getInstance().getIndex(listPrograms, teacherToClass.getSubject()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AssignTeachers.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}