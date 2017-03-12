package com.sx.dw.videoChat;

import com.sx.dw.im.entity.LinkMan;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/12/8 19:57
 */

public class JPushTipEntity {


    /**
     * body : {"amount":100,"info":{"area":"","dwID":"1845581884","icon":"http://dev.service.dawan.online/group1/M00/00/13/cEodc1gqbVeASc0TAADKuGWfeGA824.jpg","name":"吓跑鱼","sex":2,"sign":"我是算命的啊！","type":"疑","worth":1},"time":1479282927767,"type":"NOTICE_TIP"}
     * mid : 0
     * receiverID : 3251749164
     * senderID : 1845581884
     * time : 1479282927767
     * type : NOTICE_TIP
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
         * info : {"area":"","dwID":"1845581884","icon":"http://dev.service.dawan.online/group1/M00/00/13/cEodc1gqbVeASc0TAADKuGWfeGA824.jpg","name":"吓跑鱼","sex":2,"sign":"我是算命的啊！","type":"疑","worth":1}
         * time : 1479282927767
         * type : NOTICE_TIP
         */

        private int amount;
        private LinkMan info;
        private long time;
        private String type;

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
