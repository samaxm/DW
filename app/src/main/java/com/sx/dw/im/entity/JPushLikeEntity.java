package com.sx.dw.im.entity;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/11/15 15:36
 */

public class JPushLikeEntity {


    /**
     * beLikedID : 1851236884
     * icon : http://dev.service.dawan.online/group1/M00/00/13/cEodc1gqbVeASc0TAADKuGWfeGA824.jpg
     * likeID : 1845581884
     * name : 吓跑鱼
     * sex : 男
     * type : NOTICE_LIKE
     */

    private BodyBean body;
    /**
     * body : {"beLikedID":"1851236884","icon":"http://dev.service.dawan.online/group1/M00/00/13/cEodc1gqbVeASc0TAADKuGWfeGA824.jpg","likeID":"1845581884","name":"吓跑鱼","sex":"男","type":"NOTICE_LIKE"}
     * mid : 0
     * receiverID : 1851236884
     * senderID : 1845581884
     * time : 1479195310617
     * type : NOTICE_LIKE
     */

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
        private String beLikedID;
        private String icon;
        private String likeID;
        private String name;
        private String sex;
        private String type;

        public String getBeLikedID() {
            return beLikedID;
        }

        public void setBeLikedID(String beLikedID) {
            this.beLikedID = beLikedID;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getLikeID() {
            return likeID;
        }

        public void setLikeID(String likeID) {
            this.likeID = likeID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
