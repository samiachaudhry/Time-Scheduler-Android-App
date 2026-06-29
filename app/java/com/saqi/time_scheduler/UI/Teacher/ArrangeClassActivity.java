package com.saqi.time_scheduler.UI.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saqi.time_scheduler.Constants.Const;
import com.saqi.time_scheduler.Models.ClassDetail;
import com.saqi.time_scheduler.Models.Room;
import com.saqi.time_scheduler.Models.TimeSlot;
import com.saqi.time_scheduler.Notifications.MessageNotificationData;
import com.saqi.time_scheduler.Notifications.NotificationUtils;
import com.saqi.time_scheduler.Notifications.Notifications;
import com.saqi.time_scheduler.R;
import com.saqi.time_scheduler.Utils.FormateDate;
import com.saqi.time_scheduler.Utils.LinearSearch;
import com.saqi.time_scheduler.Utils.ListUtils;
import com.saqi.time_scheduler.Utils.SaveUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ArrangeClassActivity extends AppCompatActivity {
    private final String TAG = "ArrangeClassActivity";
    Spinner spnrProgram, spnrSubject, spnrTeacher, spnrTime, spnrDay, spnrRoom;
    String selectedProgram, selectedSubject, selectedTeacher, selectedTime, selectedDay, selectedRoom;
    Button btnArrangeClass;

    ArrayAdapter<String> adapterProgram, adapterSubject, adapterTeacher, adapterTime, adapterDay, adapterRoom;

    List<ClassDetail> emptySlots;
    List<ClassDetail> TIME_TABLE;
    List<Room> completeRoomList;
    List<TimeSlot> completeTimeSlotList;

    List<String> listPrograms, listSubjects, listTeachers, listTime, listDay, listRooms;

    ProgressDialog progressDialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_class);
        setting();
        fetchAndFillData();

        btnArrangeClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchAllSpnrData();
                if (isDataValid()) {
                    int intDay = FormateDate.getInstance().getIndexByDay(selectedDay);
                    boolean slotAvailable = LinearSearch.getInstance().isAssignedClassInTimeTable(emptySlots, selectedRoom, intDay, selectedTime);
                    if (slotAvailable) {
                        boolean isTeacherAvailable = LinearSearch.getInstance().isTeacherAvailableDayTime(TIME_TABLE, intDay, selectedTime, selectedTeacher);
                        if (isTeacherAvailable) {
                            boolean isClassAvailable = LinearSearch.getInstance().isClassAvailableDayTime(TIME_TABLE, intDay, selectedTime, selectedProgram);
                            if (isClassAvailable) {
                                int dailyClassLimit = ListUtils.getInstance(ArrangeClassActivity.this).totalClassesForSingleDay(TIME_TABLE, intDay, selectedProgram);
                                if (dailyClassLimit <= 3) {
                                    addArrangeClassToTimeTable();
                                } else {
                                    Toast.makeText(ArrangeClassActivity.this, "This program  already have a lot of classes in this day", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ArrangeClassActivity.this, "Class is not available", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ArrangeClassActivity.this, "Teacher not available", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ArrangeClassActivity.this, "Slot not available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean isDataValid() {
        try {
            if (selectedDay.equals("") || selectedDay.equals("Select")) {
                Toast.makeText(this, "Select Day", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (selectedTime.equals("") || selectedTime.equals("Select")) {
                Toast.makeText(this, "Select time", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (selectedRoom.equals("") || selectedRoom.equals("Select")) {
                Toast.makeText(this, "Select room", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (selectedProgram.equals("") || selectedProgram.equals("Select")) {
                Toast.makeText(this, "Select class", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (selectedTeacher.equals("") || selectedTeacher.equals("Select")) {
                Toast.makeText(this, "Select teacher", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (selectedSubject.equals("") || selectedSubject.equals("Select")) {
                Toast.makeText(this, "Select subject", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void fetchAllSpnrData() {
        selectedProgram = (String) spnrProgram.getSelectedItem();
        selectedSubject = (String) spnrSubject.getSelectedItem();
        selectedTeacher = (String) spnrTeacher.getSelectedItem();

        selectedDay = (String) spnrDay.getSelectedItem();
        selectedTime = (String) spnrTime.getSelectedItem();
        selectedRoom = (String) spnrRoom.getSelectedItem();
    }

    private void addArrangeClassToTimeTable() {
        ClassDetail classDetail = new ClassDetail();
        classDetail.setTeachername(selectedTeacher);
        classDetail.setProgramme(selectedProgram);
        classDetail.setSubject(selectedSubject);

        classDetail.setRoom(selectedRoom);
        classDetail.setDayOfWeek(FormateDate.getInstance().getIndexByDay(selectedDay));

        int et = Integer.parseInt(selectedTime) + 150;
        String endTime = et + "";
        if (et < 1000) {
            endTime = "0" + et;
        }
        TimeSlot timeSlot = new TimeSlot(selectedTime, endTime);
        classDetail.setTimeSlot(timeSlot);

        classDetail.setSubcode("No code");
        classDetail.setChr("1.5");

        progressDialog.show();
        Date date = Calendar.getInstance().getTime();
        String key = date.getTime() + "";
        FirebaseDatabase.getInstance()
                .getReference("Admin")
                .child(Const.FB_DB_TIME_TABLE)
                .child(key)
                .setValue(classDetail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(ArrangeClassActivity.this, "Class added", Toast.LENGTH_SHORT).show();
                            NotificationUtils.getInstance().sendNotificationForArrangeClass(classDetail, ArrangeClassActivity.this);
                        } else {
                            Toast.makeText(ArrangeClassActivity.this, "Something wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    private void setting() {
        btnArrangeClass = findViewById(R.id.btn_arrange_class);
        spnrProgram = findViewById(R.id.spnr_arrange_class_class);
        spnrSubject = findViewById(R.id.spnr_arrange_class_subject);
        spnrTeacher = findViewById(R.id.spnr_arrange_class_teacher);
        spnrTime = findViewById(R.id.spnr_arrange_class_time);
        spnrDay = findViewById(R.id.spnr_arrange_class_day);
        spnrRoom = findViewById(R.id.spnr_arrange_class_room);
        emptySlots = new ArrayList<>();
        listPrograms = new ArrayList<>();
        listSubjects = new ArrayList<>();
        listTeachers = new ArrayList<>();
        listTime = new ArrayList<>();
        listDay = new ArrayList<>();
        listRooms = new ArrayList<>();
        TIME_TABLE = new ArrayList<>();
        completeRoomList = new ArrayList<>();
        completeTimeSlotList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Arranging...");
        progressDialog.setMessage("Your class is arranging. Please wait...");
    }

    private void setSpinnerListeners() {
        spnrDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDay = ((String) spnrDay.getSelectedItem());
                if (selectedDay != null) {
                    if (!selectedDay.equals("") || !selectedDay.equals("Select")) {
                        listTime = ListUtils.getInstance(ArrangeClassActivity.this).getAvailableTimeInEmptySlots(emptySlots, selectedDay);
                        adapterTime = new ArrayAdapter<String>(ArrangeClassActivity.this, android.R.layout.simple_spinner_dropdown_item, listTime);
                        spnrTime.setAdapter(adapterTime);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnrTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTime = ((String) spnrTime.getSelectedItem());
                if (selectedDay != null) {
                    if (!selectedDay.equals("") || !selectedDay.equals("Select")) {
                        if (selectedTime != null) {
                            if (!selectedTime.equals("") || !selectedTime.equals("Select")) {
                                listRooms = ListUtils.getInstance(ArrangeClassActivity.this).getAvailableRoomsInEmptySlots(emptySlots, selectedDay, selectedTime);
                                adapterRoom = new ArrayAdapter<String>(ArrangeClassActivity.this, android.R.layout.simple_spinner_dropdown_item, listRooms);
                                spnrRoom.setAdapter(adapterRoom);
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void resetAdapters() {
        adapterProgram = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listPrograms);
        adapterSubject = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listSubjects);
        adapterTeacher = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listTeachers);
        adapterDay = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listDay);

        spnrProgram.setAdapter(adapterProgram);
        spnrSubject.setAdapter(adapterSubject);
        spnrTeacher.setAdapter(adapterTeacher);
        spnrDay.setAdapter(adapterDay);

        setSpinnerListeners();
    }

    private void fetchAndFillData() {
        try {
            FirebaseDatabase.getInstance()
                    .getReference("Admin")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            listTeachers.clear();
                            listSubjects.clear();
                            listPrograms.clear();
                            emptySlots.clear();
                            TIME_TABLE.clear();
                            completeRoomList.clear();
                            completeTimeSlotList.clear();

                            DataSnapshot snapTeachers = snapshot.child("Teachers");
                            for (DataSnapshot item : snapTeachers.getChildren()) {
                                String teacherName = item.child("teachername").getValue().toString();
                                listTeachers.add(teacherName);
                            }

                            DataSnapshot snapSubjects = snapshot.child("Subjects");
                            for (DataSnapshot item : snapSubjects.getChildren()) {
                                String subject = item.child("subname").getValue().toString();
                                listSubjects.add(subject);
                            }

                            DataSnapshot snapProgram = snapshot.child("Programes");
                            for (DataSnapshot item : snapProgram.getChildren()) {
                                String prog = item.getValue().toString();
                                listPrograms.add(prog);
                            }

                            DataSnapshot snapRooms = snapshot.child("Rooms");
                            for (DataSnapshot itemR : snapRooms.getChildren()) {
                                String name = itemR.getValue().toString();
                                completeRoomList.add(new Room(name));
                                listRooms.add(name);
                            }

                            DataSnapshot snapTimeSlot = snapshot.child("Time Slots");
                            for (DataSnapshot itemT : snapTimeSlot.getChildren()) {
                                String endtime = itemT.child("end time").getValue().toString();
                                String starttime = itemT.child("start time").getValue().toString();
                                completeTimeSlotList.add(new TimeSlot(starttime, endtime));
                                listTime.add(starttime);
                            }

                            DataSnapshot snapTimeTable = snapshot.child(Const.FB_DB_TIME_TABLE);
                            for (DataSnapshot item : snapTimeTable.getChildren()) {
                                ClassDetail cd = item.getValue(ClassDetail.class);
                                TIME_TABLE.add(cd);
                            }
                            emptySlots = ListUtils.getInstance(ArrangeClassActivity.this)
                                    .getEmptySlots(TIME_TABLE, completeRoomList, completeTimeSlotList);
                            listDay = ListUtils.getInstance(ArrangeClassActivity.this).getAvailableDaysInEmptySlots(emptySlots);
                            resetAdapters();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ArrangeClassActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Some data not available", Toast.LENGTH_SHORT).show();
        }
    }

}