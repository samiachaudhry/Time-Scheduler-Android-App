package com.saqi.time_scheduler.Utils;

import android.util.Log;

import com.saqi.time_scheduler.Models.ClassDetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sorting {
    private final String TAG = "Sorting";
    private static Sorting _INSTANCE;

    public static Sorting getInstance() {
        if (_INSTANCE == null) _INSTANCE = new Sorting();
        return _INSTANCE;
    }

    public List<ClassDetail> sortAscendingByDay(List<ClassDetail> timetable1) {
        ClassDetail[] list = new ClassDetail[timetable1.size()];
        for (int k = 0; k < timetable1.size(); k++) {
            list[k] = timetable1.get(k);
        }
        int size = list.length;
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < (size - 1); i++) {
                Log.i(TAG, "j=" + j + " :: i=" + i);
                ClassDetail cs1 = list[i];
                ClassDetail cs2 = list[i + 1];
                if (cs1.getDayOfWeek() > cs2.getDayOfWeek()) {
                    ClassDetail temp = list[i];
                    list[i] = list[i + 1];
                    list[i + 1] = temp;
                }
            }
        }
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < (size - 1); i++) {
                ClassDetail cs1 = list[i];
                ClassDetail cs2 = list[i + 1];
                if (cs1.getDayOfWeek() == cs2.getDayOfWeek()) {
                    int time1 = Integer.parseInt(cs1.getTimeSlot().getStartTime());
                    int time2 = Integer.parseInt(cs2.getTimeSlot().getStartTime());
                    if (time1 > time2) {
                        ClassDetail temp = list[i];
                        list[i] = list[i + 1];
                        list[i + 1] = temp;
                    }
                }
            }
        }
        List<ClassDetail> temp = new ArrayList<>();
        for (int k = 0; k < size; k++) {
            temp.add(list[k]);
        }
        return temp;
    }

}
