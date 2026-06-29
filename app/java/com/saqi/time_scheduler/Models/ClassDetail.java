package com.saqi.time_scheduler.Models;

public class ClassDetail {
    String teachername, programme, subject, subcode, Chr, room;
    TimeSlot timeSlot;
    int dayOfWeek;

    public ClassDetail() {
        this.teachername = "";
        this.programme = "";
        this.subject = "";
        this.subcode = "";
        Chr = "";
        timeSlot = new TimeSlot("", "");
        this.room = "";
        this.dayOfWeek = 1;
    }

    public ClassDetail(String teachername, String programme, String subject, String subcode, String chr, TimeSlot time, String room,int dayOfWeek) {
        this.teachername = teachername;
        this.programme = programme;
        this.subject = subject;
        this.subcode = subcode;
        Chr = chr;
        this.timeSlot = time;
        this.room = room;
        this.dayOfWeek = dayOfWeek;
    }


    public void addAssignTeacherToClassData(AssignTeacherToClass assignTeacherToClass) {
        this.teachername = assignTeacherToClass.getTeachername();
        this.programme = assignTeacherToClass.getProgramme();
        this.subject = assignTeacherToClass.getSubject();
        this.subcode = assignTeacherToClass.getSubcode();
        this.Chr = assignTeacherToClass.getChr();

    }

    public String getTeachername() {
        return teachername;
    }

    public void setTeachername(String teachername) {
        this.teachername = teachername;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubcode() {
        return subcode;
    }

    public void setSubcode(String subcode) {
        this.subcode = subcode;
    }

    public String getChr() {
        return Chr;
    }

    public void setChr(String chr) {
        Chr = chr;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
