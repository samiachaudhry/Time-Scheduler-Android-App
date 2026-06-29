package com.saqi.time_scheduler.UI.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saqi.time_scheduler.Constants.Const;
import com.saqi.time_scheduler.Models.AssignTeacherToClass;
import com.saqi.time_scheduler.Models.ClassDetail;
import com.saqi.time_scheduler.Models.Room;
import com.saqi.time_scheduler.Models.Teacher;
import com.saqi.time_scheduler.Models.TimeSlot;
import com.saqi.time_scheduler.R;
import com.saqi.time_scheduler.Timetable.MyTimeTable2;
import com.saqi.time_scheduler.UI.FeedbackActivity;
import com.saqi.time_scheduler.Utils.DailyNotificationUtils;
import com.saqi.time_scheduler.Utils.FDUtils;
import com.saqi.time_scheduler.Utils.SaveFile;

import java.util.ArrayList;
import java.util.List;

public class AdminHome extends AppCompatActivity {
    private final String TAG = "AdminHome";
    private final String fileName = "mytimetable.txt";
    CardView cardRoom, cardSubject, cardProgram, cardTeacher, cardGenerate;
    CardView cardAssign, cardSave, cardReport, cardSendNotifications, cardFeedbacks;
    List<AssignTeacherToClass> assignTeacherToClassList;

    List<Teacher> teacherList;
    List<Room> roomsList;
    List<TimeSlot> timeSlotList;
    List<ClassDetail> TIMETABLE;
    Dialog dialog;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        assignTeacherToClassList = new ArrayList<>();
        teacherList = new ArrayList<>();
        roomsList = new ArrayList<>();
        timeSlotList = new ArrayList<>();
        TIMETABLE = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Timetable");
        progressDialog.setMessage("Timetable generating...");

        cardRoom = findViewById(R.id.rooms);
        cardSubject = findViewById(R.id.subject);
        cardProgram = findViewById(R.id.programme);
        cardTeacher = findViewById(R.id.teacher);
        cardGenerate = findViewById(R.id.card_admin_home_generate);
        cardAssign = findViewById(R.id.card_admin_home_assign);
        cardSave = findViewById(R.id.card_admin_home_save);
        cardReport = findViewById(R.id.card_admin_home_report);
        cardFeedbacks = findViewById(R.id.card_admin_home_feedbacks);
        cardSendNotifications = findViewById(R.id.card_admin_home_send_notifications);

        cardRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(AdminHome.this, AddRoom.class);
                startActivity(myintent);
            }
        });
        cardSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(AdminHome.this, AddSubject.class);
                startActivity(myintent);
            }
        });
        cardProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(AdminHome.this, AddClass.class);
                startActivity(myintent);
            }
        });
        cardTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(AdminHome.this, AddTeacher.class);
                startActivity(myintent);
            }
        });
        cardAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myintent = new Intent(AdminHome.this, AssignTeachers.class);
                startActivity(myintent);
            }
        });

        cardGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupConfirmationForGenerateTimetable();
            }
        });

        cardSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FDUtils.getInstance().fetchDataAndExport(AdminHome.this);
