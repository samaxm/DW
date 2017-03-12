package com.sx.dw.social.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.apkfuns.logutils.LogUtils;
import com.sx.dw.R;
import com.sx.dw.accountAndSecurity.entity.AccountInfoEntity;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.ToastUtil;
import com.sx.dw.social.UserInfoApi;

import java.lang.reflect.Field;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.GlobalData.setAccountInfo;

/**
 * 设置用户信息页面
 */
public class SetUserInfoDetailActivity extends BaseActivity implements View.OnClickListener{


    private EditText text;
    private TextView titleView;
    private Button button;
    public static String InfoTitleKey="InfoTitleKey";
    public static String InfoValueKey="InfoValueKey";
    public static String InfoKey="InfoKey";
    public static int ResponseSuccess=1;
    public static int ResponseFail=0;

    private String title;
    private String value;
    private String key;
    private UserInfoApi userInfoApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_info_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent=getIntent();
        title=intent.getStringExtra(InfoTitleKey);
        value=intent.getStringExtra(InfoValueKey);
        key=intent.getStringExtra(InfoKey);

        text= (EditText) findViewById(R.id.editText);
        if(key.equals("age")){
            text.setKeyListener(new NumberKeyListener() {
                @Override
                protected char[] getAcceptedChars() {
                    return new char[] { '0', '1','2', '3', '4', '5', '6', '7', '8','9' };
                }
                @Override
                public int getInputType() {
                    // TODO Auto-generated method stub
                    return android.text.InputType.TYPE_CLASS_NUMBER;
                }
            });
        }
        titleView= (TextView) findViewById(R.id.set_user_info_title);
        button= (Button) findViewById(R.id.save);
        text.setText(value);
        titleView.setText(title);
        userInfoApi= DwRetrofit.getInstance().createApi(UserInfoApi.class);
        button.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        String dwId = accountInfo.getId();
        String password = accountInfo.getPasswordAES();
        HashMap<String,String> info=new HashMap<>(1);
        value=text.getText().toString();
        info.put(key,value);
        userInfoApi.setUserInfo(password, dwId, JSON.toJSONString(info)).enqueue(new Callback<EntityHead<AccountInfoEntity>>() {
            @Override
            public void onResponse(Call<EntityHead<AccountInfoEntity>> call, Response<EntityHead<AccountInfoEntity>> response) {
                if(response.body().isSuccess()){
                    Field[] fields=AccountInfoEntity.class.getDeclaredFields();
                    for(int i=0;i<fields.length;i++){
                        Field f=fields[i];
                        String fn=f.getName();
                        if(fn.equals(key)){
                            try {
                                f.setAccessible(true);
                                f.set(accountInfo,value);
                                setAccountInfo(accountInfo);
                                setResult(ResponseSuccess);
                                break;
                            } catch (IllegalAccessException e) {
                            }
                        }
                    }
                    finish();
                }else{
                    ToastUtil.showToast(response.body().getMsg());
                    setResult(ResponseFail);
                }
            }

            @Override
            public void onFailure(Call<EntityHead<AccountInfoEntity>> call, Throwable t) {
                LogUtils.e("",t);
                ToastUtil.showToast("设置信息失败！");
            }
        });


    }
}
