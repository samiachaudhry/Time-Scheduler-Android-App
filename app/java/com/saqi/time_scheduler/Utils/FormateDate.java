package com.saqi.time_scheduler.Utils;

import com.saqi.time_scheduler.Models.MyTime;

import java.util.Calendar;

public class FormateDate {

    private static FormateDate _INSTANCE;

    public static FormateDate getInstance() {
        if (_INSTANCE == null) _INSTANCE = new FormateDate();
        return _INSTANCE;
    }

    public String getDayByIndex(int index) {
        switch (index) {
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return "Friday";
            case 6:
                return "Saturday";
            case 7:
                return "Sunday";
            default:
                return "";
        }
    }

    public int getIndexByDay(String day) {
        switch (day) {
            case "Monday":
            case "Mon":
                return 1;
            case "Tuesday":
            case "Tue":
                return 2;
            case "Wednesday":
            case "Wed":
                return 3;
            case "Thursday":
            case "Thu":
                return 4;
            case "Friday":
            case "Fri":
                return 5;
            case "Saturday":
            case "Sat":
                return 6;
            case "Sunday":
            case "Sun":
                return 7;
            default:
                return -1;
        }
    }

    public MyTime formatTime(String time) {
        char[] t = time.toCharArray();
        if (t.length != 4) {
            return null;
        }
        MyTime myTime = new MyTime();
        try {
            String h = t[0] + "" + t[1] + "";
            String m = t[2] + "" + t[3] + "";
            String am = "AM";
            int hh = Integer.parseInt(h);
            if (hh > 24) {
                am = "AM";
                hh = hh - 24;
                if (hh < 10) {
                    h = "0" + hh;
                } else {
                    h = hh + "";
                }
            } else if (hh == 24) {
                am = "AM";
                h = "12";
            } else if (hh > 12) {
                am = "PM";
                hh = hh - 12;
                if (hh < 10) {
                    h = "0" + hh;
                } else {
                    h = hh + "";
                }
            } else if (hh == 12) {
                am = "PM";
            }

            myTime.setAMPM(am);
            myTime.setMint(m);
            myTime.setHour(h);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return myTime;
    }
}
