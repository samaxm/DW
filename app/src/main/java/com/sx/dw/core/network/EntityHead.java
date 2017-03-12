package com.sx.dw.core.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @ClassName: ${CLASS_NAME}
 * @Description: Api返回数据的头，传入的泛型为data中包含的类型
 * @author: fanjie
 * @date: 2016/9/8
 */
public class EntityHead<T> {
    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("data")
    @Expose
    private T data;

    public boolean isSuccess(){
        return statusCode == 10000;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     *
     * @return 返回指定泛型的data
     */
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "EntityHead{" +
                "statusCode=" + statusCode +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
