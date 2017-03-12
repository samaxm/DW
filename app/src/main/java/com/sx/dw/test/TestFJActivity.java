/**
 *
 */
package com.sx.dw.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.sx.dw.R;
import com.sx.dw.accountAndSecurity.entity.AccountInfoEntity;
import com.sx.dw.accountAndSecurity.entity.PhoneRegisterRequestEntity;
import com.sx.dw.accountAndSecurity.helper.AccountApi;
import com.sx.dw.accountAndSecurity.helper.PhoneAboutApi;
import com.sx.dw.accountAndSecurity.ui.LoginActivity;
import com.sx.dw.accountAndSecurity.ui.RegisterActivity;
import com.sx.dw.accountAndSecurity.ui.SetPasswordActivity;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.ToastUtil;
import com.sx.dw.im.entity.ChatMsg;
import com.sx.dw.im.helper.MsgApi;
import com.sx.dw.im.message.MsgSyncService;
import com.sx.dw.im.message.ProtoMsgUtils;
import com.sx.dw.social.UserInfoApi;
import com.sx.dw.social.ui.SetUserInfoActivity;
import com.sx.dw.social.ui.UserCenterActivity;
import com.sx.dw.videoChat.VideoChatActivity;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.protobuf.ProtoConverterFactory;

import static com.sx.dw.core.util.C.RequestCode.RESET_INFO;

/**
 * @ClassName: TestFJActivity
 * @Description:
 * @author: fanjie
 * @date: 2016年8月20日 下午4:41:23
 */
public class TestFJActivity extends BaseActivity {

    private String[] likeMsgs = {"交个朋友吧","我想泡你","搞个球球","拿tm的象拔蚌去走位"};


    public static void startMe(Context context) {
        Intent i = new Intent(context, TestFJActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_test_fj);

    }

    public void testEveryThing(View view) {
        ToastUtil.showToast(getRundomText());
    }

    private String getRundomText(){
        Random random=new Random();
        return likeMsgs[random.nextInt(likeMsgs.length)];
    }

    public void testLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }


    public void testRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void registerByPhone(View view) {
        AccountApi accountApi = DwRetrofit.getInstance().createApi(AccountApi.class);
        PhoneRegisterRequestEntity entity = new PhoneRegisterRequestEntity();
        entity.setPhoneNum("15013445816");
        entity.setPassword("15013445816");
        entity.setCode("2222");
        accountApi.registerV2(entity.toJsonString(), "PHONECODE").enqueue(new Callback<EntityHead<AccountInfoEntity>>() {
            @Override
            public void onResponse(Call<EntityHead<AccountInfoEntity>> call, Response<EntityHead<AccountInfoEntity>> response) {
                LogUtils.d(response);
                LogUtils.d(response.body().getData());
            }

            @Override
            public void onFailure(Call<EntityHead<AccountInfoEntity>> call, Throwable t) {
                LogUtils.d(t);
            }
        });
    }

    public void testPhoneCoe(View view) {
        String str = "15013445816";
        Call call = DwRetrofit.getInstance().createApi(PhoneAboutApi.class).sendForgetPasswordPhoneCode(str);
        LogUtils.d(call.request());
        call.enqueue(new Callback<EntityHead>() {
            @Override
            public void onResponse(Call<EntityHead> call, Response<EntityHead> response) {
                LogUtils.d(response);
                if (response.body().isSuccess()) {
                    LogUtils.d(response.body());
                } else {
                    ToastUtil.showToast(response.body().getMsg());
                }
            }

            @Override
            public void onFailure(Call<EntityHead> call, Throwable t) {
                LogUtils.d(t.getMessage());
                ToastUtil.showToast(t.getMessage());
            }
        });
    }

    public void testSetPassword(View view) {
        startActivity(new Intent(this, SetPasswordActivity.class));
    }

    public void videoChatV2(View view) {
        startActivity(new Intent(this, VideoChatActivity.class));
    }

    public void userInfo(View view) {
        SetUserInfoActivity.startMe(this, RESET_INFO);
    }

    public void testProtobuf(View view) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.198:8080/message/")
                .build();
        MsgApi api = retrofit.create(MsgApi.class);
        ChatMsg entity = new ChatMsg();
        entity.setFromID("356494");
        entity.setToId("356494");
        entity.setText("玩个球球");
//        entity.setMessageType(MessageProtos.ChatMessageType.TEXT);
        final ProtoMsgUtils utils = new ProtoMsgUtils();
////        api.send(utils.toBody(entity)).enqueue(new Callback<ResponseBody>() {
////            @Override
////            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
////                LogUtils.d(response);
//////                try {
////////
////////                    switch (magicBox.getType()){
////////                        case CHAT:
////////                            LogUtils.d(magicBox.getChatMsg());
////////                            break;
////////                        case LIST:
////////                            LogUtils.d(magicBox.getChatMsgs());
////////                            break;
//////                    }
//////                }
////            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });
    }

    public void startMsgService(View view) {
        startService(new Intent(this, MsgSyncService.class));
    }

    public void getUserInfo(View view) {
        DwRetrofit.getInstance().createApi(UserInfoApi.class).testGetUserInfo("9344031574").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    LogUtils.d(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtils.d(t.getMessage());
            }
        });
    }


    public void toUserCenterTest(View view) {
        startActivity(new Intent(this, UserCenterActivity.class));
    }

    public void testprotobuf(View view) {
        String s = new String(Character.toChars(0x1F602));
        LogUtils.d(s);
        ((TextView) view).setText(s);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.198:8080/message/")
                .addConverterFactory(ProtoConverterFactory.create())
                .build();
        ChatMsg entity = new ChatMsg();
        entity.setFromID("356494");
        entity.setToId("356494");
        entity.setText("玩个球球");
//        entity.setMessageType(MessageProtos.ChatMessageType.TEXT);
        final ProtoMsgUtils utils = new ProtoMsgUtils();
        MsgApi api = retrofit.create(MsgApi.class);
//        api.testSend(utils.toBody(entity)).enqueue(new Callback<MessageProtos.Message>() {
//            @Override
//            public void onResponse(Call<MessageProtos.Message> call, Response<MessageProtos.Message> response) {
//                LogUtils.d(response.body());
//            }
//
//            @Override
//            public void onFailure(Call<MessageProtos.Message> call, Throwable t) {
//                LogUtils.d(t);
//            }
//        });
    }

    public void toChatActivity(View view) {

    }

    public void testAliPay(View view) {
//
    }

    public void testWePay(View view) {


    }

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    @SuppressWarnings("unchecked")
                    Map<String, String> result = (Map<String, String>) msg.obj;
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    LogUtils.d(result);
                    break;
                }
                case 1: {

                    break;
                }
                default:
                    break;
            }
        }
    };


}
