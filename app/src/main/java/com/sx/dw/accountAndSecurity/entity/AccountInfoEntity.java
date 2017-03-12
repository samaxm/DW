package com.sx.dw.accountAndSecurity.entity;

import com.alibaba.fastjson.JSON;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sx.dw.core.util.AES;
import com.sx.dw.im.entity.LinkMan;

import java.io.Serializable;

import static com.sx.dw.accountAndSecurity.entity.AccountInfoEntity.LoginType.NOTHING;

/**
 * @Description: 账户信息模型
 * @author: fanjie
 * @date: 2016/9/14
 */
public class AccountInfoEntity implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("unionid")
    @Expose
    private String unionid;
    @SerializedName("openid")
    @Expose
    private String openid;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("worth")
    @Expose
    private int worth;
    @SerializedName("wealth")
    @Expose
    private int wealth;
    @SerializedName("sign")
    @Expose
    private String sign;
    @SerializedName("sex")
    @Expose
    private int sex;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("version")
    @Expose
    private int version;


    @SerializedName("tag")
    @Expose
    private String tag;


    @SerializedName("type")
    @Expose
    private String type;
    /**
     * 是否设置过密码，设置过的话需要输入密码登陆
     */
    @SerializedName("init")
    @Expose
    private boolean init;
    @SerializedName("loginType")
    @Expose
    private LoginType loginType;
    @SerializedName("account")
    @Expose
    private String account;
    @SerializedName("canwithdraw")
    @Expose
    private int canWithdraw;

    @SerializedName("company")
    @Expose
    private String company;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("star")
    @Expose
    private String star;

    @SerializedName("age")
    @Expose
    private String age;

    @SerializedName("realname")
    @Expose
    private String realname;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordAES() {
        if (getPassword() == null) {
            return null;
        } else {
            return AES.encode(getPassword());
        }
    }

    public String getPrimaryIcon(){
        if(icon!=null&&!icon.equals("")){
            return icon.split(";")[0];
        }else {
            return null;
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getWorth() {
        return worth;
    }

    public void setWorth(int worth) {
        this.worth = worth;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getSex() {
        return sex;
    }

    public String getSexString() {
        if (sex == 1) {
            return "男";
        } else if (sex == 2) {
            return "女";
        }
        return "";
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWealth() {
        return wealth;
    }

    public void setWealth(int wealth) {
        this.wealth = wealth;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getCanWithdraw() {
        return canWithdraw;
    }

    public boolean isUserCanWithdraw() {
        return canWithdraw == 1;
    }
    public void setCanWithdraw(int canWithdraw) {
        this.canWithdraw = canWithdraw;
    }

    public void setCanWithdraw(boolean canWithdraw) {
        this.canWithdraw = canWithdraw ? 1 : -99;
    }


    public String getWorthString() {
        return getWorth() + "";
    }

    public String getWealthString() {
        return "" + getWealth();
    }

    public LoginType getLoginType() {
        if (loginType == null) {
            return NOTHING;
        }
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public String toJsonString() {

        return JSON.toJSONString(this);
//        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue);
    }

    public LinkMan toLinkMan() {
        LinkMan linkMan = new LinkMan();
        linkMan.setDwID(getId());
        linkMan.setIcon(getIcon());
        linkMan.setName(getName());
        linkMan.setWorth(getWorth());
        linkMan.setWealth(getWealth());
        linkMan.setSex(getSex());
        linkMan.setArea(getArea());
        linkMan.setSign(getSign());
        linkMan.setType(getType());
        linkMan.setTag(this.tag);
        return linkMan;
    }

    @Override
    public String toString() {
        return "AccountInfoEntity{" +
                "id='" + id + '\'' +
                ", unionid='" + unionid + '\'' +
                ", openid='" + openid + '\'' +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", area='" + area + '\'' +
                ", worth=" + worth +
                ", wealth=" + wealth +
                ", sign='" + sign + '\'' +
                ", sex=" + sex +
                ", phone='" + phone + '\'' +
                ", version=" + version +
                ", tag='" + tag + '\'' +
                ", type='" + type + '\'' +
                ", init=" + init +
                ", loginType=" + loginType +
                ", account='" + account + '\'' +
                ", canWithdraw=" + canWithdraw +
                ", company='" + company + '\'' +
                ", title='" + title + '\'' +
                ", star='" + star + '\'' +
                ", age='" + age + '\'' +
                ", realname='" + realname + '\'' +
                '}';
    }

    public enum LoginType {
        WECHAT, QQ, NOTHING, ACCOUNT
    }

}
