package com.sx.dw.social.ui;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.apkfuns.logutils.LogUtils;
import com.sx.dw.R;
import com.sx.dw.accountAndSecurity.entity.AccountInfoEntity;
import com.sx.dw.core.BaseActivity;
import com.sx.dw.core.network.DwRetrofit;
import com.sx.dw.core.network.EntityHead;
import com.sx.dw.core.util.ToastUtil;
import com.sx.dw.social.UserInfoApi;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.GlobalData.setAccountInfo;
import static com.sx.dw.core.util.C.IntentExtra.REQUEST_CODE;
import static com.sx.dw.core.util.C.RequestCode.SELECT_IMAGE;

/**
 * 设置用户信息页面
 */
public class SetUserInfoActivity extends BaseActivity {


    private ViewPager sdvSetIcon;
    private UserIconPageAdapter adapter;
    private static int ChangeInfoRequest=0;

    private TextView gender;
    private TextView tag;
    private TextView nick;
    private TextView realname;
    private TextView age;
    private TextView area;
    private TextView company;
    private TextView title;
    private TextView id;
    private TextView star;

    private UserInfoApi userInfoApi;

    private int requestType;

    private Handler showIconHandler=new Handler();
    private Runnable showIconRunnable=new Runnable() {
        @Override
        public void run() {
            sdvSetIcon.setCurrentItem((sdvSetIcon.getCurrentItem()+1)%adapter.getCount());
            showIconHandler.postDelayed(this,3000);
        }
    };

