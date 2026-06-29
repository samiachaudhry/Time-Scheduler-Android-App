package com.saqi.time_scheduler.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.saqi.time_scheduler.UI.Admin.AssignTeachers;
import com.saqi.time_scheduler.Models.AssignTeacherToClass;
import com.saqi.time_scheduler.R;

import java.util.List;

public class AssignTeachersAdapter extends RecyclerView.Adapter<AssignTeachersAdapter.MyViewHolder> {
    Context context;
    List<AssignTeacherToClass> list;

    public AssignTeachersAdapter(Context context, List<AssignTeacherToClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.template_assign_teacherdata, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AssignTeacherToClass currentUser = list.get(position);
        holder.tvteachername.setText(currentUser.getTeachername());
        holder.tvsubjectname.setText(currentUser.getSubject());
        holder.tvprogramme.setText(currentUser.getProgramme());
        holder.cardAssignTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AssignTeachers.class);
                intent.putExtra("data", currentUser);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvteachername, tvsubjectname, tvprogramme;
        CardView cardAssignTeacher;

        public MyViewHolder(@NonNull View v) {
            super(v);
            tvteachername = v.findViewById(R.id.tv_teacher_name);
            tvsubjectname = v.findViewById(R.id.tv_subject_name);
            tvprogramme = v.findViewById(R.id.tv_programme);
            cardAssignTeacher = v.findViewById(R.id.card_assign_teacher);
        }
    }
}
