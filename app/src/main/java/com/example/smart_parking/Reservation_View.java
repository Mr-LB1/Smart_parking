package com.example.smart_parking;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Utils.AppManager;
import javaBean.ParkingInfo;
import javaBean.UserInfo;

public class Reservation_View extends AppCompatActivity implements View.OnClickListener {
private TextView    Parking_Car_Num;
private TextView    ParkName;
private TextView    Charge;
private Button      Btn_Reserve;
private Button      Btn_Back_to_Main;
private UserInfo    userInfo;
private ParkingInfo parkingInfo;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.addActivity(this);
        setContentView(R.layout.activity_reservation__view);
        initView();
        parkingInfo = (ParkingInfo) getIntent().getSerializableExtra("parkinginfo");
        userInfo = (UserInfo) getIntent().getSerializableExtra("userinfo");
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject show_parkinginfo = new JSONObject();
                try {
                    show_parkinginfo.put("PlaceId",parkingInfo.getPlaceId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    String callStr = HttpUtil.post("http://192.168.43.202:8080/Smart_Parking_Service/Show_parkinginfo",show_parkinginfo.toString());
                    JSONObject call_json = new JSONObject(callStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                parkingInfo.setTotalCarNumber(call_json.getString("TotalCarNumber"));
                                parkingInfo.setFreeNumber(call_json.getString("FreeNumber"));
                                parkingInfo.setPlaceName(call_json.getString("ParkName"));
                                parkingInfo.setCharge(call_json.getString("SaleId"));
                                Parking_Car_Num.setText(parkingInfo.getFreeNumber()+"/"+parkingInfo.getTotalCarNumber());
                                ParkName.setText(parkingInfo.getPlaceName());
                                Charge.setText(parkingInfo.getCharge()+"元/小时");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

        }).start();

    }

    private void initView() {
        Parking_Car_Num = findViewById(R.id.parking_car_num);
        ParkName = findViewById(R.id.parkname);
        Charge = findViewById(R.id.charge);
        Btn_Reserve = findViewById(R.id.btn_Reserve);
        Btn_Back_to_Main = findViewById(R.id.btn_back_main);
        Btn_Reserve.setOnClickListener(this);
        Btn_Back_to_Main.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_Reserve:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject reserve = new JSONObject();
                        try {
                            reserve.put("PlaceId",parkingInfo.getPlaceId());
                            reserve.put("UserId",userInfo.getUserId());
                            reserve.put("NaviGation",1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            String callStr = HttpUtil.post("http://192.168.43.202:8080/Smart_Parking_Service/Create_reservation",reserve.toString());
                            if (callStr.equals("1")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Reservation_View.this,"订单创建成功",Toast.LENGTH_SHORT).show();
                                        AppManager.finishCurrentActivity();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.btn_back_main:
                AppManager.finishCurrentActivity();
                break;
        }
    }
}
