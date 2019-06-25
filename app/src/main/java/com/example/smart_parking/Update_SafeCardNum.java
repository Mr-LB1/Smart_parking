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
import Utils.Realnameauthentication;
import javaBean.UserInfo;

public class Update_SafeCardNum extends AppCompatActivity implements View.OnClickListener {
  private       EditText               UserRealName;
  private       EditText               UserSafeCardNumber;
  private       Button                 Update_UserSafeCardNumber;
  private       Button                 Back_Main;
  private       UserInfo               userInfo                 =null;
  private final Realnameauthentication isrealnameauthentication = (Realnameauthentication) getApplication();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_safecardnum);
        AppManager.addActivity(this);
        UserRealName=findViewById(R.id.user_realname);
        UserSafeCardNumber=findViewById(R.id.user_safecardnum);
        Update_UserSafeCardNumber=findViewById(R.id.btn_update_safecardnum);
        Back_Main=findViewById(R.id.btn_back_main);
        Update_UserSafeCardNumber.setOnClickListener(this);
        Back_Main.setOnClickListener(this);
        userInfo = (UserInfo) getIntent().getSerializableExtra("userinfo");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_update_safecardnum:

                    new Thread(){
                        @Override
                        public void run() {
                            JSONObject updateinfo=new JSONObject();
                            try {
                                updateinfo.put("UserId",userInfo.getUserId());
                                updateinfo.put("UserRealName",UserRealName.getText().toString());
                                updateinfo.put("SafeCardNumber",UserSafeCardNumber.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                String CallStr=HttpUtil.post("http://192.168.43.202:8080/Smart_Parking_Service/Update_safecardinfo",updateinfo.toString());
                                if (Integer.parseInt(CallStr)==1){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Update_SafeCardNum.this,"实名认证成功",Toast.LENGTH_SHORT).show();
                                            userInfo.setUserRealName(UserRealName.getText().toString());
                                            userInfo.setSafeCardNumber(UserSafeCardNumber.getText().toString());
                                            Intent intent=new Intent(Update_SafeCardNum.this,Baidu_Map.class);
                                            intent.putExtra("userinfo",userInfo);
                                            startActivity(intent);
                                            AppManager.finishCurrentActivity();
                                            AppManager.finishActivity(Baidu_Map.class);
                                        }
                                    });
                                }
                                else {
                                    Toast.makeText(Update_SafeCardNum.this,"服务器异常",Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    break;
            case R.id.btn_back_main:
                AppManager.finishCurrentActivity();
        }
    }
}
