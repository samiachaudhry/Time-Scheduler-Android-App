package com.saqi.time_scheduler.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.saqi.time_scheduler.Models.Subject;
import com.saqi.time_scheduler.R;

import java.util.List;

public class SubjectsAdapter extends BaseAdapter {
    Context context;
    List<Subject> subjectList;

    public SubjectsAdapter(@NonNull Context context, List<Subject> list) {
        this.subjectList = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return subjectList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.template_subject_dropdown, null);
        TextView txtSubCode = view.findViewById(R.id.txt_temp_subject_code);
        TextView txtSubName = view.findViewById(R.id.txt_temp_subject_name);
        txtSubCode.setText(subjectList.get(position).getSubcode());
        txtSubName.setText(subjectList.get(position).getSubname());
        return view;
    }
}
