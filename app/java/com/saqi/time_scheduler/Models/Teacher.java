package com.saqi.time_scheduler.Models;

public class Teacher {
    String teachername, department, arrivaltime, leavetime;

    public Teacher() {
    }

    public Teacher(String teachername, String department, String arrivaltime, String leavetime) {
        this.teachername = teachername;
        this.department = department;
        this.arrivaltime = arrivaltime;
        this.leavetime = leavetime;
    }

    public String getTeachername() {
        return teachername;
    }

    public void setTeachername(String teachername) {
        this.teachername = teachername;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getArrivaltime() {
        return arrivaltime;
    }

    public void setArrivaltime(String arrivaltime) {
        this.arrivaltime = arrivaltime;
    }

    public String getLeavetime() {
        return leavetime;
    }

    public void setLeavetime(String leavetime) {
        this.leavetime = leavetime;
    }
}
