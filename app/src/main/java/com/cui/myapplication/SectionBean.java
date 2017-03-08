package com.cui.myapplication;

import java.io.Serializable;

/**
 * Created by Cui on 2016/8/1.
 * 第几节课信息bean
 */
public class SectionBean implements Serializable {
    private String mName;
    private String mBegin;
    private String mEnd;
    private int mTime;
    private int mTheClass;

    public int getTheClass() {
        return mTheClass;
    }

    public void setTheClass(int theClass) {
        this.mTheClass = theClass;
    }

    public int getTime() {
        return mTime;
    }

    public void setTime(int time) {
        this.mTime = time;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getBegin() {
        return mBegin;
    }

    public void setBegin(String begin) {
        this.mBegin = begin;
    }

    public String getEnd() {
        return mEnd;
    }

    public void setEnd(String end) {
        this.mEnd = end;
    }
}
