package com.saqi.time_scheduler.Utils;

import com.saqi.time_scheduler.Models.ClassDetail;
import com.saqi.time_scheduler.Models.Subject;

import java.util.List;

public class LinearSearch {

    public static LinearSearch _INSTANCE;

    public static LinearSearch getInstance() {
        if (_INSTANCE == null) {
            _INSTANCE = new LinearSearch();
        }
        return _INSTANCE;
    }

    public boolean isAvailable(List<String> list, String key) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(key)) {
                return true;
            }
        }
        return false;
    }

    public int getIndexOfSubject(List<Subject> list, String key) {
        boolean available = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getSubname().equals(key)) {
                return i;
            }
        }
        return -1;
    }

    public int getIndex(List<String> list, String key) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(key)) {
                return i;
            }
        }
        return -1;
    }

    public int getIndexOfSubjectCode(List<Subject> list, String key) {
        boolean available = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getSubcode().equals(key)) {
                return i;
            }
        }
        return -1;
    }

    public ClassDetail isAvailableClass(List<ClassDetail> list, String room, int day, String sTime) {
        for (int i = 0; i < list.size(); i++) {
            ClassDetail cd = list.get(i);
            if (cd.getRoom().equals(room) &&
                    Integer.parseInt(cd.getTimeSlot().getStartTime()) == Integer.parseInt(sTime) &&
                    cd.getDayOfWeek() == day) {
                return cd;
            }
        }
        return null;
    }

    public boolean isAssignedClassInTimeTable(List<ClassDetail> list, String room, int day, String sTime) {
        for (int i = 0; i < list.size(); i++) {
            ClassDetail cd = list.get(i);
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
}
