package com.saqi.time_scheduler.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.saqi.time_scheduler.Models.ClassDetail;
import com.saqi.time_scheduler.Models.MyTime;
import com.saqi.time_scheduler.R;
import com.saqi.time_scheduler.Utils.FormateDate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

//class detail adapter
public class ClassDetailAdapter extends RecyclerView.Adapter<ClassDetailAdapter.MyViewHolder> {
    Context context;
    List<ClassDetail> list;

    public ClassDetailAdapter(Context context, List<ClassDetail> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.template_class_detail_timetable, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ClassDetail classDetail = list.get(position);
        holder.tvDay.setText(FormateDate.getInstance().getDayByIndex(classDetail.getDayOfWeek()));

        MyTime t = FormateDate.getInstance().formatTime(classDetail.getTimeSlot().getStartTime());
        if (t != null) {
            holder.tvTime.setText(t.getHour() + ":" + t.getMint() + " " + t.getAMPM());
        } else {
            holder.tvTime.setText(classDetail.getTimeSlot().getStartTime());
        }


        holder.tvRoom.setText(classDetail.getRoom());
        holder.tvSubject.setText(classDetail.getSubject());
        holder.tvTeacher.setText(classDetail.getTeachername());

        Calendar calendar = Calendar.getInstance();
        if ((calendar.get(Calendar.DAY_OF_WEEK) - 1) == classDetail.getDayOfWeek()) {
            holder.RL_header.setBackgroundColor(context.getResources().getColor(R.color.green_22_400));
        }

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTeacher, tvSubject, tvDay, tvTime, tvRoom;
        CardView card;
        RelativeLayout RL_header;

        public MyViewHolder(@NonNull View v) {
            super(v);
            tvTeacher = v.findViewById(R.id.tv_temp_timetable_teacher);
            tvSubject = v.findViewById(R.id.tv_temp_timetable_subject);
            tvDay = v.findViewById(R.id.tv_temp_timetable_day);
            tvTime = v.findViewById(R.id.tv_temp_timetable_time);
            tvRoom = v.findViewById(R.id.tv_temp_timetable_room);
            card = v.findViewById(R.id.card_temp_timetable);
            RL_header = v.findViewById(R.id.RL_temp_timetable_header);
        }
    }
}