    public static void startMe(BaseActivity activity, int requestCode) {
        Intent intent = new Intent(activity, SetUserInfoActivity.class);
        intent.putExtra(REQUEST_CODE, requestCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestType = getIntent().getIntExtra(REQUEST_CODE, 0);
        sdvSetIcon = (ViewPager) findViewById(R.id.self_icon_pagers);
        List<String> icons=new LinkedList<>();
        if(accountInfo.getIcon()!=null&&!accountInfo.getIcon().equals("")) {
            String[] arr = accountInfo.getIcon().split(";");
            for (String icon : arr) {
                icons.add(icon);
            }
        }
        if(icons.size()<6){
            icons.add(UserIconFragment.PLACE_HOLDER);
        }
        adapter=new UserIconPageAdapter(getSupportFragmentManager(),icons,sdvSetIcon,onClickListener);
        adapter.setTouchListener(onTouchListener);
        sdvSetIcon.setAdapter(adapter);
        sdvSetIcon.addOnPageChangeListener(adapter);


        gender = (TextView) findViewById(R.id.user_info_gender);
        id = (TextView) findViewById(R.id.user_info_id);
        star = (TextView) findViewById(R.id.user_info_star);
        company = (TextView) findViewById(R.id.user_info_company);
        title = (TextView) findViewById(R.id.user_info_title);
        age = (TextView) findViewById(R.id.user_info_age);
        area = (TextView) findViewById(R.id.user_info_area);
        nick = (TextView) findViewById(R.id.user_info_nick);
        realname = (TextView) findViewById(R.id.user_info_realname);
        tag = (TextView) findViewById(R.id.user_info_tag);
        userInfoApi= DwRetrofit.getInstance().createApi(UserInfoApi.class);
        initView();
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.info_table);
        int amount = viewGroup.getChildCount();
        for (int i =0; i < amount; i++) {
            View v = viewGroup.getChildAt(i);
            v.setOnClickListener(infoClickListener);
        }

        showIconHandler.postDelayed(showIconRunnable,3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {


        gender.setText(accountInfo.getSex()==1?"男":"女");
        id.setText(accountInfo.getId());
        area.setText(accountInfo.getArea());
        nick.setText(accountInfo.getName());
        tag.setText(accountInfo.getSign());
        realname.setText(accountInfo.getRealname());
        company.setText(accountInfo.getCompany());
        title.setText(accountInfo.getTitle());
        star.setText(accountInfo.getStar());
        age.setText(accountInfo.getAge());


//        tvSelectSex.setOnClickListener(onClickListener);
//        tvSelectArea.setOnClickListener(onClickListener);
//        btnPush.setOnClickListener(onClickListener);


//            etInputName.setText(accountInfo.getName());
//            etInputSign.setText(accountInfo.getSign());
//            tvSelectSex.setText(accountInfo.getSexString());
//            tvSelectArea.setText(accountInfo.getArea());
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                LogUtils.v(data.getData());
                String[] strings = data.getDataString().split("/");
                File cropImage = new File(getCacheDir(), "SampleCropImage" + strings[strings.length - 1] + ".jpeg");
                Uri destinationUri = Uri.fromFile(cropImage);
                UCrop.Options options = new UCrop.Options();
                options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                options.setMaxBitmapSize(640);
                UCrop.of(data.getData(), destinationUri)
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(640, 640)
                        .withOptions(options)
                        .start(this);
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                final Uri imageUri = UCrop.getOutput(data);
                RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), getFileByUri(imageUri));
                userInfoApi.setUserInfo(accountInfo.getPasswordAES(), accountInfo.getId(), null, body,sdvSetIcon.getCurrentItem()).enqueue(new Callback<EntityHead<AccountInfoEntity>>() {
                    @Override
                    public void onResponse(Call<EntityHead<AccountInfoEntity>> call, Response<EntityHead<AccountInfoEntity>> response) {
                        if (response.body() != null && response.body().isSuccess()&&response.body().getData()!=null
                                &&response.body().getData().getIcon()!=null) {
                            String iconString=response.body().getData().getIcon();
                            String currentIcon=iconString.split(";")[sdvSetIcon.getCurrentItem()];
                            adapter.getCurrentUserIconFragment().setIconURI(currentIcon);
                            accountInfo.setIcon(iconString);
                            setAccountInfo(accountInfo);
                            if (adapter.getCurrentIcon().equals(UserIconFragment.PLACE_HOLDER)) {
                                adapter.addPlaceHolder();
                            }
                            dismissLoading();
                        } else {
                            ToastUtil.showToast(response.body().getMsg());
                            dismissLoading();
                        }
                    }

                    @Override
                    public void onFailure(Call<EntityHead<AccountInfoEntity>> call, Throwable t) {
                        ToastUtil.showToast("修改头像失败");
                        dismissLoading();
                    }
                });

                showLoading("上传中...");
                showIconHandler.postDelayed(showIconRunnable,3000);
            } else if (requestCode == ChangeInfoRequest) {
                if (resultCode == SetUserInfoDetailActivity.ResponseSuccess) {
                    ToastUtil.showToast("修改用户信息成功");
                } else {
                    ToastUtil.showToast("修改用户信息失败");
                }
                showIconHandler.postDelayed(showIconRunnable,3000);
            }

        }
    }
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        intent.setType("image/*");
        // FIXME: 2016/10/25 vivo Y27(Y27) android.content.ActivityNotFoundException: No Activity found to handle Intent { act=android.intent.action.PICK typ=image/* }
        // FIXME: 2016/10/25 酷派 Coolpad 5263(Coolpad 5263) android.content.ActivityNotFoundException: No Activity found to handle Intent { act=android.intent.action.PICK typ=image/* }
        if(intent.resolveActivity(getPackageManager()) == null){
            ToastUtil.showToast("没有可以响应图片选择的应用");
        }else {
            startActivityForResult(intent, SELECT_IMAGE);
        }
    }

    private void selectSex() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] sexes = new String[]{"男", "女", "就不告诉你"};
        builder.setTitle("选择性别")
                .setItems(sexes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, final int i) {
                        JSONObject object=new JSONObject();
                        object.put("sex",i+1);
                        userInfoApi.setUserInfo(accountInfo.getPasswordAES(),accountInfo.getId(),object.toJSONString()).enqueue(new Callback<EntityHead<AccountInfoEntity>>() {
                            @Override
                            public void onResponse(Call<EntityHead<AccountInfoEntity>> call, Response<EntityHead<AccountInfoEntity>> response) {
                                if(response.body()!=null&&response.body().isSuccess()) {
                                    gender.setText(sexes[i]);
                                    accountInfo.setSex(i + 1);
                                    setAccountInfo(accountInfo);
                                    ToastUtil.showToast("修改用户信息成功");
                                }else {
                                    ToastUtil.showToast("修改用户信息失败");
                                }
                            }

                            @Override
                            public void onFailure(Call<EntityHead<AccountInfoEntity>> call, Throwable t) {
                                ToastUtil.showToast("修改用户信息失败");
                            }
                        });

                    }
                }).show();
    }

    private void selectArea() {
    }

