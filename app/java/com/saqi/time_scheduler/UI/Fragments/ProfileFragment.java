package com.saqi.time_scheduler.UI.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saqi.time_scheduler.Constants.Const;
import com.saqi.time_scheduler.Models.User;
import com.saqi.time_scheduler.R;
import com.saqi.time_scheduler.UI.Admin.AdminHome;
import com.saqi.time_scheduler.UI.Login;
import com.saqi.time_scheduler.UI.SplashScreen;
import com.saqi.time_scheduler.UI.Teacher.ArrangeClassActivity;
import com.saqi.time_scheduler.Utils.SaveUser;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    private final String TAG = "ProfileFragment";

    LinearLayout LL_Logout, LL_Admin_panel, LL_Teacher_panel;
    TextView tvUsername, tvType, tvPhone, tvEmail, tvRollno, tvProgram;
    CircleImageView civProfilePic;

    ImageView imgProgramEdit;

    ProgressDialog progressDialog, progressDialogPrograms;
    List<String> listPrograms;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setting(view);
        User user = SaveUser.getInstance(getActivity()).fetchUser();
        setUserData(user);
        setTeacherData(user);
        setAdminData(user);

        imgProgramEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchPrograms();
            }
        });

        LL_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                SaveUser.getInstance(getActivity()).signOut();
                getActivity().finishAffinity();
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
            }
        });

        tvProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchPrograms();
            }
        });
        return view;
    }


    private void setting(View v) {
        LL_Logout = v.findViewById(R.id.LL_profile_logout);
        LL_Admin_panel = v.findViewById(R.id.LL_profile_admin_panel);
        LL_Teacher_panel = v.findViewById(R.id.LL_profile_teacher_panel);
        civProfilePic = v.findViewById(R.id.civ_profile_pic);
        tvUsername = v.findViewById(R.id.tv_profile_name);
        tvType = v.findViewById(R.id.tv_profile_type);
        tvEmail = v.findViewById(R.id.tv_profile_email);
        tvPhone = v.findViewById(R.id.tv_profile_phone);
        tvRollno = v.findViewById(R.id.tv_profile_rollno);
        tvProgram = v.findViewById(R.id.tv_profile_program);
        imgProgramEdit = v.findViewById(R.id.img_frag_profile_editProgram);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Verifying");
        progressDialog.setMessage("Please wait for verification");
        progressDialogPrograms = new ProgressDialog(getActivity());
        progressDialogPrograms.setCancelable(true);
        progressDialogPrograms.setTitle("Programs");
        progressDialogPrograms.setMessage("Available programs are loading...");
        listPrograms = new ArrayList<>();
    }

    private void setUserData(User user) {
        if (!user.getUid().equals("")) {
            Glide.with(getActivity()).load(user.getImageURL()).centerCrop().into(civProfilePic);
            Log.i(TAG, "imageURL:" + user.getImageURL());
            tvUsername.setText(user.getUsername());
            tvType.setText("(" + user.getType() + ")");
            tvPhone.setText(user.getPhone());
            tvEmail.setText(user.getEmail());
            tvRollno.setText(user.getRollno());
            if (user.getProgram().equals("") || user.getProgram().equals("None")) {
                tvProgram.setText("Not selected");
            } else {
                tvProgram.setText(user.getProgram());
            }
        }

    }

    private void setTeacherData(User user) {
        if (user.getType().equals(Const.Teacher)) {
            LL_Teacher_panel.setVisibility(View.VISIBLE);
            LL_Teacher_panel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.show();
                    FirebaseDatabase.getInstance()
                            .getReference(Const.FB_DB_USERS)
                            .child(user.getUid())
                            .child("type")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    progressDialog.dismiss();
                                    if (snapshot.getValue().toString().equals(Const.Teacher)) {
//                                            Toast.makeText(getActivity(), "You are teacher", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), ArrangeClassActivity.class));
                                    } else if (snapshot.getValue().toString().equals(Const.ACCESS_ADMIN)) {
                                        Toast.makeText(getActivity(), "Please login again!", Toast.LENGTH_SHORT).show();
                                        FirebaseAuth.getInstance().signOut();
                                        SaveUser.getInstance(getActivity()).signOut();
                                        getActivity().finishAffinity();
                                        startActivity(new Intent(getActivity(), Login.class));
                                        getActivity().finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                }
            });
        } else {
            LL_Teacher_panel.setVisibility(View.GONE);
        }
    }

    private void setAdminData(User user) {
        if (user.getAccess().equals(Const.ACCESS_ADMIN)) {
            LL_Admin_panel.setVisibility(View.VISIBLE);
            LL_Admin_panel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.show();

                    FirebaseDatabase.getInstance()
                            .getReference(Const.FB_DB_USERS)
                            .child(user.getUid())
                            .child("access")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    progressDialog.dismiss();

                                    if (snapshot.getValue().toString().equals(Const.ACCESS_ADMIN)) {

                                        startActivity(new Intent(getActivity(), AdminHome.class));
                                    } else if (snapshot.getValue().toString().equals(Const.ACCESS_USER)) {
                                        Toast.makeText(getActivity(), "Please login again!", Toast.LENGTH_SHORT).show();
                                        FirebaseAuth.getInstance().signOut();
                                        SaveUser.getInstance(getActivity()).signOut();
                                        getActivity().finishAffinity();
                                        startActivity(new Intent(getActivity(), Login.class));
                                        getActivity().finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                }
            });
        } else {
            LL_Admin_panel.setVisibility(View.GONE);
        }
    }


    public void fetchPrograms() {
        progressDialogPrograms.show();
        FirebaseDatabase.getInstance()
                .getReference(Const.FB_DB_ADMIN)
                .child(Const.FB_DB_ADMIN_PROGRAMS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialogPrograms.dismiss();
                        listPrograms.clear();
                        listPrograms.add("None");
                        if (snapshot.exists()) {
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                try {
                                    String program = snap.getValue().toString();
                                    listPrograms.add(program);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        showPopupGettingProgram();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialogPrograms.dismiss();
                        Toast.makeText(getActivity(), "Something wrong!", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, error.toString());
                    }
                });
    }


    public void showPopupGettingProgram() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.popup_get_program);
        dialog.setCancelable(true);
        LinearLayout LLAccept = dialog.findViewById(R.id.LL_popup_accept);
        TextView tvClose = dialog.findViewById(R.id.tv_popup_close);
        Spinner spnrProgram = dialog.findViewById(R.id.spnr_popup_programs);

        ArrayAdapter<String> adapterProgram =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listPrograms);
        spnrProgram.setAdapter(adapterProgram);


        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        LLAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedProgram = (String) spnrProgram.getSelectedItem();
                Log.i(TAG, "Selected Program: " + selectedProgram);
                User user = SaveUser.getInstance(getActivity()).fetchUser();
                user.setProgram(selectedProgram);
                SaveUser.getInstance(getActivity()).save(user);
                updateUserProgram(user.getUid(), selectedProgram);
                setUserData(user);
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void updateUserProgram(String uid, String selectedProgram) {
        FirebaseDatabase.getInstance()
                .getReference(Const.FB_DB_USERS)
                .child(uid)
                .child(Const.FB_DB_USERS_PROGRAM)
                .setValue(selectedProgram);
    }


}