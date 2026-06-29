package com.saqi.time_scheduler.Services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saqi.time_scheduler.Constants.Const;
import com.saqi.time_scheduler.Models.ClassDetail;
import com.saqi.time_scheduler.Notifications.Notifications;
import com.saqi.time_scheduler.R;
import com.saqi.time_scheduler.UI.Login;
import com.saqi.time_scheduler.UI.SplashScreen;
import com.saqi.time_scheduler.Utils.FormateDate;
import com.saqi.time_scheduler.Utils.ListUtils;
import com.saqi.time_scheduler.Utils.SaveUser;

import java.util.ArrayList;
import java.util.List;

public class MyNotificationBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = "MyNotification";
    NotificationManagerCompat managerCompatSMS;
    private final String CHANNEL_ID = "scNotifications";
    private final String CHANNEL_NAME = "SC Notifications";
    private final String CHANNEL_DESC = "Sending and Receiving Notifications";

    List<ClassDetail> list;
    List<ClassDetail> TIMETABLE;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received");
        fetchTimeTable(context);
    }

    private void showTimeTableNotification(Context context, String title, String body) {
        Notifications.createNotificationChannel(context);
        String text = "";

        Intent intent = new Intent(context, SplashScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                100,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logosplashh)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                        .setPriority(NotificationCompat.PRIORITY_MAX);

        managerCompatSMS = NotificationManagerCompat.from(context);
        managerCompatSMS.notify(5, builder.build());
    }


    private void fetchTimeTable(Context context) {
        FirebaseDatabase.getInstance()
                .getReference("Admin")
                .child(Const.FB_DB_TIME_TABLE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        TIMETABLE = new ArrayList<>();
                        Log.i(TAG, "Timetable received");

                        if (!snapshot.exists()) {
                            return;
                        }

                        for (DataSnapshot snap : snapshot.getChildren()) {
                            ClassDetail classDetail = snap.getValue(ClassDetail.class);
                            TIMETABLE.add(classDetail);
                        }
                        checkForUserProgram(context);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, error.toString());
                    }
                });
    }

    private void checkForUserProgram(Context context) {
        String program = SaveUser.getInstance(context).fetchUser().getProgram();
        if (program.equals("")) {
            Log.i(TAG, "Program empty");

        } else {
            list = ListUtils.getInstance(context).getClassByProgramOfCurrentDay(TIMETABLE, program);
            Log.i(TAG, "Daily class =" + list.size());
            generateDataForNotification(context);
        }

    }

    private void generateDataForNotification(Context context) {
        String title = "Time Table";
        String body = "";

        for (int i = 0; i < list.size(); i++) {
            ClassDetail cd = list.get(i);
            title = "Time Table of " + FormateDate.getInstance().getDayByIndex(cd.getDayOfWeek());


            body = body + cd.getSubject() + " " + cd.getTeachername() + " " + cd.getTimeSlot().getStartTime() + " " + cd.getRoom();
            body = body + "\n\n";
        }

        if (list.size() > 0)
            showTimeTableNotification(context, title, body);
    }

}
