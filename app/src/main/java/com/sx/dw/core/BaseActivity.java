
package com.sx.dw.core;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.R;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: BaseActivity.java
 * @Description: FragmentActivity基础类
 * @author: cj
 * @date: 2016年1月8日 上午9:36:41
 */
public class BaseActivity extends AppCompatActivity {
    public Activity context;
    private AlertDialog dialog;

    /**
     * Activity创建
     */
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        DWApplication.addActivity(this);
        context = this;
    }


    public void showLoading(){
        showLoading(null);
    }
    public void showLoading(String info) {
        if(dialog == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(R.layout.dialog_weiting_progress);
            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface d) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
        }
        LogUtils.v(" info = " + info);
        if(!TextUtils.isEmpty(info)) {
            dialog.setMessage(info);
        }
        dialog.show();
    }

    public void dismissLoading() {
        LogUtils.v("dismissLoading " );
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DWApplication.delActivity(this);
    }

}
