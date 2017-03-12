package com.sx.dw.wealth;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sx.dw.R;
import com.sx.dw.core.BaseActivity;

public class WithdrawExplainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_explain);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    public void okIAmKnow(View view) {
        finish();
    }
}
