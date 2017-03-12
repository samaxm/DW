package com.sx.dw.im.entity;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/12/8 18:42
 */

public class JPushVipPayEntity {


    /**
     * body : {"dwID":"1234567890","time":1480144347755,"type":"NOTICE_VIP_REGISTER"}
     * mid : 0
     * receiverID : 1234567890
     * senderID : SYSTEM
     * time : 1480144347756
     * type : NOTICE_VIP_REGISTER
     */

    private BodyBean body;
    private int mid;
    private String receiverID;
    private String senderID;
    private long time;
    private String type;

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class BodyBean {
        /**
         * dwID : 1234567890
         * time : 1480144347755
         * type : NOTICE_VIP_REGISTER
         */

        private String dwID;
        private long time;
        private String type;

        public String getDwID() {
            return dwID;
        }

        public void setDwID(String dwID) {
            this.dwID = dwID;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
