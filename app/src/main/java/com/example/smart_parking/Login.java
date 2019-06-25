package com.example.smart_parking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Utils.AppManager;
import javaBean.UserInfo;

public class Login extends AppCompatActivity {
private ImageView eyes_password;
private EditText username;
private EditText password;
private Button login;
private Button register;
private CheckBox checkBox;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppManager.addActivity(this);
        eyes_password=findViewById(R.id.eyes_password);
        password=findViewById(R.id.password);
        username=findViewById(R.id.username);
        login=findViewById(R.id.login);
        register=findViewById(R.id.register);
        checkBox = findViewById(R.id.savepwd);
        readFromPre(this,username,password);
        if (!TextUtils.isEmpty(username.getText().toString())){
            checkBox.setChecked(true);
            username.setCursorVisible(false);
            password.setCursorVisible(false);
            password.setSelection(password.getText().length());
        }
        eyes_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    eyes_password.setImageResource(R.drawable.eye_close);
                    password.setSelection(password.getText().length());
                }
                if (event.getAction()==MotionEvent.ACTION_UP){
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eyes_password.setImageResource(R.drawable.eye_open);
                    password.setSelection(password.getText().length());
                }
                return true;
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread() {
                    @Override
                    public void run() {
                        //传递json
                        JSONObject jsonObject = new JSONObject();
                        if (!HttpUtil.isNetAvailable(Login.this))
                        {
                            Looper.prepare();
                            Toast.makeText(Login.this, "网络不可用", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        try {
                            jsonObject.put("UserName",username.getText().toString());
                            jsonObject.put("AccountPassWord",password.getText().toString());
                            if (checkBox.isChecked()){
                                saveToPre(getBaseContext(), username, password);
                            }
                            else {
                                deleteToPre(getBaseContext());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            String callStr = HttpUtil.post("http://192.168.43.202:8080/Smart_Parking_Service/Login", jsonObject.toString());
                            JSONObject call_json = new JSONObject(callStr);
                            if (call_json.getString("Status").equals("1")){
                                //在子线程中调用ui线程
                               runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Login.this, "登录成功", Toast.LENGTH_SHORT).show();
                                        UserInfo userInfo=new UserInfo();
                                        try {
                                            userInfo.setUserId(call_json.getInt("UserId"));
                                            userInfo.setAccountPassWord(call_json.getString("AccountPassWord"));
                                            userInfo.setUserName(call_json.getString("UserName"));
                                            userInfo.setPhoneNumber(call_json.getString("PhoneNumber"));
                                            userInfo.setSafeCardNumber(call_json.getString("SafeCardNumber"));
                                            userInfo.setUserCarNumber(call_json.getString("UserCarNumber"));
                                            userInfo.setUserRealName(call_json.getString("UserRealName"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Intent intent=new Intent(Login.this,Baidu_Map.class);
                                        intent.putExtra("userinfo",userInfo);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }

                        } catch (IOException e) {
                            Looper.prepare();
                            Toast.makeText(Login.this, "服务器异常", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            e.printStackTrace();
                        } catch (JSONException e) {
                            Looper.prepare();
                            Toast.makeText(Login.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            e.printStackTrace();
                        }

                    }
                }.start();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,Register.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
            AppManager.AppExit(Login.this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public static void readFromPre(Context context, EditText username, EditText password){
        SharedPreferences sharedPreferences = context.getSharedPreferences("login_info",context.MODE_PRIVATE);
        username.setText(sharedPreferences.getString("username",""));
        password.setText(sharedPreferences.getString("password",""));
    }
    public static void saveToPre(Context context, EditText username, EditText password){
        SharedPreferences sharedPreferences = context.getSharedPreferences("login_info",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username",username.getText().toString());
        editor.putString("password",password.getText().toString());
        editor.apply();
    }
    public static void deleteToPre(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("login_info",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }
}
