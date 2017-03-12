package com.sx.dw.im.entity;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/12/8 18:39
 */

public class JPushRechargeEntity {

    /**
     * body : {"amount":100,"dwID":"9180813414","time":1481193147831,"type":"NOTICE_RECHARGE"}
     * mid : 0
     * receiverID : 9180813414
     * senderID :
     * time : 1481193147831
     * type : NOTICE_RECHARGE
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
         * amount : 100
         * dwID : 9180813414
         * time : 1481193147831
         * type : NOTICE_RECHARGE
         */

        private int amount;
        private String dwID;
        private long time;
        private String type;

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

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
