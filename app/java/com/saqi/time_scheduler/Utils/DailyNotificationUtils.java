package com.saqi.time_scheduler.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saqi.time_scheduler.Constants.Const;
import com.saqi.time_scheduler.Models.AssignTeacherToClass;
import com.saqi.time_scheduler.Models.ClassDetail;
import com.saqi.time_scheduler.Models.MyTime;
import com.saqi.time_scheduler.Models.Room;
import com.saqi.time_scheduler.Models.Teacher;
import com.saqi.time_scheduler.Models.TimeSlot;
import com.saqi.time_scheduler.Notifications.NotificationUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyNotificationUtils {
    private final String TAG = DailyNotificationUtils.class.getSimpleName();
    List<String> listOfPrograms;
    List<ClassDetail> TIME_TABLE;
    Activity activity;

    private DailyNotificationUtils(Activity activity) {
        listOfPrograms = new ArrayList<>();
        TIME_TABLE = new ArrayList<>();
        this.activity = activity;
    }

    private static DailyNotificationUtils _INSTANCE;

    public static DailyNotificationUtils getInstance(Activity activity) {
        if (_INSTANCE == null) _INSTANCE = new DailyNotificationUtils(activity);
        return _INSTANCE;
    }

    public void fetchTimeTableAndSendNotifications() {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Notifications");
        progressDialog.setMessage("Daily classes notifications are sending. Please wait a moment...");
        progressDialog.show();
        listOfPrograms = new ArrayList<>();
        TIME_TABLE = new ArrayList<>();

        FirebaseDatabase.getInstance()
                .getReference("Admin")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listOfPrograms.clear();
                        TIME_TABLE.clear();

                        if (!snapshot.exists()) return;

                        DataSnapshot snapProgram = snapshot.child("Programes");
                        if (snapProgram.exists()) {
                            for (DataSnapshot item : snapProgram.getChildren()) {
                                String prog = item.getValue().toString();
                                listOfPrograms.add(prog);
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Something wrong", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        DataSnapshot snapTimeTable = snapshot.child(Const.FB_DB_TIME_TABLE);
                        if (snapTimeTable.exists()) {
                            for (DataSnapshot item : snapTimeTable.getChildren()) {
                                ClassDetail cd = item.getValue(ClassDetail.class);
                                TIME_TABLE.add(cd);
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Something wrong", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        createNotificationForAllPrograms(progressDialog);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, error.toString());
                    }
                });
    }

    private void createNotificationForAllPrograms(ProgressDialog progressDialog) {
        for (int i = 0; i < listOfPrograms.size(); i++) {
            String program = listOfPrograms.get(i);
            List<ClassDetail> list = ListUtils.getInstance(activity).getClassByProgramOfCurrentDay(TIME_TABLE, program);
            String msg = "";
            String dayOfWeek = FormateDate.getInstance().getDayByIndex(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
            for (int j = 0; j < list.size(); j++) {
                ClassDetail cd = list.get(j);

                MyTime t = FormateDate.getInstance().formatTime(cd.getTimeSlot().getStartTime());
                String ST = cd.getTimeSlot().getStartTime();
                if (t != null) {
                    ST = t.getHour() + ":" + t.getMint() + " " + t.getAMPM();
                }
                msg = msg + cd.getSubject() + " by " + cd.getTeachername() + " at " + ST + " in " + cd.getRoom();
                msg = msg + "\n";
            }
            NotificationUtils.getInstance().sendNotificationDaily(program, dayOfWeek, msg, activity);
        }
        progressDialog.dismiss();
        Toast.makeText(activity, "Notifications sent", Toast.LENGTH_SHORT).show();
    }

}
