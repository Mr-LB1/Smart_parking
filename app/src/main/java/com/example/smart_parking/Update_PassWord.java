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

public class Update_PassWord extends AppCompatActivity implements View.OnClickListener {
    private EditText Old_Pwd;
    private EditText New_Pwd;
    private EditText Check_Pwd;
    private Button   Update_Pwd;
    private Button   Back_Main;
    private UserInfo userInfo =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update__user_password);
        AppManager.addActivity(this);
        Old_Pwd=findViewById(R.id.old_pwd);
        New_Pwd=findViewById(R.id.new_pwd);
        Check_Pwd=findViewById(R.id.check_pwd);
        Update_Pwd=findViewById(R.id.btn_update_pwd);
        Back_Main=findViewById(R.id.btn_back_main);
        Update_Pwd.setOnClickListener(this);
        Back_Main.setOnClickListener(this);
        userInfo = (UserInfo) getIntent().getSerializableExtra("userinfo");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_update_pwd:
                if (Old_Pwd.getText().toString().equals(userInfo.getAccountPassWord())){
                if ((!(New_Pwd.getText().toString().equals("")))&&(New_Pwd.getText().toString().equals(Check_Pwd.getText().toString()))) {
                    new Thread() {
                        @Override
                        public void run() {
                            JSONObject updatePwd = new JSONObject();
                            try {
                                updatePwd.put("UserId",userInfo.getUserId());
                                updatePwd.put("AccountPassWord", New_Pwd.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                String CallStr = HttpUtil.post("http://192.168.43.202:8080/Smart_Parking_Service/Update_password", updatePwd.toString());
                                if (Integer.parseInt(CallStr) == 1) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Update_PassWord.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                                            userInfo.setAccountPassWord(New_Pwd.getText().toString());
                                            Intent intent = new Intent(Update_PassWord.this, Baidu_Map.class);
                                            intent.putExtra("userinfo", userInfo);
                                            startActivity(intent);
                                            AppManager.finishCurrentActivity();
                                            AppManager.finishActivity(Baidu_Map.class);
                                        }

                                    });
                                } else {
                                    Toast.makeText(Update_PassWord.this, "服务器异常", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }.start();
                }
                else {
                    Toast.makeText(Update_PassWord.this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();}
                }
                else {
                    Toast.makeText(Update_PassWord.this, "原密码不正确", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_back_main:
                AppManager.finishCurrentActivity();
        }
    }

}
