package com.example.smart_parking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import javaBean.Reservation;

public class ReservationAdapter extends BaseAdapter {
    private Context           context;
    private int               layout;
    private List<Reservation> reservations;
    public ReservationAdapter(Context context, int layout, List<Reservation> reservations){
        this.context=context;
        this.layout = layout;
        this.reservations = reservations;
    }
    @Override
    public int getCount() {
        return reservations.size();
    }

    @Override
    public Object getItem(int position) {
        return reservations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(layout,parent,false);
        }
        TextView UserName = convertView.findViewById(R.id.username);
        TextView ParkName = convertView.findViewById(R.id.parkname);
        TextView Create_Date = convertView.findViewById(R.id.create_date);
        TextView NavigaTion = convertView.findViewById(R.id.navigation);
        UserName.setText(reservations.get(position).getUserName());
        ParkName.setText(reservations.get(position).getParkName());
        Create_Date.setText(reservations.get(position).getCreateDate());
        if ((reservations.get(position).getNaviGation())==1){
            NavigaTion.setText("预定中");
        }
        else {
            NavigaTion.setText("订单已取消或完成");
        }
        return convertView;
    }
}
