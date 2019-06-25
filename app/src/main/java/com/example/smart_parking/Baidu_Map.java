package com.example.smart_parking;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.utils.DistanceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utils.AppManager;
import Utils.PoiOverlay;
import javaBean.ParkingInfo;
import javaBean.PlaceInfo;
import javaBean.UserInfo;


public class Baidu_Map extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener, SensorEventListener, OnGetPoiSearchResultListener, AdapterView.OnItemClickListener,
        ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupCollapseListener, DetailAdpater.CancleClick {
    private MapView                    mMapView;
    private BaiduMap                   mBaiduMap;
    public  LocationClient             mLocationClient;
    public  BDAbstractLocationListener mBdAbstractLocationListener;
    private MyLocationData             locData;
    private Button                     btn_location;
    private Button                     search_go;
    private LatLng                     location;
    private LatLng                     Destination;
    private LinearLayout               detailinfo;
    private boolean                    isFirstLoc;
    private TextView                   city;
    private List<PlaceInfo>            placeInfos   = new ArrayList<PlaceInfo>();
    private List<ParkingInfo>          parkingInfos = new ArrayList<ParkingInfo>();
    private PlaceInfo                  placeInfo    = new PlaceInfo();
    private ParkingInfo                parkingInfo  = new ParkingInfo();
    private DrawerLayout               drawerLayout;
    private NavigationView             navigationView;
    private ImageView                  User_menu;
    private SensorManager              sensorManager;
    private PoiSearch                  mPoiSearch   =null;
    private ExpandableListView         listView;
    private UserInfo                   userInfo     =null;
    private long                       ExitTime;
    private int                        Direction    =0;
    private double                     lastX        = 0.0;
    private double                     CurrentLat   = 0.0;
    private double                     CurrentLng   = 0.0;
    private float                      CurrentAccracy;
    private boolean                    isFirstPop   = true;
    private Double                     Lat;
    private Double                     lon;
    private Double                     end_latitude;
    private Double                     end_longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_baidu__map);
        AppManager.addActivity(this);
        userInfo = (UserInfo) getIntent().getSerializableExtra("userinfo");
        String Destination = getIntent().getStringExtra("destination");
         Lat = getIntent().getDoubleExtra("lat",0);
         lon = getIntent().getDoubleExtra("lon",0);
        isFirstLoc = getIntent().getBooleanExtra("isfirstloc",true);
        initView();
        initMap();
        Show_parking(Destination, Lat, lon);
    }
//poi附近搜索接口
    private void Show_parking(String destination, Double lat, Double lon) {
        if (destination != null){
            search_go.setText(destination);
            Destination = new LatLng(lat,lon);
            PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
                    .keyword("停车场")
                    .sortType(PoiSortType.distance_from_near_to_far)
                    .location(Destination)
                    .radius(3000)
                    .pageNum(0)
                    .scope(1);
            mPoiSearch.searchNearby(nearbySearchOption);
        }
    }

    //设置地图整体上移,防止挡住底部UI
    private void SetMapviewUp() {
        detailinfo.setVisibility(View.VISIBLE);
        mBaiduMap.setViewPadding(0,0,0,210);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btn_location.getLayoutParams();
        layoutParams.setMargins(0,0,0,330);
        btn_location.setLayoutParams(layoutParams);
    }
    //使地图回复原样
    private void setmMapViewDown(){
        if (isFirstPop){detailinfo.setVisibility(View.GONE);
        isFirstPop=false;
        }
        else {
            detailinfo.setVisibility(View.GONE);
            mBaiduMap.setViewPadding(0,0,0,0);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btn_location.getLayoutParams();
            layoutParams.setMargins(0,0,0,120);
            btn_location.setLayoutParams(layoutParams);
        }

    }

    //设置城市名
    public void SetCity(final String str) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        city.setText(str);
                    }
                });
            }
        }.start();
    }
    //初始化各种控件
    private void initView() {
        mMapView = findViewById(R.id.bmapView);
        btn_location = findViewById(R.id.location);
        btn_location.setOnClickListener(this);
        city = findViewById(R.id.city);
        drawerLayout = findViewById(R.id.drawr_layout);
        navigationView = findViewById(R.id.nav);
        View head = navigationView.getHeaderView(0);
        TextView User_name=head.findViewById(R.id.show_username);
        User_name.setText(userInfo.getUserName());
        User_menu = findViewById(R.id.user_menu);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        User_menu.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        sensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
        search_go=findViewById(R.id.btn_searchGo);
        search_go.setOnClickListener(this);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        listView = findViewById(R.id.listview);
        listView.setOnItemClickListener(this);
        listView.setOnGroupExpandListener(this);
        listView.setOnGroupCollapseListener(this);
        detailinfo = findViewById(R.id.DetalInfo);
        setmMapViewDown();
        Button btn_nav=findViewById(R.id.btn_nav);
        Button btn_reserve=findViewById(R.id.btn_reserve);
        btn_nav.setOnClickListener(this);
        btn_reserve.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
    }
    //初始化地图
    private void initMap() {
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,true,null));
        mLocationClient = new LocationClient(this);
        initLocation();
        mBdAbstractLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mBdAbstractLocationListener);
        mLocationClient.start();
        mLocationClient.requestLocation();
    }
