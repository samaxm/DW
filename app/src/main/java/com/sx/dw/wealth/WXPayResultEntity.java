package com.sx.dw.wealth;

import com.google.gson.annotations.SerializedName;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/10/10 15:01
 */

public class WXPayResultEntity {


    /**
     * package : Sign=WXPay
     * appid : wx8a6304437033f400
     * sign : 5984E3508BCFECC87569C0E61E9AFC15
     * partnerid : 1282167201
     * prepayid : wx201610101453271f6f1cd5760952727789
     * noncestr : a4c297a4d5224b9a8e02835f0df56b70
     * timestamp : 1476082408
     */

//    序列化名不能作为属性名，所以通过注解标记
    @SerializedName("package")
    private String packageX;
    private String appid;
    private String sign;
    private String partnerid;
    private String prepayid;
    private String noncestr;
    private String timestamp;

    public String getPackageX() {
        return packageX;
    }

    public void setPackageX(String packageX) {
        this.packageX = packageX;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "WXPayResultEntity{" +
                "packageX='" + packageX + '\'' +
                ", appid='" + appid + '\'' +
                ", sign='" + sign + '\'' +
                ", partnerid='" + partnerid + '\'' +
                ", prepayid='" + prepayid + '\'' +
                ", noncestr='" + noncestr + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
