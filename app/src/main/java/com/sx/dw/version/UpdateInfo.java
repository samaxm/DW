package com.sx.dw.version;

import java.io.Serializable;

/**
 * @ClassName: ${CLASS_NAME}
 * @Description: 检查更新的json实体类
 * @author: fanjie
 * @date: 2016/9/5
 */
public class UpdateInfo implements Serializable {

    private int versionId;
    private String appType;
    private String versionDescription;
    private String downloadurl;
    private String versionName;
    private int versionNum;

    public UpdateInfo() {
    }

    public UpdateInfo(int versionId, String appType, String versionDescription, String downloadurl, String versionName, int versionNum) {
        this.versionId = versionId;
        this.appType = appType;
        this.versionDescription = versionDescription;
        this.downloadurl = downloadurl;
        this.versionName = versionName;
        this.versionNum = versionNum;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    @Override
    public String toString() {
        return "UpdateInfo{" +
                "versionId=" + versionId +
                ", appType='" + appType + '\'' +
                ", versionDescription='" + versionDescription + '\'' +
                ", downloadurl='" + downloadurl + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionNum=" + versionNum +
                '}';
    }
}
