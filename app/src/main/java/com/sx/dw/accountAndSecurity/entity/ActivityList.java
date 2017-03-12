package com.sx.dw.accountAndSecurity.entity;

import java.util.List;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2017/1/5 9:53
 */

public class ActivityList {

    private long dateNum;
    private List<ActivityDetail> list;

    public long getDateNum() {
        return dateNum;
    }

    public void setDateNum(long dateNum) {
        this.dateNum = dateNum;
    }

    public List<ActivityDetail> getList() {
        return list;
    }

    public void setList(List<ActivityDetail> list) {
        this.list = list;
    }
}
