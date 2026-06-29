package com.saqi.time_scheduler.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.saqi.time_scheduler.Models.Report;
import com.saqi.time_scheduler.R;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {
    Context context;
    List<Report> list;

    public ReportAdapter(Context context, List<Report> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.template_report, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Report currentData = list.get(position);
        holder.tvName.setText(currentData.getName());
        holder.tvTotalClasses.setText(currentData.getRemainingClasses() + "/" + currentData.getWeeklyClasses() + "");
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTotalClasses;
        CardView card;

        public MyViewHolder(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_name);
            tvTotalClasses = v.findViewById(R.id.tv_totalClasses);
            card = v.findViewById(R.id.card);
        }
    }
}
