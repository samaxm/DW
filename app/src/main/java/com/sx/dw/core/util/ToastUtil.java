/**
 *
 */
package com.sx.dw.core.util;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.sx.dw.core.AppConfig;
import com.sx.dw.core.DWApplication;
public class ToastUtil {

    /**
     * 显示提示信息
     *
     * @param text
     */
    public static void showToast(final String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
//        保证在主线程调用
        Handler handler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(android.os.Message msg) {
                Toast.makeText(DWApplication.getInstance(),text,Toast.LENGTH_SHORT).show();
            }
        };
        handler.sendEmptyMessage(1);
    }

    public static void debugToast(String text){
        if(AppConfig.IS_DEBUG){
            showToast(text);
        }
    }

}
