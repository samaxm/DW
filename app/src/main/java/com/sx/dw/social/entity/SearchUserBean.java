package com.sx.dw.social.entity;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2017/1/9 11:25
 */
//"name":"龙亦吾","area":null,"sex":"无","sign":null,"id":"3726684155","worth":"null","icon":null,"realname":null,"company":null,"title":null,"star":null,"age":null,"type":null


public class SearchUserBean {
    private String name;
    private String area;
    private String sex;
    private String sign;
    private String id;
    private String worth;
    private String icon;
    private String realname;
    private String company;
    private String title;
    private String star;
    private String age;
    private String type;
    private String tag;

    public String getPrimaryIcon(){
        return icon==null?null:icon.split(";")[0];
    }

    public SearchUserBean(){}
    public SearchUserBean(String name, String area, String sex, String sign, String id, String worth, String icon, String realname, String company, String title, String star, String age, String type,String tag) {
        this.tag=tag;
        this.name = name;
        this.area = area;
        this.sex = sex;
        this.sign = sign;
        this.id = id;
        this.worth = worth;
        this.icon = icon;
        this.realname = realname;
        this.company = company;
        this.title = title;
        this.star = star;
        this.age = age;
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWorth() {
        return worth;
    }

    public void setWorth(String worth) {
        this.worth = worth;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
