package com.saqi.time_scheduler.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saqi.time_scheduler.Constants.Const;
import com.saqi.time_scheduler.R;
import com.saqi.time_scheduler.Models.User;
import com.saqi.time_scheduler.UI.Fragments.MyTimeTableFragment;
import com.saqi.time_scheduler.UI.Fragments.ProfileFragment;
import com.saqi.time_scheduler.UI.Fragments.TimeTableFragment;
import com.saqi.time_scheduler.Utils.FDUtils;
import com.saqi.time_scheduler.Utils.SaveUser;


import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "MainActivity";

    String role;
    DrawerLayout drawer;
    NavigationView navigationView;
    private ActionBarDrawerToggle mToggle;

    TextView tvUsername, tvRollno;
    CircleImageView civProfilePic;
    ProgressDialog progressDialog;
    User user;
    MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setting();
        bottomNavigation = findViewById(R.id.MBN_main);
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_bn_timetable));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_bn_my_timetable));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_bn_profile));
        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                Fragment fragment = new MyTimeTableFragment();
                switch (item.getId()) {
                    case 1:
                        fragment = new TimeTableFragment();
                        break;
                    case 2:
                        fragment = new MyTimeTableFragment();
                        break;
                    case 3:
                        fragment = new ProfileFragment();
                        break;
                }
                openFragment(fragment);
            }
        });
        bottomNavigation.show(2, true);

        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                switch (item.getId()) {
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                }

            }
        });
        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
            }
        } else {

        }

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mToggle = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawer.addDrawerListener(mToggle);
        mToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        updateNavHeaderView();

    }

    private void updateNavHeaderView() {
        User user = SaveUser.getInstance(this).fetchUser();
        View v = navigationView.getHeaderView(0);

        civProfilePic = v.findViewById(R.id.civ_profile_pic);
        tvUsername = v.findViewById(R.id.tv_profile_name);
        tvRollno = v.findViewById(R.id.tv_profile_rollno);

        civProfilePic = v.findViewById(R.id.civ_profile_pic);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Verifying");
        progressDialog.setMessage("Please wait for verification");

        if (!user.getUid().equals("")) {
            Glide.with(this).load(user.getImageURL()).centerCrop().into(civProfilePic);
            Log.i(TAG, "imageURL:" + user.getImageURL());
            tvUsername.setText(user.getUsername());
            tvRollno.setText(user.getRollno());
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_export_timetable:
                FDUtils.getInstance().fetchDataAndExport(MainActivity.this);
                break;


            case R.id.nav_feedback:
                startActivity(new Intent(MainActivity.this, FeedbackActivity.class));
                break;
            case R.id.nav_logout:
                logout();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        SaveUser.getInstance(this).signOut();
        FirebaseAuth.getInstance().signOut();
        finishAffinity();
        startActivity(new Intent(this, Login.class));
        finish();
    }


    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setting() {
        user = SaveUser.getInstance(this).fetchUser();
        if (FirebaseAuth.getInstance().getUid() == null) {
            finish();
        }
        if (user.getType().equals("") || user.getUid().equals("") || user.getAccess().equals("")) {
            finish();
        }

        setUserManagement(user);

    }

    private void setUserManagement(User user) {
        FirebaseDatabase.getInstance()
                .getReference(Const.FB_DB_USERS)
                .child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            User u = snapshot.getValue(User.class);
                            try {
                                if (u.getStatus().equals(Const.STATUS_BLOCKED)) {
                                    logoutThisUser();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                logoutThisUser();
                            }
                        } else {
                            logoutThisUser();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, error.toString());
                    }
                });
    }

    private void logoutThisUser() {
        FirebaseAuth.getInstance().signOut();
        SaveUser.getInstance(this).signOut();

        this.finishAffinity();
        startActivity(new Intent(this, Login.class));
        finish();
    }
}
