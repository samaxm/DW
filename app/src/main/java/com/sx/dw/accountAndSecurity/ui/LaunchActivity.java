package com.sx.dw.accountAndSecurity.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.sx.dw.R;
import com.sx.dw.accountAndSecurity.entity.ActivityDetail;
import com.sx.dw.accountAndSecurity.entity.ActivityList;
import com.sx.dw.accountAndSecurity.helper.AppApi;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.util.DateHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sx.dw.core.GlobalData.getLatestActivity;
import static com.sx.dw.core.GlobalData.updateActivityList;

/**
 * @Description: TODO 添加类的描述
 * @author: fanjie
 * @date: 2017/1/4 17:59
 */

public class LaunchActivity extends BaseActivity {

    private AppApi appApi;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_launch_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        ActivityDetail detail=getLatestActivity();
        if(detail!=null){
            LoginActivity.startMe(context);
            finish();
        }else {
            long currentDateNum = DateHelper.getCurrentDateNum();
            appApi = DwRetrofit.getInstance().createApi(AppApi.class);
            appApi.getTodayActivity(currentDateNum).enqueue(new Callback<EntityHead<ActivityList>>() {
                @Override
                public void onResponse(Call<EntityHead<ActivityList>> call, Response<EntityHead<ActivityList>> response) {
                    if (response.body() != null) {
                        ActivityList list = response.body().getData();
                        updateActivityList(list);
                    }
                    LoginActivity.startMe(context);
                    finish();
                }

                @Override
                public void onFailure(Call<EntityHead<ActivityList>> call, Throwable t) {
                    LoginActivity.startMe(context);
                    finish();
                }
            });
        }
    }
}
