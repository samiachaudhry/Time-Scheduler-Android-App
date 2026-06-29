package com.saqi.time_scheduler.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.saqi.time_scheduler.Models.ClassDetail;
import com.saqi.time_scheduler.Models.Room;
import com.saqi.time_scheduler.Models.Report;
import com.saqi.time_scheduler.Models.TimeSlot;
import com.saqi.time_scheduler.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListUtils {
    private final String TAG = ListUtils.class.getSimpleName();
    Context context;

    private ListUtils(Context context) {
        this.context = context;
    }

    private static ListUtils _INSTANCE;

    public static ListUtils getInstance(Context context) {
        if (_INSTANCE == null) _INSTANCE = new ListUtils(context);
        return _INSTANCE;
    }

    public List<ClassDetail> getEmptySlots(List<ClassDetail> timetable, List<Room> roomsList, List<TimeSlot> timeSlotList) {
        List<ClassDetail> emptySlotList = new ArrayList<>();
        if (roomsList.size() > 0 && timeSlotList.size() > 0) {
            for (int dayOfWeek = 1; dayOfWeek <= 5; dayOfWeek++) {
                for (TimeSlot timeslot : timeSlotList) {
                    for (Room room : roomsList) {
                        boolean isAssigned =
                                LinearSearch.getInstance().isAssignedClassInTimeTable(timetable, room.getName(), dayOfWeek, timeslot.getStartTime());
                        if (!isAssigned) {
                            ClassDetail classDetail = new ClassDetail();
                            classDetail.setDayOfWeek(dayOfWeek);
                            classDetail.setTimeSlot(timeslot);
                            classDetail.setRoom(room.getName());
                            emptySlotList.add(classDetail);
                        }
                    }
                }
            }
            return emptySlotList;
        } else {
            Toast.makeText(context, "Data not available", Toast.LENGTH_SHORT).show();
        }
        return emptySlotList;
    }

    public List<String> getAvailableDaysInEmptySlots(List<ClassDetail> emptySlots) {
        List<String> daysList = new ArrayList<>();
        for (int i = 0; i < emptySlots.size(); i++) {
            String day = FormateDate.getInstance().getDayByIndex(emptySlots.get(i).getDayOfWeek());
            boolean isAdded = LinearSearch.getInstance().isAvailable(daysList, day);
            if (!isAdded) {
                daysList.add(day);
            }
        }
        return daysList;
    }

    public List<ClassDetail> getClassByProgram(List<ClassDetail> timetable, String program) {
        List<ClassDetail> list = new ArrayList<>();
        for (int i = 0; i < timetable.size(); i++) {
            ClassDetail classDetail = timetable.get(i);
            if (classDetail.getProgramme().equals(program)) {
                list.add(classDetail);
            }
        }
        return list;
    }

    public List<ClassDetail> getClassByProgramOfCurrentDay(List<ClassDetail> timetable, String program) {
        Calendar calendar = Calendar.getInstance();
        List<ClassDetail> list = new ArrayList<>();

        for (int i = 0; i < timetable.size(); i++) {
            ClassDetail classDetail = timetable.get(i);
            if (classDetail.getProgramme().equals(program)) {
                if ((calendar.get(Calendar.DAY_OF_WEEK) - 1) == classDetail.getDayOfWeek()) {
//                if (1 == classDetail.getDayOfWeek()) {
                    list.add(classDetail);
                }
            }
        }
        return list;
    }

    public List<String> getAvailableTimeInEmptySlots(List<ClassDetail> emptySlots, String day) {
        List<String> timeList = new ArrayList<>();
        for (int i = 0; i < emptySlots.size(); i++) {
            ClassDetail cd = emptySlots.get(i);
            if (day.equals(FormateDate.getInstance().getDayByIndex(cd.getDayOfWeek()))) {
                TimeSlot timeSlot = cd.getTimeSlot();
                boolean isAdded = LinearSearch.getInstance().isAvailable(timeList, timeSlot.getStartTime());
                if (!isAdded) {
                    timeList.add(timeSlot.getStartTime());
                }
            }
        }
        return timeList;
    }

    public List<String> getAvailableRoomsInEmptySlots(List<ClassDetail> emptySlots, String day, String time) {
        List<String> roomsList = new ArrayList<>();
        for (int i = 0; i < emptySlots.size(); i++) {
            ClassDetail cd = emptySlots.get(i);
            if (day.equals(FormateDate.getInstance().getDayByIndex(cd.getDayOfWeek()))) {
                if (time.equals(cd.getTimeSlot().getStartTime())) {
                    String room = cd.getRoom();
                    boolean isAdded = LinearSearch.getInstance().isAvailable(roomsList, room);
                    if (!isAdded) {
                        roomsList.add(room);
                    }
                }
            }
        }
        return roomsList;
    }

    public int getTotalClassOfDay(List<ClassDetail> Timetable, int day) {
        int assigned = 0;
        for (int i = 0; i < Timetable.size(); i++) {
            ClassDetail cd = Timetable.get(i);
            if (cd.getDayOfWeek() == day) {
                assigned = assigned + 1;
            }
        }
        return assigned;
    }

    public boolean isAssignedClassInTimeTable(List<ClassDetail> timetable, String room, int day, String sTime) {
        for (int i = 0; i < timetable.size(); i++) {
            ClassDetail cd = timetable.get(i);
            if (cd.getRoom().equals(room) &&
                    Integer.parseInt(cd.getTimeSlot().getStartTime()) == Integer.parseInt(sTime) &&
                    cd.getDayOfWeek() == day) {
                return true;
            }
        }
        return false;
    }

    public boolean isTeacherAvailableDayTime(List<ClassDetail> timetable, int day, String sTime, String teacher) {
        for (int i = 0; i < timetable.size(); i++) {
            ClassDetail cd = timetable.get(i);
            if (Integer.parseInt(cd.getTimeSlot().getStartTime()) == Integer.parseInt(sTime) &&
                    cd.getDayOfWeek() == day &&
                    cd.getTeachername().equals(teacher)) {
                return false;
            }
        }
        return true;
    }

    public boolean isClassAvailableDayTime(List<ClassDetail> timetable, int day, String sTime, String program) {
        for (int i = 0; i < timetable.size(); i++) {
            ClassDetail cd = timetable.get(i);
            if (Integer.parseInt(cd.getTimeSlot().getStartTime()) == Integer.parseInt(sTime) &&
                    cd.getDayOfWeek() == day &&
                    cd.getProgramme().equals(program)) {
                return false;
            }
        }
        return true;
    }

    public int totalClassesForSingleDay(List<ClassDetail> timetable, int day, String program) {
        int totalClasses = 0;
        for (int i = 0; i < timetable.size(); i++) {
            ClassDetail cd = timetable.get(i);
            if (cd.getDayOfWeek() == day &&
                    cd.getProgramme().equals(program)) {
                totalClasses = totalClasses + 1;
            }
        }
        return totalClasses;
    }

    public List<Report> getReportComplete(List<ClassDetail> timeTable, String type) {
        List<Report> list = new ArrayList<>();

        Date currentDate = Calendar.getInstance().getTime();

        SimpleDateFormat df_day = new SimpleDateFormat("EEEE", Locale.getDefault());
        String currentDay = df_day.format(currentDate);
        int currentDayIndex = FormateDate.getInstance().getIndexByDay(currentDay);
        Log.i(TAG, "Current day: " + currentDay + " \tIndex:" + currentDayIndex);

        SimpleDateFormat df_time = new SimpleDateFormat("HHmm", Locale.getDefault());
        int currentTime = Integer.parseInt(df_time.format(currentDate));
        Log.i(TAG, "Current Time: " + currentTime);


        for (int i = 0; i < timeTable.size(); i++) {
            int pos = -1;
            String name = "";
            TimeSlot timeSlot = timeTable.get(i).getTimeSlot();
            int dayOfWeek = timeTable.get(i).getDayOfWeek();
            if (type.equals("Teacher")) {
                name = timeTable.get(i).getTeachername();
                pos = findPosition(list, name);
            } else if (type.equals("Class")) {
                name = timeTable.get(i).getProgramme();
                pos = findPosition(list, name);
            } else if (type.equals("Subject")) {
                name = timeTable.get(i).getSubject();
                pos = findPosition(list, name);
            } else {
                continue;
            }
            if (pos == -1) {
                Report report = new Report(name, 1, 0);
                if (dayOfWeek > currentDayIndex) {
                    report.increaseRemainingClass();
                } else if (dayOfWeek == currentDayIndex) {
                    if (Integer.parseInt(timeSlot.getEndTime()) > currentTime) {
                        report.increaseRemainingClass();
                    }
                }
                list.add(report);
            } else {
                Report data = list.remove(pos);
                data.increaseWeeklyClass();
                if (dayOfWeek > currentDayIndex) {
                    data.increaseRemainingClass();
                } else if (dayOfWeek == currentDayIndex) {
                    if (Integer.parseInt(timeSlot.getEndTime()) > currentTime) {
                        data.increaseRemainingClass();
                    }
                }
                list.add(data);
            }
        }

        return list;
    }

    private int findPosition(List<Report> list, String name) {
        int pos = -1;
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).getName().equals(name)) {
                return i;
            }
        }
        return pos;
    }

}
