package com.saqi.time_scheduler.Timetable;

import android.content.Context;
import android.util.Log;

import com.saqi.time_scheduler.Models.AssignTeacherToClass;
import com.saqi.time_scheduler.Models.ClassDetail;
import com.saqi.time_scheduler.Models.Room;
import com.saqi.time_scheduler.Models.Teacher;
import com.saqi.time_scheduler.Models.TimeSlot;
import com.saqi.time_scheduler.Utils.LinearSearch;
import com.saqi.time_scheduler.Utils.ListUtils;
import com.saqi.time_scheduler.Utils.QueueForAssignTeachers;

import java.util.ArrayList;
import java.util.List;

public class MyTimeTable2 extends TimeTable {
    private final String TAG = "MyTimeTable2";
    private int classesAssigned = 0;
    private final int maxClassInSingleDay = 3;

    public MyTimeTable2(Context context, List<AssignTeacherToClass> assignTeacherToClassList,
                        List<Teacher> teacherList, List<Room> roomsList,
                        List<TimeSlot> timeSlotList, int dailyClassLimit) {
        super(context, assignTeacherToClassList, teacherList, roomsList, timeSlotList, dailyClassLimit);
    }

    @Override
    public List<ClassDetail> generate() {
        QueueForAssignTeachers queue = new QueueForAssignTeachers(assignTeacherToClassList);

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

                    if (classesAssigned >= DailyClassLimit) break;
                    if (queue.isEmpty()) break;

                    AssignTeacherToClass currentClass = queue.pop();
                    if (progListTodayClasses.size() > 0 && teacherListTodayClasses.size() > 0) {
                        if (LinearSearch.getInstance().isAvailable(progListTodayClasses, currentClass.getProgramme()) ||
                                LinearSearch.getInstance().isAvailable(teacherListTodayClasses, currentClass.getTeachername()) ||
                                ListUtils.getInstance(context).totalClassesForSingleDay(TIME_TABLE, dayOfWeek, currentClass.getProgramme()) > maxClassInSingleDay
                        ) {
                            queue.push(currentClass);

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
//                            double currentClassTimeDuration = eTime - sTime;
                            double currentClassTimeDuration = 1.5;
                            double remainChrOfClass = currentClassChr - currentClassTimeDuration;
                            if (remainChrOfClass > 0) {
                                currentClass.setChr(String.valueOf(remainChrOfClass));
                                queue.push(currentClass);
                            }
                            classesAssigned++;
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
                    }
                }
            }
        }
        Log.i(TAG, "Remaining:" + queue.size());
        if (queue.size() > 0) {
            fillRemainingClasses(queue);
        }
        return TIME_TABLE;
    }

    private void fillRemainingClasses(QueueForAssignTeachers queue) {
        List<AssignTeacherToClass> tempList = new ArrayList<>();

        for (int dayOfWeek = 1; dayOfWeek <= 5; dayOfWeek++) {
            classesAssigned = ListUtils.getInstance(context).getTotalClassOfDay(TIME_TABLE, dayOfWeek);
            if (queue.isEmpty()) break;
            for (TimeSlot timeslot : timeSlotList) {
                if (classesAssigned >= DailyClassLimit) break;
                if (queue.isEmpty()) break;

                for (Room room : roomsList) {
                    tempList.clear();
                    if (classesAssigned >= DailyClassLimit) break;
                    if (queue.isEmpty()) break;

                    if (!ListUtils.getInstance(context).isAssignedClassInTimeTable(TIME_TABLE, room.getName(), dayOfWeek, timeslot.getStartTime())) {

                        for (int i = 0; i < queue.size(); i++) {
                            AssignTeacherToClass currentClass = queue.pop();
                            String teacher = currentClass.getTeachername();
                            String program = currentClass.getProgramme();
                            if (ListUtils.getInstance(context).isTeacherAvailableDayTime(TIME_TABLE, dayOfWeek, timeslot.getStartTime(), teacher) &&
                                    ListUtils.getInstance(context).isClassAvailableDayTime(TIME_TABLE, dayOfWeek, timeslot.getStartTime(), program) &&
                                    ListUtils.getInstance(context).totalClassesForSingleDay(TIME_TABLE, dayOfWeek, currentClass.getProgramme()) <= maxClassInSingleDay) {
                                ClassDetail classDetail = new ClassDetail();
                                classDetail.addAssignTeacherToClassData(currentClass);
                                classDetail.setRoom(room.getName());
                                classDetail.setTimeSlot(timeslot);
                                classDetail.setDayOfWeek(dayOfWeek);
                                TIME_TABLE.add(classDetail);

                                double currentClassChr = Double.parseDouble(currentClass.getChr());
                                double currentClassTimeDuration = 1.5;
                                double remainChrOfClass = currentClassChr - currentClassTimeDuration;
                                if (remainChrOfClass > 0) {
                                    currentClass.setChr(String.valueOf(remainChrOfClass));
                                    queue.push(currentClass);
                                }
                                classesAssigned++;
                                break;
                            } else {
                                tempList.add(currentClass);
                            }
                        }
                        for (int j = 0; j < tempList.size(); j++) {
                            queue.push(tempList.get(j));
                        }
                    }
                }
            }
        }
        Log.i(TAG, "Remaining:" + queue.size());
    }

}
