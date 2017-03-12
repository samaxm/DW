package com.sx.dw.im.entity;

import java.io.Serializable;
public class WealthAckMsg implements Serializable {
//    DB属性
    private String tempID;
    private long mid;
    private int wealth;

//    逻辑属性
    private boolean chargeSuccess;

    public String getTempID() {
        return tempID;
    }

    public void setTempID(String tempID) {
        this.tempID = tempID;
    }

    public long getMid() {
        return mid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

    public int getWealth() {
        return wealth;
    }

    public void setWealth(int wealth) {
        this.wealth = wealth;
    }

    public boolean isChargeSuccess() {
        return chargeSuccess;
    }

    public void setChargeSuccess(boolean chargeSuccess) {
        this.chargeSuccess = chargeSuccess;
    }
}
