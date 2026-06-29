package com.saqi.time_scheduler.Utils;

import android.content.Context;
import android.util.Log;

import com.saqi.time_scheduler.Models.ClassDetail;

import java.util.List;

public class PrintTimeTable {
    private final String TAG = "PrintTimeTable";
    Context context;

    private static PrintTimeTable _INSTANCE;

    public static PrintTimeTable getInstance(Context context) {
        if (_INSTANCE == null) {
            _INSTANCE = new PrintTimeTable(context);
        }
        return _INSTANCE;
    }

    public PrintTimeTable(Context context) {
        this.context = context;
    }

    public String timetableToString(List<ClassDetail> TIME_TABLE) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < TIME_TABLE.size(); i++) {
            stringBuilder
                    .append(TIME_TABLE.get(i).getRoom())
                    .append("\t::\t").append("Day ").append(TIME_TABLE.get(i).getDayOfWeek()).append(" --> ").append(TIME_TABLE.get(i).getTimeSlot().getStartTime()).append(":").append(TIME_TABLE.get(i).getTimeSlot().getEndTime())
                    .append("\t:: ").append(TIME_TABLE.get(i).getProgramme())
                    .append(" ::\t\tSubject: ").append(TIME_TABLE.get(i).getSubject())
                    .append("\t\t\t\t--->>>\t\tTeacher: ").append(TIME_TABLE.get(i).getTeachername())
                    .append("\n");
        }
        Log.i(TAG, stringBuilder.toString());
        return stringBuilder.toString();
        //  generateNoteOnSD(fileName, stringBuilder.toString());
    }

    private void printEmptySlots(List<ClassDetail> emptySlotList) {
        for (int i = 0; i < emptySlotList.size(); i++) {
            String msg = "Day: " + emptySlotList.get(i).getDayOfWeek() +
                    "       Time: " + emptySlotList.get(i).getTimeSlot().getStartTime() +
                    "       Room: " + emptySlotList.get(i).getRoom();
            Log.i(TAG, msg);
        }
    }

}
