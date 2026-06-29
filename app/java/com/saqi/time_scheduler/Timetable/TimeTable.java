package com.saqi.time_scheduler.Timetable;

import android.content.Context;

import com.saqi.time_scheduler.Models.AssignTeacherToClass;
import com.saqi.time_scheduler.Models.ClassDetail;
import com.saqi.time_scheduler.Models.Room;
import com.saqi.time_scheduler.Models.Teacher;
import com.saqi.time_scheduler.Models.TimeSlot;

import java.util.ArrayList;
import java.util.List;

public abstract class TimeTable {
    private final String TAG = "TimeTable";
    protected int DailyClassLimit = 50;
    protected Context context;
    protected List<AssignTeacherToClass> assignTeacherToClassList;
    protected List<Teacher> teacherList;
    protected List<Room> roomsList;
    protected List<TimeSlot> timeSlotList;
    protected List<ClassDetail> TIME_TABLE;

    protected TimeTable(Context context, List<AssignTeacherToClass> assignTeacherToClassList,
                        List<Teacher> teacherList, List<Room> roomsList,
                        List<TimeSlot> timeSlotList, int dailyClassLimit) {
        this.context = context;
        this.assignTeacherToClassList = assignTeacherToClassList;
        this.teacherList = teacherList;
        this.roomsList = roomsList;
        this.timeSlotList = timeSlotList;
        this.TIME_TABLE = new ArrayList<>();
        this.DailyClassLimit = dailyClassLimit;
    }

    protected abstract List<ClassDetail> generate();
}
