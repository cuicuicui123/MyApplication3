package com.cui.myapplication;

/**
 * Created by Cui on 2017/3/9.
 *
 * @Description 保存日程信息
 */

public class ScheduleBean {


    /**
     * ID : 34
     * Date : 2017/3/6 0:00:00
     * IsAllDay : 0
     * BeginTime : 08:00
     * EndTime : 08:30
     * Work : 升旗仪式
     * Address : 学校操场
     * Type : 1
     */

    private int ID;
    private String Date;
    private int IsAllDay;
    private String BeginTime;
    private String EndTime;
    private String Work;
    private String Address;
    private int Type;
    private int mWeek;
    private int mTheClass;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public int getIsAllDay() {
        return IsAllDay;
    }

    public void setIsAllDay(int IsAllDay) {
        this.IsAllDay = IsAllDay;
    }

    public String getBeginTime() {
        return BeginTime;
    }

    public void setBeginTime(String BeginTime) {
        this.BeginTime = BeginTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String EndTime) {
        this.EndTime = EndTime;
    }

    public String getWork() {
        return Work;
    }

    public void setWork(String Work) {
        this.Work = Work;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public int getWeek() {
        return mWeek;
    }

    public void setWeek(int week) {
        mWeek = week;
    }

    public int getTheClass() {
        return mTheClass;
    }

    public void setTheClass(int theClass) {
        mTheClass = theClass;
    }
}
