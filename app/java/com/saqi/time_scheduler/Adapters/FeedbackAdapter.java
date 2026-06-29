package com.saqi.time_scheduler.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saqi.time_scheduler.Constants.Const;
import com.saqi.time_scheduler.Models.AssignTeacherToClass;
import com.saqi.time_scheduler.Models.Feedback;
import com.saqi.time_scheduler.Models.User;
import com.saqi.time_scheduler.R;
import com.saqi.time_scheduler.UI.Admin.AssignTeachers;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.MyViewHolder> {
    private final String TAG = FeedbackAdapter.class.getSimpleName();
    Context context;
    List<Feedback> list;

    public FeedbackAdapter(Context context, List<Feedback> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.template_feedback, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Feedback feedback = list.get(position);

        Long d = Long.parseLong(feedback.getTimeStamp());
        DateFormat dateFormat = new DateFormat();
        String dd = dateFormat.format("yyyy-MM-dd hh:mm:ss aa", new Date(d)).toString();
        holder.tvTime.setText(dd);
        holder.tvFeedback.setText(feedback.getFeedback());

        setPicAndName(feedback, holder);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void setPicAndName(Feedback feedback, MyViewHolder holder) {
        FirebaseDatabase.getInstance().getReference(Const.FB_DB_USERS)
                .child(feedback.getUID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            holder.tvName.setText(user.getUsername());
                            Glide.with(context).load(user.getImageURL()).centerCrop().into(holder.civProfilePic);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, error.toString());
                    }
                });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTime, tvFeedback;
        CircleImageView civProfilePic;
        CardView card;

        public MyViewHolder(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_temp_name);
            tvFeedback = v.findViewById(R.id.tv_temp_feedback);
            tvTime = v.findViewById(R.id.tv_temp_time);
            civProfilePic = v.findViewById(R.id.civ_temp_profile_pic);
            card = v.findViewById(R.id.card);
        }
    }
}
