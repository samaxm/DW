/**
 *
 */
package com.sx.dw.im.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.sx.dw.core.GlobalData;
import com.sx.dw.social.entity.SearchUserBean;

import java.io.Serializable;

@Table(name = "LinkMans")
public class LinkMan extends Model implements Serializable {

    @Column(name = "name")
    private String name;
    @Column(name = "dwID")
    private String dwID;
    @Column(name = "wealth")
    private int wealth;
    @Column(name = "icon")
    private String icon;
    @Column(name = "sign")
    private String sign;
    @Column(name = "area")
    private String area;
    @Column(name = "worth")
    private int worth;
    @Column(name = "sex")
    private int sex;
    @Column(name = "type")
    private String type;
//    外键，与ChatEntity互持外键为一对一关系
    @Column(name = "chat")
    private Chat chat;
//    是否是收藏的
    @Column(name = "isLike")
    private boolean isLike;

    @Column(name = "age")
    private String age;

    @Column(name = "company")
    private String company;

    @Column(name = "title")
    private String title;

    @Column(name = "star")
    private String star;

    @Column(name = "rname")
    private String rname;
    @Column(name = "likeDate")
    private long likeDate;
    @Column(name = "tag")
    private String tag;


    public String getPrimaryIcon(){
        if(icon!=null&&!icon.equals("")){
            return icon.split(";")[0];
        }else {
            return null;
        }
    }
    public Chat getChat() {
        if(chat == null && getId()!=null){
            chat = new Select().from(Chat.class).where("id = " + getId()).executeSingle();
        }
        if(chat == null){
            chat = new Chat();
        }
        chat.setLinkMan(this);
        chat.setLinkManId(getDwID());
        return chat;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getLikeDate() {
        return likeDate;
    }

    public void setLikeDate(long likeDate) {
        this.likeDate = likeDate;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDwID() {
        return dwID;
    }

    public void setDwID(String dwID) {
        this.dwID = dwID;
    }

    public int getWealth() {
        return wealth;
    }

    public void setWealth(int wealth) {
        this.wealth = wealth;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
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

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
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

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public String getSexString() {
        if (sex == 1) {
            return "男";
        } else if (sex == 2) {
            return "女";
        }
        return "";
    }
    public String getWorthString() {
//        TODO 身价的显示方式
        return getWorth() + "";
    }

    public String getWealthString() {
        //        TODO 财富的显示方式
        return ""+getWealth();
    }

    public LinkMan update(LinkMan model) {
        setName(model.getName());
        setWealth(model.getWealth());
        setIcon(model.getIcon());
        setSign(model.getSign());
        setArea(model.getArea());
        setWorth(model.getWorth());
        setSex(model.getSex());
        setType(model.getType());
        this.tag=model.tag;
        setLike(model.isLike());
        setAge(model.getAge());
        setCompany(model.getCompany());
        setTitle(model.getTitle());
        setStar(model.getStar());
        this.likeDate=model.likeDate;
        setRname(model.getRname());
        save();
        return this;
    }

    public LinkMan(){};
    public LinkMan(SearchUserBean model) {
        this.likeDate=0;
        this.tag=model.getTag();
        setName(model.getName());
        setWealth(0);
        setIcon(model.getIcon());
        setDwID(model.getId());
        setSign(model.getSign());
        setArea(model.getArea());
        setWorth(Integer.valueOf(model.getWorth()));
        setSex(model.getSex().equals("男")?1:2);
        setType(model.getType());
        setLike(GlobalData.linkManMap.containsKey(model.getId()));
        setAge(model.getAge());
        setCompany(model.getCompany());
        setTitle(model.getTitle());
        setStar(model.getStar());
        setRname(model.getRealname());
    }


    @Override
    public String toString() {
        return "LinkMan{" +
                "id='" + getId() + '\'' +
                "name='" + name + '\'' +
                ", dwID='" + dwID + '\'' +
                ", wealth=" + wealth +
                ", icon='" + icon + '\'' +
                ", sign='" + sign + '\'' +
                ", area='" + area + '\'' +
                ", worth=" + worth +
                ", sex=" + sex +
                ", type='" + type + '\'' +
                ", age='" + age + '\'' +
                ", company='" + company + '\'' +
                ", title='" + title + '\'' +
                ", rname='" + rname + '\'' +
                ", star='" + star + '\'' +

                '}';
    }



}
