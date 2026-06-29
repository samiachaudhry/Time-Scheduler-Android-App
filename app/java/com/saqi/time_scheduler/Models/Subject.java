package com.saqi.time_scheduler.Models;

public class Subject {

    private String subname, subcode;

    public Subject(String subname, String subcode) {
        this.subname = subname;
        this.subcode = subcode;

    }

    public Subject() {
    }

    public String getSubname() {
        return subname;
    }

    public void setSubname(String subname) {
        this.subname = subname;
    }

    public String getSubcode() {
        return subcode;
    }

    public void setSubcode(String subcode) {
        this.subcode = subcode;
    }


}
