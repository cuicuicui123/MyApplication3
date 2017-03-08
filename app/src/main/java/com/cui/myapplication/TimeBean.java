package com.cui.myapplication;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Cui on 2016/8/1.
 * 时间段上午下午bean，早读或者晚自习不会有下属的SectionBean，上午下午会有下属的第几节课信息SectionBean
 */
public class TimeBean implements Serializable {
    private String mName;
    private List<SectionBean> mList;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public List<SectionBean> getList() {
        return mList;
    }

    public void setList(List<SectionBean> list) {
        this.mList = list;
    }
}