//    设置定位属性
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setPriority(LocationClientOption.GpsFirst);
        option.setScanSpan(1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);
        option.setOpenAutoNotifyMode();
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
    }
//各种按钮的响应事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location:
                setmMapViewDown();
                MapStatus mapStatus = new MapStatus.Builder().target(location)
                        .zoom(17.0f)
                        .build();
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
                mBaiduMap.setMapStatus(mapStatusUpdate);

                break;
            case R.id.user_menu:
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
                break;
            case R.id.btn_searchGo:
                Intent intent = new Intent(Baidu_Map.this,Search_View.class);
                intent.putExtra("userinfo",userInfo);
                intent.putExtra("city", city.getText().toString());
                startActivity(intent);
                break;
            case R.id.btn_nav:
                Intent intent1 = new Intent(this,baidu.navi.sdkdemo.newif.DemoMainActivity.class);
                intent1.putExtra("start_lat",CurrentLat);
                intent1.putExtra("start_lon",CurrentLng);
                intent1.putExtra("end_lat",end_latitude);
                intent1.putExtra("end_lon",end_longitude);
                startActivity(intent1);
                break;
            case R.id.btn_reserve:
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        JSONObject jsonObject = new JSONObject();
//                        try {
//                            jsonObject.put("ParkName",parkingInfo.getPlaceName());
//                            jsonObject.put("ParkPlaceLat",parkingInfo.getParkPlaceLat().toString());
//                            jsonObject.put("ParkPlaceLng",parkingInfo.getParkPlaceLng().toString());
//                            jsonObject.put("SaleId", parkingInfo.getCharge());
//                            jsonObject.put("TotalCarNumber",parkingInfo.getTotalCarNumber());
//                            jsonObject.put("FreeNumber",parkingInfo.getFreeNumber());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        try {
//                            String callStr = HttpUtil.post("http://192.168.43.202:8080/Smart_Parking_Service/Insert_parkinginfo",jsonObject.toString());
//                            if (Integer.parseInt(callStr)==1){
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(Baidu_Map.this,"插入成功",Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
                Intent intent2 = new Intent(this,Reservation_View.class);
                intent2.putExtra("parkinginfo",parkingInfo);
                intent2.putExtra("userinfo",userInfo);
                startActivity(intent2);
                break;
        }
    }

//侧边菜单栏点击事件
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.show_userinfo:
                Intent intent=new Intent(Baidu_Map.this,Show_userinfo.class);
                intent.putExtra("userinfo",userInfo);
                startActivity(intent);
                break;
            case R.id.update_safecardinfo:
                Intent intent1=new Intent(Baidu_Map.this,Update_SafeCardNum.class);
                intent1.putExtra("userinfo",userInfo);
                startActivity(intent1);
                break;
            case R.id.show_reservation:
                drawerLayout.closeDrawer(navigationView);
                Intent intent5 = new Intent(this,Show_Reverations.class);
                intent5.putExtra("userinfo",userInfo);
                startActivity(intent5);
                break;
            case R.id.update_password:
                Intent intent2=new Intent(Baidu_Map.this,Update_PassWord.class);
                intent2.putExtra("userinfo",userInfo);
                startActivity(intent2);
                break;
            case R.id.update_usercarnumber:
                Intent intent3=new Intent(Baidu_Map.this,Update_UserCarNum.class);
                intent3.putExtra("userinfo",userInfo);
                startActivity(intent3);
                break;
            case R.id.exit_app:
                drawerLayout.closeDrawer(navigationView);
                AppManager.AppExit(Baidu_Map.this);
                break;
            case R.id.logout:
                drawerLayout.closeDrawer(navigationView);
                Intent intent4=new Intent(Baidu_Map.this,Login.class);
                startActivity(intent4);
                AppManager.finishCurrentActivity();
                break;
        }
        return true;
    }
//返回键监听,实现点击2下返回键退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (System.currentTimeMillis()-ExitTime>1000){
            Toast.makeText(Baidu_Map.this,"再按一次退出本程序",Toast.LENGTH_SHORT).show();
            ExitTime=System.currentTimeMillis();
        }
        else {
            AppManager.AppExit(Baidu_Map.this);
        }
    }
