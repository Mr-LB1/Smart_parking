package com.example.smart_parking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import Utils.AppManager;
import javaBean.UserInfo;

public class Show_userinfo extends AppCompatActivity implements View.OnClickListener {
private UserInfo userInfo =null;
private TextView Show_UserName;
private TextView Show_UserCarNum;
private TextView Show_UserPhoneNum;
private TextView Show_RealName;
private TextView Show_SafeCardNum;
Button btn_backmain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_userinfo);
        AppManager.addActivity(this);
        userInfo = (UserInfo) getIntent().getSerializableExtra("userinfo");
        Show_UserName=findViewById(R.id.show_username);
        Show_UserCarNum=findViewById(R.id.show_car_num);
        Show_UserPhoneNum=findViewById(R.id.show_phone_num);
        Show_RealName=findViewById(R.id.show_real_name);
        Show_SafeCardNum=findViewById(R.id.show_safecard_num);
        btn_backmain=findViewById(R.id.btn_back_main);
        btn_backmain.setOnClickListener(this);
        Show_UserName.setText(userInfo.getUserName());
        Show_UserCarNum.setText(userInfo.getUserCarNumber());
        Show_UserPhoneNum.setText(userInfo.getPhoneNumber());
        Show_RealName.setText(userInfo.getUserRealName());
        Show_SafeCardNum.setText(userInfo.getSafeCardNumber());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back_main:
                AppManager.finishCurrentActivity();
        }
    }
}
