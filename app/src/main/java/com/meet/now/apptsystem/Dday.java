package com.meet.now.apptsystem;

public class Dday {

    String apptName;
    String apptDate;
    int dDay;

    public Dday(String apptName, String apptDate, int dDay) {
        this.apptName = apptName;
        this.apptDate = apptDate;
        this.dDay = dDay;
    }

    public String getApptName() {
        return apptName;
    }

    public void setApptName(String apptName) {
        this.apptName = apptName;
    }

    public String getApptDate() {
        return apptDate;
    }

    public void setApptDate(String apptDate) {
        this.apptDate = apptDate;
    }

    public String getdDay() {
        return dDay+"";
    }

    public void setdDay(int dDay) {
        this.dDay = dDay;
    }
}