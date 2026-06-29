package com.saqi.time_scheduler.UI;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.saqi.time_scheduler.Constants.Const;
import com.saqi.time_scheduler.Models.*;
import com.saqi.time_scheduler.R;
import com.saqi.time_scheduler.Utils.SaveUser;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {

    private final String TAG = "Signup";
    private final int PERMISSION_REQUEST_CODE = 200;
    private final String DB_USERS = "Users";
    Button btnreg, btnlogin;
    CircleImageView civProfilePic;
    Spinner spinnerUserType;
    EditText etUsername, etEmail, etPhone,   etPass, etConfPass,etrollno;
    String username, email, phone,  pass, confPass,rollno, type = "Student";
    ProgressDialog progressDialog;
    Uri URI_OF_SELECTED_IMAGE, URI_URL_OF_IMAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setting();
        listeners();
    }

    private void listeners() {
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchInputData();
                if (isValidInputData()) {
                    progressDialog.show();
                    createAccount();
                }
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        civProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picImageFromGallery();
            }
        });

        spinnerUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = ((TextView) view).getText().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void createAccount() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    uploadProfilePic(FirebaseAuth.getInstance().getUid());
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SignUp.this, "Account not created!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void uploadProfilePic(String uid) {
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("profilePics/" + (uid));
        reference.putFile(URI_OF_SELECTED_IMAGE)
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return reference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    URI_URL_OF_IMAGE = task.getResult();
                    createDatabaseRecode(uid);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SignUp.this, "Image not uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createDatabaseRecode(String uid) {
        User user = new User(uid,username, email,  pass,rollno,phone,
                URI_URL_OF_IMAGE.toString(), type,Const.ACCESS_USER,Const.STATUS_ACTIVE);
        FirebaseDatabase.getInstance()
                .getReference(DB_USERS)
                .child(uid)
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            SaveUser.getInstance(SignUp.this).save(user);
                            Toast.makeText(SignUp.this, "Account created", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(SignUp.this, "Error in creating account", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setting() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Signing up");
        progressDialog.setMessage("Your account is creating.");
        btnreg = findViewById(R.id.btn_signup);
        btnlogin = findViewById(R.id.btn_login);

        etUsername = findViewById(R.id.txtname);
        etEmail = findViewById(R.id.txtemail);
        etPhone = findViewById(R.id.txtphone);

        etPass = findViewById(R.id.txtpass);
        etConfPass = findViewById(R.id.txtconpass);
        etrollno = findViewById(R.id.txtroll);
        civProfilePic = findViewById(R.id.civ_signup_pic);
        spinnerUserType = findViewById(R.id.spin_role);

        List<String> listUserType = new ArrayList<>();
        listUserType.add(Const.Student);
        listUserType.add(Const.Teacher);
        ArrayAdapter<String> adapterUserType = new ArrayAdapter<String>(this, R.layout.template_spinner_usertype, listUserType);
        spinnerUserType.setAdapter(adapterUserType);
    }

    private void fetchInputData() {
        username = etUsername.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        phone = etPhone.getText().toString().trim();

        pass = etPass.getText().toString().trim();
        confPass = etConfPass.getText().toString().trim();
        rollno = etrollno.getText().toString().trim();
    }

    private boolean isValidInputData() {
        if (URI_OF_SELECTED_IMAGE == null) {
            Toast.makeText(this, "Please select your profile pic", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (username.equals("")) {
            etUsername.setError("Please enter username");
            return false;
        }
        if (email.equals("")) {
            etEmail.setError("Please enter your email");
            return false;
        }


        if (pass.equals("")) {
            etPass.setError("Please enter password for security");
            return false;
        }
        if (confPass.equals("")) {
            etConfPass.setError("Please retype your password");
            return false;
        }
        if (pass.length() < 6) {
            etPass.setError("Please enter a stronger password");
            return false;
        }
        if (!pass.equals(confPass)) {
            etConfPass.setError("Password didn't match");
            return false;
        }
        if (rollno.equals("")) {
            etPhone.setError("Please enter your Roll number");
            return false;
        }
        if (phone.equals("")) {
            etPhone.setError("Please enter your contact number");
            return false;
        }
        return true;
    }

    private void picImageFromGallery() {
        CropImage.startPickImageActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                URI_OF_SELECTED_IMAGE = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMultiTouchEnabled(true)
                        .start(this);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    if (result != null) {
                        URI_OF_SELECTED_IMAGE = result.getUri();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                        civProfilePic.setImageBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}