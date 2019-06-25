package com.example.smart_parking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import javaBean.PlaceInfo;

public class DetailAdpater extends BaseExpandableListAdapter implements View.OnClickListener {
    private List<PlaceInfo> placeInfos;
    private Context         context;
    private int             GroupLayout;
    private int             ChildLayout;
    private CancleClick     cancleClick;
    public DetailAdpater(List<PlaceInfo> placeInfos, Context context, int groupLayout,int childLayout, CancleClick cancleClick) {
        this.placeInfos = placeInfos;
        this.context = context;
        this.GroupLayout = groupLayout;
        this.ChildLayout = childLayout;
        this.cancleClick = cancleClick;
    }

    @Override
    public void onClick(View v) {
        cancleClick.Btn_Cancle(v);
    }

    public interface CancleClick{
        public void Btn_Cancle(View v);
    }
    @Override
    public int getGroupCount() {
        return placeInfos.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
       return placeInfos.get(groupPosition).getParkingInfo().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return placeInfos.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return placeInfos.get(groupPosition).getParkingInfo().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(GroupLayout,parent,false);
        }
        TextView Name = convertView.findViewById(R.id.place_name);
        TextView Address = convertView.findViewById(R.id.address);
        TextView distance = convertView.findViewById(R.id.distance);
        Button cancle = convertView.findViewById(R.id.cancle);
        cancle.setOnClickListener(this);
        Name.setText(placeInfos.get(groupPosition).getName());
        Address.setText(placeInfos.get(groupPosition).getAddress());
        distance.setText(String.format("距目的地%s米",placeInfos.get(groupPosition).getDistance()).substring(0,String.format("距目的地%s米",placeInfos.get(groupPosition).getDistance()).lastIndexOf("."))+"米");
        return convertView;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(ChildLayout,parent,false);
        }
        TextView totalnumber = convertView.findViewById(R.id.totalcarnumber);
        TextView freenumber = convertView.findViewById(R.id.freenumber);
        TextView freetime= convertView.findViewById(R.id.freetime);
        TextView charge = convertView.findViewById(R.id.charge);
        totalnumber.setText(placeInfos.get(groupPosition).getParkingInfo().get(childPosition).getTotalCarNumber());
        freenumber.setText(placeInfos.get(groupPosition).getParkingInfo().get(childPosition).getFreeNumber());
        freetime.setText(placeInfos.get(groupPosition).getParkingInfo().get(childPosition).getFreeTime()+"小时");
        charge.setText(placeInfos.get(groupPosition).getParkingInfo().get(childPosition).getCharge()+"元/小时");
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}