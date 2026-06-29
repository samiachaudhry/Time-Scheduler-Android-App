package com.saqi.time_scheduler.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.saqi.time_scheduler.Constants.Const;
import com.saqi.time_scheduler.Models.User;
import com.saqi.time_scheduler.R;
import com.saqi.time_scheduler.Services.MyNotificationBroadcastReceiver;
import com.saqi.time_scheduler.Utils.SaveUser;
import com.saqi.time_scheduler.Utils.Utils;

import java.util.Calendar;


public class Login extends AppCompatActivity {
    private final String TAG = "Login";
    Button btn1, btnreg, btnForgotPassword;

    EditText editText, etPassword;
    FirebaseAuth auth;
    ProgressDialog progressDialog, progressDialogForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Utils.getInstance().requestAllPermissions(this);

        editText = findViewById(R.id.txtname);
        etPassword = findViewById(R.id.txtpass);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Log In");
        progressDialog.setMessage("You account is being login. please wait...");

        progressDialogForgot = new ProgressDialog(this);
        progressDialogForgot.setCancelable(true);
        progressDialogForgot.setTitle("Reset Password");
        progressDialogForgot.setMessage("Reset password link is sending in your Email Box. please wait...");

        btn1 = findViewById(R.id.btn_login);
        btnreg = findViewById(R.id.btn_signup);
        btnForgotPassword = findViewById(R.id.forget);
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, SignUp.class));

            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    firebaseSignIn();
                }
            }
        });

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupForgotPassword();
            }
        });
        checkForAlreadyLogin();
    }

    private void firebaseSignIn() {
        String email = editText.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        progressDialog.show();

        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //startActivity(new Intent(Login.this, AdminHome.class));
                            //finish();
                            fetchAndSaveUserData(task.getResult().getUser().getUid());
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Login.this, "email or password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fetchAndSaveUserData(String uid) {
        FirebaseDatabase.getInstance()
                .getReference(Const.FB_DB_USERS)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Log.i(TAG, user.getType());
                        SaveUser.getInstance(Login.this).save(user);
                        saveUserMessagingToken(user);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, error.toString());
                        progressDialog.dismiss();
                    }
                });
    }

    private void checkForAlreadyLogin() {
        String uid = SaveUser.getInstance(this).fetchUser().getUid();
        if (!uid.equals("")) {
            uid = FirebaseAuth.getInstance().getUid();
            if (uid != null) {
                finishAffinity();
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
            }
        }
    }

    private boolean isValid() {
        boolean isValid = true;
        if (editText.getText().toString().isEmpty()) {
            editText.setError("Please enter email");
            isValid = false;
        } else if (etPassword.getText().toString().isEmpty()) {
            etPassword.setError("Please enter password");
            isValid = false;
        }
        return isValid;
    }

    private void saveUserMessagingToken(User user) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    DatabaseReference db = null;
                    if (user.getAccess().equals(Const.ACCESS_ADMIN)) {
                        db = FirebaseDatabase.getInstance()
                                .getReference(Const.FB_MESSAGE_TOKEN)
                                .child(Const.FB_DB_ADMIN)
                                .child(user.getUid());
                    } else if (user.getType().equals(Const.Teacher)) {
                        db = FirebaseDatabase.getInstance()
                                .getReference(Const.FB_MESSAGE_TOKEN)
                                .child(Const.Teacher)
                                .child(user.getUid());
                    } else {
                        String program = user.getProgram();
                        if (program.equals("")) {
                            db = FirebaseDatabase.getInstance()
                                    .getReference(Const.FB_MESSAGE_TOKEN)
                                    .child(Const.FB_DB_USERS)
                                    .child(Const.FB_MESSAGE_TOKEN_USER_NO_PROGRAM)
                                    .child(user.getUid());
                        } else {
                            db = FirebaseDatabase.getInstance()
                                    .getReference(Const.FB_MESSAGE_TOKEN)
                                    .child(Const.FB_DB_USERS)
                                    .child(program)
                                    .child(user.getUid());
                        }
                    }


                    if (db == null) {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, "Something wrong in your account", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.setValue(task.getResult())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Something wrong!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    registerForDailyNotifications();
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    finish();
                                }
                            });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Something wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerForDailyNotifications() {
        Log.i(TAG, "registerForDailyNotifications start");
        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();

        calSet.set(Calendar.HOUR_OF_DAY, 6);
        calSet.set(Calendar.MINUTE, 30);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);



        Intent intent = new Intent(getBaseContext(), MyNotificationBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 1, intent, 0);

        Log.i(TAG, "registerForDailyNotifications: Alarm service creating");
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.i(TAG, "registerForDailyNotifications: Alarm Set");
    }

    public void showPopupForgotPassword() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_forget_password);
        dialog.setCancelable(true);
        LinearLayout LLSend = dialog.findViewById(R.id.LL_popup_accept);
        TextView tvClose = dialog.findViewById(R.id.tv_popup_close);
        EditText etEmail = dialog.findViewById(R.id.et_email);

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        LLSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                if (email.equals("")) {
                    etEmail.setError("Please enter your email");
                    return;
                }
                dialog.dismiss();
                progressDialogForgot.show();
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialogForgot.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Reset password link is sent!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Login.this, "Something went wrong in sending reset link", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}