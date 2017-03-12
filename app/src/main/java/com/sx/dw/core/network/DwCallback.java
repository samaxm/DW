package com.sx.dw.core.network;

import com.apkfuns.logutils.LogUtils;
import com.google.gson.stream.MalformedJsonException;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.DWApplication;
import com.sx.dw.core.network.exception.NullBodyException;
import com.sx.dw.core.network.exception.UnSuccessfulException;
import com.sx.dw.core.util.NetworkUtils;
import com.sx.dw.core.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.EOFException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2016/10/24 9:00
 */

public abstract class DwCallback<T> implements Callback<T> {
    private BaseActivity activity;

    public DwCallback() {
    }

    public DwCallback(BaseActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        LogUtils.v(response);
        if (response.isSuccessful()) {
            if (response.body() != null) {
                getBody(call,response.body());
            } else {
                onFailure(call, new NullBodyException(response));
            }
        } else {
            onFailure(call, new UnSuccessfulException(response));
        }
    }


    @Override
    public void onFailure(Call<T> call, Throwable t) {

        String toastStr = "";
        if(!NetworkUtils.isNetWorkConnected()){
            toastStr = "网络错误";
        }else if(t instanceof EOFException || t instanceof MalformedJsonException){
            toastStr = "数据解析异常";
        }else if (t instanceof UnknownHostException){
            toastStr = "无法连接到服务器";
        }else if(t instanceof NullBodyException){
            toastStr = "请求数据异常";
        }else if(t instanceof UnSuccessfulException){
            toastStr = "数据请求失败";
        }else {
            toastStr = t.getMessage();
        }
        ToastUtil.showToast(toastStr);
        String tStr = "url = " + call.request().url() + "\n"  + t;
        LogUtils.e(tStr);
        if(activity == null) {
            MobclickAgent.reportError(DWApplication.getInstance(), tStr);
        }else {
            MobclickAgent.reportError(activity,tStr);
            activity.dismissLoading();
        }
    }

    public abstract void getBody(Call call,T body);

}
