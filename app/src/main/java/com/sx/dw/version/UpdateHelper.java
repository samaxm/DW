package com.sx.dw.version;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.core.network.DwCallback;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.ToastUtil;

import retrofit2.Call;

/**
 * @ClassName: UpdateHelper
 * @Description: 版本更新工具，封装新版本信息请求、提示对话框、启动下载等，对话框可以调用stopDialog手动关闭
 * @author: fanjie
 * @date: 2016年8月23日 上午10:24:21
 */
public class UpdateHelper {
    private Context context;
    private Dialog dialog;

    public UpdateHelper(Context context) {
        this.context = context;
    }

    public void checkUpdate(final boolean showToast) {
        VersionApi versionApi = DwRetrofit.getInstance().createApi(VersionApi.class);
        versionApi.checkVersion("ANDROID").enqueue(new DwCallback<EntityHead<UpdateInfo>>() {
            @Override
            public void getBody(Call call, EntityHead<UpdateInfo> body) {
                if(body.isSuccess()){
                    if(isNew(body.getData().getVersionNum())) {
                        showDialog(body.getData());
                    }else {
                        if (showToast){
                            ToastUtil.showToast("已经是最新版本");
                        }
                    }
                }else {
                    ToastUtil.showToast(body.getMsg());
                }
            }
        });
    }

    public void stopDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void showDialog(final UpdateInfo info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("有新版本:" + info.getVersionName());
        builder.setMessage(info.getVersionDescription());
        builder.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                LogUtils.d("showDialog,onClick, 确定");
                DownloadListenerService.startMe(context, info);
            }
        });
        builder.setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private boolean isNew(int versionCode) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            if (versionCode > info.versionCode) {
                return true;
            } else {
                return false;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

}
