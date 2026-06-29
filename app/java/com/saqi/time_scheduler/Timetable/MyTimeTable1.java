package com.saqi.time_scheduler.Timetable;

import android.content.Context;
import android.util.Log;

import com.saqi.time_scheduler.Models.AssignTeacherToClass;
import com.saqi.time_scheduler.Models.ClassDetail;
import com.saqi.time_scheduler.Models.Room;
import com.saqi.time_scheduler.Models.Teacher;
import com.saqi.time_scheduler.Models.TimeSlot;
import com.saqi.time_scheduler.Utils.LinearSearch;
import com.saqi.time_scheduler.Utils.QueueForAssignTeachers;

import java.util.ArrayList;
import java.util.List;

public class MyTimeTable1 extends TimeTable {
    private final String TAG = "MyTimeTable1";
    private int classesAssigned = 0;

    public MyTimeTable1(Context context, List<AssignTeacherToClass> assignTeacherToClassList,
                           List<Teacher> teacherList, List<Room> roomsList,
                           List<TimeSlot> timeSlotList, int dailyClassLimit) {
        super(context, assignTeacherToClassList, teacherList, roomsList, timeSlotList, dailyClassLimit);
    }

    @Override
    public List<ClassDetail> generate() {
        QueueForAssignTeachers queue = new QueueForAssignTeachers(assignTeacherToClassList);
        List<AssignTeacherToClass> tempList = new ArrayList<>();
        List<String> progListTodayClasses = new ArrayList<>();
        List<String> teacherListTodayClasses = new ArrayList<>();

        for (int dayOfWeek = 1; dayOfWeek <= 5; dayOfWeek++) {
            classesAssigned = 0;
            if (queue.isEmpty()) break;
            for (TimeSlot timeslot : timeSlotList) {
                if (classesAssigned >= DailyClassLimit) break;
                if (queue.isEmpty()) break;
                progListTodayClasses.clear();
                teacherListTodayClasses.clear();
                int sTime = Integer.parseInt(timeslot.getStartTime());
                int eTime = Integer.parseInt(timeslot.getEndTime());
                for (Room room : roomsList) {
                    tempList.clear();
                    if (classesAssigned >= DailyClassLimit) break;
                    if (queue.isEmpty()) break;


                    for (int s = 0; s < queue.size(); s++) {
                        AssignTeacherToClass currentClass = queue.pop();
                        if (progListTodayClasses.size() > 0 && teacherListTodayClasses.size() > 0) {
                            if (LinearSearch.getInstance().isAvailable(progListTodayClasses, currentClass.getProgramme())
                                    || LinearSearch.getInstance().isAvailable(teacherListTodayClasses, currentClass.getTeachername())
                            ) {

                                tempList.add(currentClass);
                            } else {
                                ClassDetail classDetail = new ClassDetail();
                                classDetail.addAssignTeacherToClassData(currentClass);
                                classDetail.setRoom(room.getName());
                                classDetail.setTimeSlot(timeslot);
                                classDetail.setDayOfWeek(dayOfWeek);
                                TIME_TABLE.add(classDetail);
                                progListTodayClasses.add(classDetail.getProgramme());
                                teacherListTodayClasses.add(classDetail.getTeachername());

                                double currentClassChr = Double.parseDouble(currentClass.getChr());

                                double currentClassTimeDuration = 1.5;
                                double remainChrOfClass = currentClassChr - currentClassTimeDuration;
                                if (remainChrOfClass > 0) {
                                    currentClass.setChr(String.valueOf(remainChrOfClass));
                                    queue.push(currentClass);
                                }
                                classesAssigned++;

                                break;
                            }
                        } else {
                            ClassDetail classDetail = new ClassDetail();
                            classDetail.addAssignTeacherToClassData(currentClass);
                            classDetail.setRoom(room.getName());
                            classDetail.setTimeSlot(timeslot);
                            classDetail.setDayOfWeek(dayOfWeek);
                            TIME_TABLE.add(classDetail);
                            progListTodayClasses.add(classDetail.getProgramme());
                            teacherListTodayClasses.add(classDetail.getTeachername());

                            double currentClassChr = Double.parseDouble(currentClass.getChr());

                            double currentClassTimeDuration = 1.5;
                            double remainChrOfClass = currentClassChr - currentClassTimeDuration;
                            if (remainChrOfClass > 0) {
                                currentClass.setChr(String.valueOf(remainChrOfClass));
                                queue.push(currentClass);
                            }
                            classesAssigned++;

                            break;
                        }
                    }
                    for (int tq = 0; tq < tempList.size(); tq++) {
                        queue.push(tempList.get(tq));
                    }
                }
            }
        }
        Log.i(TAG, "Remaining:" + queue.size());
        return TIME_TABLE;
    }

}
