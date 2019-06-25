package com.example.smart_parking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Utils.AppManager;
import javaBean.UserInfo;

public class Update_UserCarNum extends AppCompatActivity implements View.OnClickListener {
EditText New_CarNum;
EditText Input_Pwd;
Button   Update_UserCarNum;
Button   Back_Main;
UserInfo userInfo =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update__user_car_num);
        AppManager.addActivity(this);
        New_CarNum=findViewById(R.id.new_carnum);
        Input_Pwd=findViewById(R.id.input_password);
        Update_UserCarNum=findViewById(R.id.btn_update_carnum);
        Back_Main=findViewById(R.id.btn_back_main);
        Update_UserCarNum.setOnClickListener(this);
        Back_Main.setOnClickListener(this);
        userInfo = (UserInfo) getIntent().getSerializableExtra("userinfo");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_update_carnum:
                if (Input_Pwd.getText().toString().equals(userInfo.getAccountPassWord())){
                new Thread(){
                    @Override
                    public void run() {
                        JSONObject new_car_num=new JSONObject();
                        try {
                            new_car_num.put("UserId",userInfo.getUserId());
                            new_car_num.put("UserCarNumber",New_CarNum.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            String CallStr=HttpUtil.post("http://192.168.43.202:8080/Smart_Parking_Service/Update_usercarnumber",new_car_num.toString());
                            if (Integer.parseInt(CallStr)==1){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Update_UserCarNum.this,"修改成功",Toast.LENGTH_SHORT).show();
                                        userInfo.setUserCarNumber(New_CarNum.getText().toString());
                                        Intent intent=new Intent(Update_UserCarNum.this,Baidu_Map.class);
                                        intent.putExtra("userinfo",userInfo);
                                        startActivity(intent);
                                        AppManager.finishCurrentActivity();
                                        AppManager.finishActivity(Baidu_Map.class);
                                    }

                                });
                            }
                            else {
                                Toast.makeText(Update_UserCarNum.this,"服务器异常",Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
                break;
                }
                else {
                    Toast.makeText(Update_UserCarNum.this,"密码错误",Toast.LENGTH_SHORT).show();
                    break;
                }
            case R.id.btn_back_main:
                AppManager.finishCurrentActivity();
        }
    }
}