//方向传感器
    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            Direction = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(CurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(Direction).latitude(CurrentLat)
                    .longitude(CurrentLng).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
//POI搜索回调函数
    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if ((poiResult.error!= SearchResult.ERRORNO.NO_ERROR)){
            Toast.makeText(Baidu_Map.this,"未找到结果",Toast.LENGTH_SHORT).show();
        }
        else {
            mBaiduMap.clear();
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.destination);
            LatLng destination = new LatLng(Lat, lon);
            OverlayOptions overlayOptions;
            overlayOptions = new MarkerOptions().position(destination).icon(descriptor).zIndex(2).draggable(false);
            Marker marker = (Marker) mBaiduMap.addOverlay(overlayOptions);
            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(poiResult);
            overlay.addToMap();
            overlay.zoomToSpan();
           }
    }
//点击marker显示详情
    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(Baidu_Map.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        } else {
            placeInfos=new ArrayList<PlaceInfo>();
            parkingInfo = new ParkingInfo();
            parkingInfos = new ArrayList<ParkingInfo>();
            placeInfo.setName(poiDetailResult.getName());
            placeInfo.setAddress(poiDetailResult.getAddress());
            end_latitude = poiDetailResult.getLocation().latitude;
            end_longitude = poiDetailResult.getLocation().longitude;
            Double distance = DistanceUtil.getDistance(new LatLng(end_latitude,end_longitude) , new LatLng(Lat,lon));
            placeInfo.setDistance(distance);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject show_parkinginfo = new JSONObject();
                    try {
                        show_parkinginfo.put("ParkPlaceLat",end_latitude);
                        show_parkinginfo.put("ParkPlaceLng",end_longitude);
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
                                    parkingInfo.setFreeTime("2");
                                    parkingInfo.setPlaceName(call_json.getString("ParkName"));
                                    parkingInfo.setPlaceId(Integer.parseInt(call_json.getString("PlaceId")));
                                    parkingInfo.setParkPlaceLat(Double.valueOf(call_json.getString("ParkPlaceLat")));
                                    parkingInfo.setParkPlaceLng(Double.valueOf(call_json.getString("ParkPlaceLng")));
                                    parkingInfo.setTotalCarNumber(call_json.getString("TotalCarNumber"));
                                    parkingInfo.setFreeNumber(call_json.getString("FreeNumber"));
                                    parkingInfo.setCharge(call_json.getString("SaleId"));
                                    parkingInfos.add(parkingInfo);
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
            placeInfo.setParkingInfo(parkingInfos);
            placeInfos.add(placeInfo);
            DetailAdpater adapter = new DetailAdpater(placeInfos,this,R.layout.list_item,R.layout.item_detail,Baidu_Map.this);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            SetMapviewUp();
            MapStatus mapStatus = new MapStatus.Builder().target(new LatLng(end_latitude, end_longitude))
                                                         .zoom(18f)
                                                         .build();
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
            mBaiduMap.setMapStatus(mapStatusUpdate);
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
        if (poiDetailSearchResult.error!= SearchResult.ERRORNO.NO_ERROR){
            Toast.makeText(Baidu_Map.this,"未找到结果",Toast.LENGTH_SHORT).show();
        }
        else {
            List<PoiDetailInfo> poiDetailInfoList = poiDetailSearchResult.getPoiDetailInfoList();
            if (poiDetailInfoList == null || poiDetailInfoList.isEmpty()) {
                Toast.makeText(Baidu_Map.this, "抱歉，检索结果为空", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < poiDetailInfoList.size(); i++) {
                PoiDetailInfo poiDetailInfo = poiDetailInfoList.get(i);
                if (poiDetailInfo != null) {
                    Toast.makeText(Baidu_Map.this,
                            poiDetailInfo.getName() + ": " + poiDetailInfo.getAddress(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
//expandlistview的展开和收缩事件
    @Override
    public void onGroupExpand(int groupPosition) {
        mBaiduMap.setViewPadding(0,0,0,300);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btn_location.getLayoutParams();
        layoutParams.setMargins(0,0,0,420);
        btn_location.setLayoutParams(layoutParams);
    }

    @Override
    public void onGroupCollapse(int groupPosition) {
        SetMapviewUp();
    }


    @Override
    public void Btn_Cancle(View v) {
        setmMapViewDown();
    }

    //百度SDK覆盖物实现函数
    private class MyPoiOverlay extends PoiOverlay {
        MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
            // }
            return true;
        }
    }
    //百度地图SDK定位函数
    public class MyLocationListener extends BDAbstractLocationListener {
        public void onReceiveLocation(BDLocation bdLocation) {
            location =new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
            if (location == null || mBaiduMap == null){
                return;
            }
            CurrentAccracy=bdLocation.getRadius();
            CurrentLat=bdLocation.getLatitude();
            CurrentLng=bdLocation.getLongitude();
            locData=new MyLocationData.Builder()
                    .accuracy(0)
                    .direction(Direction)
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            String city=bdLocation.getCity();
            SetCity(city);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng l1 = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(l1).zoom(17.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }

        }
    }
}

