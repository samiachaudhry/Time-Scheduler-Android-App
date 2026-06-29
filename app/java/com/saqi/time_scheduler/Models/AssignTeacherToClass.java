package com.saqi.time_scheduler.Models;

import java.io.Serializable;

public class AssignTeacherToClass implements Serializable {
    String teachername, programme, subject, subcode, Chr;
    String id;

    public AssignTeacherToClass() {
    }

    public AssignTeacherToClass(String teachername, String programme, String subject, String subcode, String chr) {
        this.teachername = teachername;
        this.programme = programme;
        this.subject = subject;
        this.subcode = subcode;
        Chr = chr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
