package com.sx.dw.accountAndSecurity.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sx.dw.R;
import com.sx.dw.core.BaseActivity;

import static com.sx.dw.core.GlobalData.accountInfo;
import static com.sx.dw.core.util.C.RequestCode.BIND_PHONE;
import static com.sx.dw.core.util.C.RequestCode.RESET_PASSWORD;

/**
 * 账户与安全页面，包含绑定手机等功能选项
 */
public class AccountAndSecurityActivity extends BaseActivity {

    private LinearLayout llBindPhone;
    private LinearLayout llResetPassword;
    private TextView tvPhoneNumber;
    private ImageView ivActionBindPhone;
    private TextView tvUserId;
    private TextView tvLoginType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_and_security);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvUserId = (TextView) findViewById(R.id.tv_user_id);
        tvLoginType = (TextView) findViewById(R.id.tv_login_type);
        llBindPhone = (LinearLayout) findViewById(R.id.ll_bind_phoneNumber);
        tvPhoneNumber = (TextView) findViewById(R.id.tv_phone_number);
        ivActionBindPhone = (ImageView) findViewById(R.id.iv_action_bind_phone);
        llResetPassword = (LinearLayout) findViewById(R.id.ll_reset_password);
        initViewValue();
    }

    private void initViewValue() {
        tvUserId.setText(accountInfo.getId());
        if (TextUtils.isEmpty(accountInfo.getPhone())) {
            llBindPhone.setOnClickListener(onClickListener);
        } else {
            ivActionBindPhone.setVisibility(View.INVISIBLE);
            llResetPassword.setVisibility(View.VISIBLE);
            llResetPassword.setOnClickListener(onClickListener);
            tvPhoneNumber.setText(accountInfo.getPhone());
        }
        switch (accountInfo.getLoginType()) {
            case ACCOUNT: {
                tvLoginType.setText("账号登录");
                break;
            }
            case WECHAT: {
                tvLoginType.setText("微信登录");
                break;
            }
            case QQ: {
                tvLoginType.setText("QQ登录");
                break;
            }
        }

    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_bind_phoneNumber:
                    CheckPhoneActivity.startMe(AccountAndSecurityActivity.this, BIND_PHONE);
                    break;
                case R.id.ll_reset_password:
                    CheckPhoneActivity.startMe(AccountAndSecurityActivity.this, RESET_PASSWORD);
                    break;
            }
        }
    };


}
