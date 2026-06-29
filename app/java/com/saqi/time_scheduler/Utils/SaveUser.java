package com.saqi.time_scheduler.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.saqi.time_scheduler.Models.User;

public class SaveUser {
    private final String SHARED_PREF_NAME = "USERPREF";
    private final String SHARED_UID = "UID";
    private final String SHARED_USERNAME = "username";
    private final String SHARED_EMAIL = "email";
    private final String SHARED_PASSWORD = "pass";
    private final String SHARED_Rollno = "rollno";
    private final String SHARED_PHONE = "phone";
    private final String SHARED_IMAGE = "imageURL";
    private final String SHARED_TYPE = "type";
    private final String SHARED_ACCESS = "access";
    private final String SHARED_STATUS = "status";

    private final String SHARED_PROGRAM = "program";

    Context context;

    private static SaveUser _INSTANCE;

    public static SaveUser getInstance(Context context) {
        if (_INSTANCE == null) _INSTANCE = new SaveUser(context);
        return _INSTANCE;
    }

    private SaveUser(Context context) {
        this.context = context;
    }


    public void save(User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_UID, user.getUid());
        editor.putString(SHARED_USERNAME, user.getUsername());
        editor.putString(SHARED_IMAGE, user.getImageURL());
        editor.putString(SHARED_EMAIL, user.getEmail());
        editor.putString(SHARED_PHONE, user.getPhone());

        editor.putString(SHARED_Rollno, user.getRollno());

        editor.putString(SHARED_PASSWORD, user.getPassword());

        editor.putString(SHARED_TYPE, user.getType());
        editor.putString(SHARED_ACCESS, user.getAccess());
        editor.putString(SHARED_STATUS, user.getStatus());
        editor.putString(SHARED_PROGRAM, user.getProgram());
        editor.apply();
    }

    public User fetchUser() {
        User userData = new User();
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userData.setUid(sharedPreferences.getString(SHARED_UID, ""));
        userData.setUsername(sharedPreferences.getString(SHARED_USERNAME, ""));
        userData.setEmail(sharedPreferences.getString(SHARED_EMAIL, ""));
        userData.setPhone(sharedPreferences.getString(SHARED_PHONE, ""));

        userData.setRollno(sharedPreferences.getString(SHARED_Rollno, ""));

        userData.setPassword(sharedPreferences.getString(SHARED_PASSWORD, ""));

        userData.setType(sharedPreferences.getString(SHARED_TYPE, ""));
        userData.setImageURL(sharedPreferences.getString(SHARED_IMAGE, ""));
        userData.setAccess(sharedPreferences.getString(SHARED_ACCESS, ""));
        userData.setStatus(sharedPreferences.getString(SHARED_STATUS, ""));
        userData.setProgram(sharedPreferences.getString(SHARED_PROGRAM, ""));
        return userData;
    }

    public void signOut() {
        User user = new User();
        save(user);
    }
}
