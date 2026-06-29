package com.saqi.time_scheduler.Models;

public class MyTime {
    String hour,mint,AMPM;

    public MyTime() {
    }

    public MyTime(String hour, String mint, String AMPM) {
        this.hour = hour;
        this.mint = mint;
        this.AMPM = AMPM;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMint() {
        return mint;
    }

    public void setMint(String mint) {
        this.mint = mint;
    }

    public String getAMPM() {
        return AMPM;
    }

    public void setAMPM(String AMPM) {
        this.AMPM = AMPM;
    }
}
