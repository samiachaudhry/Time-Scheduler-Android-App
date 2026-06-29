package com.saqi.time_scheduler.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saqi.time_scheduler.Constants.Const;
import com.saqi.time_scheduler.Models.AssignTeacherToClass;
import com.saqi.time_scheduler.Models.ClassDetail;
import com.saqi.time_scheduler.Models.Report;
import com.saqi.time_scheduler.Models.Room;
import com.saqi.time_scheduler.Models.Teacher;
import com.saqi.time_scheduler.Models.TimeSlot;

import java.util.ArrayList;
import java.util.List;

public class FDUtils {

    private FDUtils() {
    }

    private static FDUtils _INSTANCE;

    public static FDUtils getInstance() {
        if (_INSTANCE == null) _INSTANCE = new FDUtils();
        return _INSTANCE;
    }

    public void fetchDataAndExport(Activity context) {
        List<ClassDetail> TIMETABLE = new ArrayList<>();
        List<AssignTeacherToClass> assignTeacherToClassList = new ArrayList<>();
        List<Teacher> teacherList = new ArrayList<>();
        List<Room> roomsList = new ArrayList<>();
        List<TimeSlot> timeSlotList = new ArrayList<>();

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(true);
        progressDialog.setTitle("Exporting");
        progressDialog.setMessage("Please wait timetable fetching and exporting...");
        progressDialog.show();
        try {
            FirebaseDatabase.getInstance()
                    .getReference("Admin")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) return;
                            assignTeacherToClassList.clear();
                            teacherList.clear();
                            roomsList.clear();
                            timeSlotList.clear();
                            TIMETABLE.clear();

                            DataSnapshot snapAssignSubject = snapshot.child(Const.FB_ASSIGN_SUBJECT);
                            if (snapAssignSubject.exists())
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
                            if (snapTeachers.exists())
                                for (DataSnapshot item : snapTeachers.getChildren()) {
                                    String arrivalTime = item.child("arrivaltime").getValue().toString();
                                    String department = item.child("department").getValue().toString();
                                    String leaveTime = item.child("leavetime").getValue().toString();
                                    String teacherName = item.child("teachername").getValue().toString();
                                    Teacher teacher = new Teacher(teacherName, department, arrivalTime, leaveTime);
                                    teacherList.add(teacher);
                                }

                            DataSnapshot snapRooms = snapshot.child("Rooms");
                            if (snapRooms.exists())
                                for (DataSnapshot itemR : snapRooms.getChildren()) {
                                    String name = itemR.getValue().toString();
                                    roomsList.add(new Room(name));
                                }

                            DataSnapshot snapTimeSlot = snapshot.child("Time Slots");
                            if (snapTimeSlot.exists())
                                for (DataSnapshot itemT : snapTimeSlot.getChildren()) {
                                    String endtime = itemT.child("end time").getValue().toString();
                                    String starttime = itemT.child("start time").getValue().toString();
                                    timeSlotList.add(new TimeSlot(starttime, endtime));
                                }


                            DataSnapshot snapTimeTable = snapshot.child(Const.FB_DB_TIME_TABLE);
                            if (snapTimeTable.exists()) {
                                for (DataSnapshot snap : snapTimeTable.getChildren()) {
                                    ClassDetail classDetail = snap.getValue(ClassDetail.class);
                                    TIMETABLE.add(classDetail);
                                }
                                SaveFile.getInstance(context).saveToExcel(TIMETABLE, roomsList, timeSlotList);
                                progressDialog.dismiss();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(context, "TimeTable not exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();
            Toast.makeText(context, "Some data not available", Toast.LENGTH_SHORT).show();
        }
    }


}
