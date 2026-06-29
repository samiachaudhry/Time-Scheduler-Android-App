package com.saqi.time_scheduler.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;
import com.saqi.time_scheduler.Constants.Const;
import com.saqi.time_scheduler.Models.MyTime;
import com.saqi.time_scheduler.R;
import com.saqi.time_scheduler.UI.SplashScreen;
import com.saqi.time_scheduler.Utils.FormateDate;
import com.saqi.time_scheduler.Utils.SaveUser;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notifications {
    private static final String TAG = "Notifications";
    private Context context;
    private static NotificationManagerCompat managerCompatSMS;

    public Notifications(Context context) {
        this.context = context;
    }

    private static final String CHANNEL_ID = "scNotifications";
    private static final String CHANNEL_NAME = "SC Notifications";
    private static final String CHANNEL_DESC = "Sending and Receiving Notifications";


    static void displayNotifications(Context context, RemoteMessage remoteMessage) {
        Log.i(TAG, "Display Notification");
        createNotificationChannel(context);
        String title = "";
        String text = "";
        Intent intent = new Intent(context, SplashScreen.class);

        if (remoteMessage.getData().get("notificationType").equals(Const.NOTIFICATION_ARRANGE_CLASS)) {
            String teacherName = remoteMessage.getData().get("receiverID");
            String subject = remoteMessage.getData().get("receiverName");
            String room = remoteMessage.getData().get("receiverImage");
            String startTime = remoteMessage.getData().get("message");
            String program = remoteMessage.getData().get("extras");
            String day = remoteMessage.getData().get("day");


            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    100,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            MyTime t = FormateDate.getInstance().formatTime(startTime);
            String tt = startTime;
            if (t != null) {
                tt = t.getHour() + ":" + t.getMint() + " " + t.getAMPM();
            }
            String bodyText = teacherName + " arrange a class on " + day + " of " + subject + "'s class in " + room + " at " + tt;

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.logosplashh)
                            .setContentTitle("Class arranged")
                            .setContentText(bodyText)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                            .setPriority(NotificationCompat.PRIORITY_MAX);

            managerCompatSMS = NotificationManagerCompat.from(context);
            managerCompatSMS.notify(1, builder.build());

        } else if (remoteMessage.getData().get("notificationType").equals(Const.NOTIFICATION_ADMIN_FEEDBACK_RECEIVED)) {
            String UID = remoteMessage.getData().get("receiverID");
            String name = remoteMessage.getData().get("receiverName");
            String image = remoteMessage.getData().get("receiverImage");
            String feedbackMsg = remoteMessage.getData().get("message");
            String program = remoteMessage.getData().get("extras");
            String day = remoteMessage.getData().get("day");


            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    101,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            String bodyText = name + " from (" + program + ") write the feedback for your app\n\"" + feedbackMsg + "\"";

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.logosplashh)
                            .setContentTitle("Feedback Received")
                            .setContentText(bodyText)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                            .setPriority(NotificationCompat.PRIORITY_MAX);

            managerCompatSMS = NotificationManagerCompat.from(context);
            managerCompatSMS.notify(2, builder.build());

        } else if (remoteMessage.getData().get("notificationType").equals(Const.NOTIFICATION_DAILY_CLASSES)) {
            String UID = remoteMessage.getData().get("receiverID");
            String name = remoteMessage.getData().get("receiverName");
            String image = remoteMessage.getData().get("receiverImage");
            String msg = remoteMessage.getData().get("message");
            String program = remoteMessage.getData().get("extras");
            String day = remoteMessage.getData().get("day");


            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    104,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            if (msg.equals("")) {
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.logosplashh)
                                .setContentTitle(day + "'s Classes")
                                .setContentText("Good News!\nToday you have no lecture")
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                                .setPriority(NotificationCompat.PRIORITY_MAX);
                managerCompatSMS = NotificationManagerCompat.from(context);
                managerCompatSMS.notify(5, builder.build());
            } else {
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.logosplashh)
                                .setContentTitle(day + "'s Classes")
                                .setContentText(msg)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                                .setPriority(NotificationCompat.PRIORITY_MAX);
                managerCompatSMS = NotificationManagerCompat.from(context);
                managerCompatSMS.notify(5, builder.build());
            }

        } else if (remoteMessage.getData().get("notificationType").equals(Const.NOTIFICATION_BASIC_FOR_ALL)) {
            String notificationTitle = remoteMessage.getData().get("receiverID");
            String subject = remoteMessage.getData().get("receiverName");
            String room = remoteMessage.getData().get("receiverImage");
            String startTime = remoteMessage.getData().get("message");
            String notificationBody = remoteMessage.getData().get("extras");
            String day = remoteMessage.getData().get("day");


            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    102,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.logosplashh)
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationBody)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                            .setPriority(NotificationCompat.PRIORITY_MAX);

            managerCompatSMS = NotificationManagerCompat.from(context);
            managerCompatSMS.notify(3, builder.build());
        }
    }

    public static void cancelAllSMSNotifications() {
        if (managerCompatSMS != null) {
            managerCompatSMS.cancelAll();
        }
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }


    public static void sendNotification(final MessageNotificationData data,
                                        final ArrayList<String> targetUserList, final Context context) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        final APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Const.FB_MESSAGE_TOKEN)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                for (String userID : targetUserList) {


                                    if (userSnapshot.getKey().equals(userID)) {


                                        String fcmToken = userSnapshot.getValue().toString();

                                        NotificationSenderModel sender = new NotificationSenderModel(data, fcmToken);
                                        apiService.sendNotifcation(sender).enqueue(new Callback<MessageResponseModel>() {
                                            @Override
                                            public void onResponse(Call<MessageResponseModel> call, Response<MessageResponseModel> response) {
                                                if (response.code() == 200) {
                                                    if (response.body().success != 1) {
                                                        progressDialog.dismiss();

                                                    } else {
                                                        progressDialog.dismiss();

                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<MessageResponseModel> call, Throwable t) {
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void sendNotification(final MessageNotificationData data,
                                        final String targetUser, final Context context) {

        Log.d(TAG, "Sending...");
        final ProgressDialog progressDialog = new ProgressDialog(context);
        final APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Const.FB_MESSAGE_TOKEN)
                .child(targetUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String fcmToken = dataSnapshot.getValue().toString();
                            Log.d(TAG, "FCM : " + fcmToken);

                            NotificationSenderModel sender = new NotificationSenderModel(data, fcmToken);
                            apiService.sendNotifcation(sender)
                                    .enqueue(new Callback<MessageResponseModel>() {
                                        @Override
                                        public void onResponse(Call<MessageResponseModel> call, Response<MessageResponseModel> response) {
                                            if (response.code() == 200) {
                                                Log.d(TAG, "Response : " + response.code());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MessageResponseModel> call, Throwable t) {
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public static void sendNotificationToSpecificProgram(final MessageNotificationData data, final String program, final Context context) {
        Log.d(TAG, "Sending...");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Const.FB_MESSAGE_TOKEN)
                .child(Const.FB_DB_USERS)
                .child(program)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                String fcmToken = snap.getValue().toString();
                                Log.d(TAG, "FCM : " + fcmToken);
                                sendNotification(data, fcmToken);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public static void sendNotificationToAdmins(final MessageNotificationData data, final Context context) {
        Log.d(TAG, "Sending...");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(Const.FB_MESSAGE_TOKEN)
                .child(Const.FB_DB_ADMIN)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                String fcmToken = snap.getValue().toString();
                                Log.d(TAG, "FCM : " + fcmToken);
                                sendNotification(data, fcmToken);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public static void sendNotificationViaToken(final MessageNotificationData data,
                                                final String token, final Context context) {

        Log.d(TAG, "Sending...");
        final APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        NotificationSenderModel sender = new NotificationSenderModel(data, token);
        apiService.sendNotifcation(sender).enqueue(new Callback<MessageResponseModel>() {
            @Override
            public void onResponse(Call<MessageResponseModel> call, Response<MessageResponseModel> response) {
                if (response.code() == 200) {
                    Log.d(TAG, "Response : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MessageResponseModel> call, Throwable t) {
            }
        });

    }


    private static void sendNotification(final MessageNotificationData data, final String fcmToken) {
        final APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        NotificationSenderModel sender = new NotificationSenderModel(data, fcmToken);
        apiService.sendNotifcation(sender)
                .enqueue(new Callback<MessageResponseModel>() {
                    @Override
                    public void onResponse(Call<MessageResponseModel> call, Response<MessageResponseModel> response) {
                        if (response.code() == 200) {
                            Log.d(TAG, "Response : " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<MessageResponseModel> call, Throwable t) {
                    }
                });
    }
}