//    private void pushInfo() {
//        if (imageUri == null && accountInfo.getIcon() == null) {
//            ToastUtil.showToast("请选择一个头像");
//        } else if (TextUtils.isEmpty(etInputName.getText())) {
//            ToastUtil.showToast("请输入昵称");
//        } else {
//            String dwId = accountInfo.getId();
//            String password = accountInfo.getPasswordAES();
//            SetUserInfoRequestEntity entity = new SetUserInfoRequestEntity();
//            entity.setName(etInputName.getText().toString());
//            entity.setSign(etInputSign.getText().toString());
//            entity.setSex(tvSelectSex.getText().toString());
//            entity.setArea(tvSelectArea.getText().toString());
//            String info = entity.toJsonString();
//            UserInfoApi api = DwRetrofit.getInstance().createApi(UserInfoApi.class);
//            Call<EntityHead<AccountInfoEntity>> call = null;
//            if(imageUri == null) {
//                call = api.setUserInfo(password, dwId, info);
//            }else {
//                RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), getFileByUri(imageUri));
//                call = api.setUserInfo(password, dwId, info, body);
//            }
//            call.enqueue(new DwCallback<EntityHead<AccountInfoEntity>>() {
//                @Override
//                public void getBody(Call call, EntityHead<AccountInfoEntity> body) {
//                    if (body.isSuccess()) {
//                        AccountInfoEntity data = body.getData();
//                        if(!TextUtils.isEmpty(data.getIcon())){
//                            accountInfo.setIcon(data.getIcon());
//                        }
//                        if(!TextUtils.isEmpty(data.getName())){
//                            accountInfo.setName(data.getName());
//                        }
//                        if(!TextUtils.isEmpty(data.getSign())){
//                            accountInfo.setSign(data.getSign());
//                        }
//                        if(data.getSex() != 0){
//                            accountInfo.setSex(data.getSex());
//                        }
//                        setAccountInfo(accountInfo);
//                        ToastUtil.showToast("设置成功");
//                        if(requestType == SETTING_INFO){
//                            LoadingActivity.startMe(SetUserInfoActivity.this);
//                        }else {
//                            setResult(RESULT_OK);
//                        }
//                        finish();
//                    } else {
//                        ToastUtil.showToast(body.getMsg());
//                    }
//                }
//            });
//        }
//    }

    private File getFileByUri(Uri uri) {
        if (null == uri) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        LogUtils.d(data);
        return new File(data);
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showIconHandler.removeCallbacks(showIconRunnable);
            selectImage();
        }
    };


    private View.OnClickListener infoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewGroup viewGroup=(ViewGroup)view;
            View title=viewGroup.getChildAt(0);
            View key=viewGroup.getChildAt(1);
            View value=viewGroup.getChildAt(2);

            String titleStr=((TextView)title).getText().toString();
            String valueStr=((TextView)value).getText().toString();
            String keyStr=((TextView)key).getText().toString();
            if(keyStr.equals("id")){
                ToastUtil.showToast("无法修改ID");
            }else if(keyStr.equals("sex")){
                selectSex();
            }else {
                Intent intent = new Intent(context, SetUserInfoDetailActivity.class);
                intent.putExtra(SetUserInfoDetailActivity.InfoTitleKey, titleStr);
                intent.putExtra(SetUserInfoDetailActivity.InfoValueKey, valueStr);
                intent.putExtra(SetUserInfoDetailActivity.InfoKey, keyStr);
                startActivityForResult(intent, ChangeInfoRequest);
            }
        }

    };

    private View.OnTouchListener onTouchListener=new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction()==MotionEvent.ACTION_MOVE){
                showIconHandler.removeCallbacks(showIconRunnable);
            }else if(event.getAction()==MotionEvent.ACTION_UP||event.getAction()==MotionEvent.ACTION_CANCEL){
                showIconHandler.postDelayed(showIconRunnable,3000);
            }
            return false;
        }
    };


}
