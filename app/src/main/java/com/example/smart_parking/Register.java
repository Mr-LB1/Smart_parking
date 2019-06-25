package com.example.smart_parking;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Utils.AppManager;

public class Register extends AppCompatActivity {
private EditText UserName;
private EditText AccountPassWord;
private EditText CheckPwd;
private EditText UserCarNumber;
private EditText PhoneNumber;
private EditText Pin;
private Button Get_Pin;
private Button next;
private Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AppManager.addActivity(this);
        UserName=findViewById(R.id.UserName);
        AccountPassWord=findViewById(R.id.PassWord);
        CheckPwd=findViewById(R.id.CheckPwd);
        UserCarNumber=findViewById(R.id.UserCarNumber);
        PhoneNumber=findViewById(R.id.PhoneNumber);
        Pin=findViewById(R.id.Pin);
        Get_Pin=findViewById(R.id.Get_Pin);
        next=findViewById(R.id.next);
        back=findViewById(R.id.back);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        JSONObject Userinfo=new JSONObject();
                        if (!HttpUtil.isNetAvailable(Register.this))
                        {
                            Looper.prepare();
                            Toast.makeText(Register.this, "网络不可用", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        try {
                            Userinfo.put("UserName",UserName.getText().toString());
                            Userinfo.put("AccountPassWord",AccountPassWord.getText().toString());
                            Userinfo.put("UserCarNumber",UserCarNumber.getText().toString());
                            Userinfo.put("PhoneNumber",PhoneNumber.getText().toString());
                            if (!AccountPassWord.getText().toString().equals(CheckPwd.getText().toString()))
                            {
                                Looper.prepare();
                                Toast.makeText(Register.this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                AccountPassWord.setText("");
                                CheckPwd.setText("");
                            }
                            else {
                                //短信验证需要付费接口,暂时不做相应功能
                                String CallStr=HttpUtil.post("http://192.168.43.202:8080/Smart_Parking_Service/Register",Userinfo.toString());
                                //注册信息判断
                                //注册成功
                                if (CallStr.equals("1")){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent=new Intent(Register.this,Login.class);
                                            startActivity(intent);
                                            AppManager.finishCurrentActivity();
                                        }
                                    });
                                }
                                //用户名已注册
                                else if (CallStr.equals("2")){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Register.this,"用户名已注册",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Register.this,Login.class);
                startActivity(intent);
                AppManager.finishCurrentActivity();
                AppManager.finishActivity(Login.class);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
            AppManager.finishCurrentActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
