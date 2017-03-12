package com.sx.dw.im.entity;

import java.io.Serializable;

public class LikeMsg implements Serializable {

	private String icon;
	private String name;
	private String likeID;
	private String beLikedID;
	private String sex;
	private long time ;

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

	public String getLikeID() {
		return likeID;
	}

	public void setLikeID(String likeID) {
		this.likeID = likeID;
	}

	public String getBeLikedID() {
		return beLikedID;
	}

	public void setBeLikedID(String beLikedID) {
		this.beLikedID = beLikedID;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
