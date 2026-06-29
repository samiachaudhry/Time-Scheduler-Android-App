package com.saqi.time_scheduler.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
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

import java.util.ArrayList;
import java.util.List;

public class Utils {

    private Utils() {
    }

    private static Utils _INSTANCE;

    public static Utils getInstance() {
        if (_INSTANCE == null) _INSTANCE = new Utils();
        return _INSTANCE;
    }

    public void requestAllPermissions(Activity context) {
        context.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.SET_ALARM,
                Manifest.permission.RECEIVE_BOOT_COMPLETED}, 505);
    }
}
