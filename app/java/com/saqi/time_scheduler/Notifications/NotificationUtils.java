package com.saqi.time_scheduler.Notifications;

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
import com.saqi.time_scheduler.Models.Room;
import com.saqi.time_scheduler.Models.Teacher;
import com.saqi.time_scheduler.Models.TimeSlot;
import com.saqi.time_scheduler.Models.User;
import com.saqi.time_scheduler.UI.Teacher.ArrangeClassActivity;
import com.saqi.time_scheduler.Utils.FormateDate;
import com.saqi.time_scheduler.Utils.SaveFile;
import com.saqi.time_scheduler.Utils.SaveUser;

import java.util.ArrayList;
import java.util.List;

public class NotificationUtils {

    private NotificationUtils() {
    }

    private static NotificationUtils _INSTANCE;

    public static NotificationUtils getInstance() {
        if (_INSTANCE == null) _INSTANCE = new NotificationUtils();
        return _INSTANCE;
    }

    public void sendNotificationForArrangeClass(ClassDetail classDetail, Activity activity) {
        MessageNotificationData notificationData =
                new MessageNotificationData(classDetail.getTeachername(), classDetail.getSubject(), classDetail.getRoom(),
                        classDetail.getTimeSlot().getStartTime(), Const.NOTIFICATION_ARRANGE_CLASS,
                        classDetail.getProgramme(), FormateDate.getInstance().getDayByIndex(classDetail.getDayOfWeek()));

        Notifications.sendNotificationToSpecificProgram(notificationData, classDetail.getProgramme(), activity);
    }

    public void sendNotificationForFeedback(String feedback, Activity activity) {
        User user = SaveUser.getInstance(activity).fetchUser();
        MessageNotificationData notificationData =
                new MessageNotificationData(user.getUid(), user.getUsername(), user.getImageURL(),
                        feedback, Const.NOTIFICATION_ADMIN_FEEDBACK_RECEIVED,
                        user.getProgram(), "");
        Notifications.sendNotificationToAdmins(notificationData, activity);
    }

    public void sendNotificationDaily(String program, String dayOfWeek, String msg, Activity activity) {
        MessageNotificationData notificationData =
                new MessageNotificationData("", "", "",
                        msg, Const.NOTIFICATION_DAILY_CLASSES,
                        program, dayOfWeek);
        Notifications.sendNotificationToSpecificProgram(notificationData, program, activity);
    }
}
