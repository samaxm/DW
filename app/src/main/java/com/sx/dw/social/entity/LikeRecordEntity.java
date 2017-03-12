package com.sx.dw.social.entity;

import com.sx.dw.im.entity.LinkMan;

/**
 * @Description: Like记录，暂时相当于联系人作用
 * @author: fanjie
 * @date: 2016/10/6 12:12
 */

public class LikeRecordEntity {

    /**
     * info : {"dwID":"5338055984","sex":2,"name":"5338055984","icon":null,"area":null,"sign":null,"worth":1,"type":"疑"}
     * time : 1475740383000
     */
    private LinkMan info;
    private long time;

    public LinkMan getInfo() {
        return info;
    }

    public void setInfo(LinkMan info) {
        this.info = info;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }



}
