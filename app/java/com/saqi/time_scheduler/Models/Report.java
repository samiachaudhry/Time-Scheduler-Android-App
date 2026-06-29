package com.saqi.time_scheduler.Models;

public class Report {
    String name;
    int weeklyClasses;
    int remainingClasses;

    public Report() {
    }

    public Report(String name, int weeklyClasses, int remainingClasses) {
        this.name = name;
        this.weeklyClasses = weeklyClasses;
        this.remainingClasses =remainingClasses;
    }

    public int getRemainingClasses() {
        return remainingClasses;
    }

    public void setRemainingClasses(int remainingClasses) {
        this.remainingClasses = remainingClasses;
    }

    public void increaseWeeklyClass() {
        this.weeklyClasses++;
    }

    public void increaseRemainingClass() {
        this.remainingClasses++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeeklyClasses() {
        return weeklyClasses;
    }

    public void setWeeklyClasses(int weeklyClasses) {
        this.weeklyClasses = weeklyClasses;
    }
}
