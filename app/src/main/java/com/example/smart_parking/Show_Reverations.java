package com.example.smart_parking;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import javaBean.Reservation;
import javaBean.UserInfo;

public class Show_Reverations extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
private ListView               show_reveration;
private UserInfo               userInfo;
private ArrayList<Reservation> reservations;
private Reservation            reservation = new Reservation();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show__reverations);
        userInfo = (UserInfo) getIntent().getSerializableExtra("userinfo");
        show_reveration = findViewById(R.id.show_Reservation);
        show_reveration.setOnItemClickListener(this);
        show_reveration.setOnItemLongClickListener(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("UserId",userInfo.getUserId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    String callStr = HttpUtil.post("http://192.168.43.202:8080/Smart_Parking_Service/Show_reservation",jsonObject.toString());
                    JSONObject call_json = new JSONObject(callStr);
                    reservations = new ArrayList<Reservation>();
                    for (int i = 1; i <= call_json.length(); i++){
                            String Reserve = call_json.getString(String.valueOf(i));
                            JSONObject reserve = new JSONObject(Reserve);
                            reservation = new Reservation();
                            reservation.setRevervaTionId(Integer.parseInt(reserve.getString("ReservaTionId")));
                            reservation.setParkName(reserve.getString("ParkName"));
                            reservation.setUserName(userInfo.getUserName());
                            reservation.setCreateDate(reserve.getString("FromDate"));
                            reservation.setNaviGation(Integer.parseInt(reserve.getString("NavigaTion")));
                            reservations.add(reservation);}
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ReservationAdapter adapter = new ReservationAdapter(Show_Reverations.this,R.layout.reservation_item,reservations);
                            show_reveration.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
        }).start();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Show_Reverations.this);
        builder.setTitle("订单处理操作").setMessage("您要进行什么操作?");
        builder.setPositiveButton("完成订单", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("RevervaTionId",reservations.get(position).getRevervaTionId());
                            HttpUtil.post("http://192.168.43.202:8080/Smart_Parking_Service/Delete_reservation",jsonObject.toString());
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        builder.setNegativeButton("取消订单", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("RevervaTionId",reservations.get(position).getRevervaTionId());
                            HttpUtil.post("http://192.168.43.202:8080/Smart_Parking_Service/Delete_reservation",jsonObject.toString());
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        builder.show();
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Show_Reverations.this);
        builder.setTitle("订单处理操作").setMessage("您要进行什么操作?");
        builder.setPositiveButton("完成订单", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("RevervaTionId",reservations.get(position).getRevervaTionId());
                            HttpUtil.post("http://192.168.43.202:8080/Smart_Parking_Service/Delete_reservation",jsonObject.toString());
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        builder.setNegativeButton("取消订单", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("RevervaTionId",reservations.get(position).getRevervaTionId());
                            HttpUtil.post("http://192.168.43.202:8080/Smart_Parking_Service/Delete_reservation",jsonObject.toString());
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        builder.show();
    }
}
