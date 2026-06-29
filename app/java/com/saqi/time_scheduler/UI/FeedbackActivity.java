package com.saqi.time_scheduler.UI;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.saqi.time_scheduler.Constants.Const;
import com.saqi.time_scheduler.Models.Feedback;
import com.saqi.time_scheduler.Notifications.NotificationUtils;
import com.saqi.time_scheduler.R;
import com.saqi.time_scheduler.Utils.SaveUser;

import java.util.Calendar;


public class FeedbackActivity extends AppCompatActivity {
    private final String TAG = FeedbackActivity.class.getSimpleName();
    Button btnSubmit;
    EditText etFeedback;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        etFeedback = findViewById(R.id.et_feedback);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Feedback");
        progressDialog.setMessage("Your feedback is submitting...");

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedbackMsg = etFeedback.getText().toString().trim();
                if (feedbackMsg.equals("")) {
                    etFeedback.setError("Write the feedback");
                    return;
                }
                String timeStamp = String.valueOf(Calendar.getInstance().getTime().getTime());
                String UID = SaveUser.getInstance(FeedbackActivity.this).fetchUser().getUid();
                Feedback feedback = new Feedback(UID, feedbackMsg, timeStamp);
                progressDialog.show();
                FirebaseDatabase.getInstance().getReference(Const.FB_FEEDBACKS)
                        .child(UID)
                        .child(timeStamp)
                        .setValue(feedback)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(FeedbackActivity.this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                                    NotificationUtils.getInstance().sendNotificationForFeedback(feedbackMsg, FeedbackActivity.this);
                                    finish();
                                } else {
                                    Toast.makeText(FeedbackActivity.this, "Something wrong in submitting feedback", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}