//                fetchDataAndExport();
            }
        });
        cardReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AdminHome.this, ReportActivity.class);
                startActivity(myIntent);
            }
        });
        cardFeedbacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AdminHome.this, ShowFeedbacksActivity.class);
                startActivity(myIntent);
            }
        });
        cardSendNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DailyNotificationUtils.getInstance(AdminHome.this).fetchTimeTableAndSendNotifications();
            }
        });
    }

    private void fetchData() {
        progressDialog.show();
        try {
            FirebaseDatabase.getInstance()
                    .getReference("Admin")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            assignTeacherToClassList.clear();
                            teacherList.clear();
                            roomsList.clear();
                            timeSlotList.clear();

                            DataSnapshot snapAssignSubject = snapshot.child(Const.FB_ASSIGN_SUBJECT);
                            for (DataSnapshot snapshot1 : snapAssignSubject.getChildren()) {
                                String chr = snapshot1.child("chr").getValue().toString();
                                String programme = snapshot1.child("programme").getValue().toString();
                                String subcode = snapshot1.child("subcode").getValue().toString();
                                String subject = snapshot1.child("subject").getValue().toString();
                                String teachername = snapshot1.child("teachername").getValue().toString();
                                AssignTeacherToClass data = new AssignTeacherToClass(teachername, programme, subject, subcode, chr);
                                assignTeacherToClassList.add(data);
                            }

                            DataSnapshot snapTeachers = snapshot.child("Teachers");
                            for (DataSnapshot item : snapTeachers.getChildren()) {
                                String arrivalTime = item.child("arrivaltime").getValue().toString();
                                String department = item.child("department").getValue().toString();
                                String leaveTime = item.child("leavetime").getValue().toString();
                                String teacherName = item.child("teachername").getValue().toString();
                                Teacher teacher = new Teacher(teacherName, department, arrivalTime, leaveTime);
                                teacherList.add(teacher);
                            }

                            DataSnapshot snapRooms = snapshot.child("Rooms");
                            for (DataSnapshot itemR : snapRooms.getChildren()) {
                                String name = itemR.getValue().toString();
                                roomsList.add(new Room(name));
                            }

                            DataSnapshot snapTimeSlot = snapshot.child("Time Slots");
                            for (DataSnapshot itemT : snapTimeSlot.getChildren()) {
                                String endtime = itemT.child("end time").getValue().toString();
                                String starttime = itemT.child("start time").getValue().toString();
                                timeSlotList.add(new TimeSlot(starttime, endtime));
                            }
                            generateTimeTable();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(AdminHome.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();
            Toast.makeText(this, "Some data not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateTimeTable() {
        progressDialog.dismiss();
        if (!isDataAvailable()) {
            return;
        }
        showPopupForLimit();
    }

    private boolean isDataAvailable() {
        if (teacherList.size() <= 0) {
            Toast.makeText(this, "Teachers data unavailable yet", Toast.LENGTH_SHORT).show();
            return false;
        } else if (assignTeacherToClassList.size() <= 0) {
            Toast.makeText(this, "Teachers assign to classes data unavailable yet", Toast.LENGTH_SHORT).show();
            return false;
        } else if (roomsList.size() <= 0) {
            Toast.makeText(this, "Rooms data unavailable yet", Toast.LENGTH_SHORT).show();
            return false;
        } else if (timeSlotList.size() <= 0) {
            Toast.makeText(this, "TimeSlots data unavailable yet", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void showPopupForLimit() {
        dialog = new Dialog(AdminHome.this);
        dialog.setContentView(R.layout.popup_classes_per_days);
        dialog.setCancelable(false);
        EditText etLimit = dialog.findViewById(R.id.et_dialog_limit_limit);
        Button btnSet = dialog.findViewById(R.id.btn_dialog_limit_set);
        TextView tvClose = dialog.findViewById(R.id.tv_dialog_limit_close);

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String limit = etLimit.getText().toString();
                if (limit.equals("")) {
                    etLimit.setError("Enter limit");
                    return;
                }
                int lmt = Integer.parseInt(limit);
                progressDialog.show();
                MyTimeTable2 myTimeTable2 = new MyTimeTable2(AdminHome.this, assignTeacherToClassList, teacherList, roomsList, timeSlotList, lmt);
                List<ClassDetail> TIME_TABLE = myTimeTable2.generate();
                formatePreviousTimeTable(TIME_TABLE);
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void showPopupConfirmationForGenerateTimetable() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_confirmation);
        dialog.setCancelable(true);
        TextView tvHeading = dialog.findViewById(R.id.tv_popup_heading);
        TextView tvBody = dialog.findViewById(R.id.tv_popup_body);
        TextView tvButtonText = dialog.findViewById(R.id.tv_popup_button_text);
        LinearLayout LLAccept = dialog.findViewById(R.id.LL_popup_accept);
        TextView tvClose = dialog.findViewById(R.id.tv_popup_close);

        tvHeading.setText("Generate Timetable");
        tvBody.setText("Are you sure to create/override timetable?");
        tvButtonText.setText("Yes");

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        LLAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void formatePreviousTimeTable(List<ClassDetail> time_table) {
        FirebaseDatabase.getInstance().getReference("Admin")
                .child(Const.FB_DB_TIME_TABLE)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            uploadTimeTable(time_table);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(AdminHome.this, "Something wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadTimeTable(List<ClassDetail> time_table) {
        FirebaseDatabase.getInstance()
                .getReference("Admin")
                .child(Const.FB_DB_TIME_TABLE)
                .setValue(time_table)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(AdminHome.this, "Timetable created and Uploaded", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AdminHome.this, "Something wrong!", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, task.getException().toString());
                        }
                    }
                });
    }



}