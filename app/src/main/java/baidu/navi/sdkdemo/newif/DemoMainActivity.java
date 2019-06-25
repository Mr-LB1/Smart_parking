/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package baidu.navi.sdkdemo.newif;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.baidu.navisdk.adapter.IBaiduNaviManager;
import Utils.AppManager;
import Utils.GPSUtil;
import com.example.smart_parking.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import baidu.navi.sdkdemo.NormalUtils;

public class DemoMainActivity extends Activity {

    private static final String APP_FOLDER_NAME = "com.example.smart_parking";

    static final String ROUTE_PLAN_NODE = "routePlanNode";

    private static final int NORMAL = 0;
    private static final int EXTERNAL = 1;

    private static final String[] authBaseArr = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private String mSDCardPath = null;

    private static final int authBaseRequestCode = 1;

    private boolean hasInitSuccess = false;

    private BNRoutePlanNode  mStartNode        = null;
    private double           mCurrentLat ;
    private double           mCurrentLng ;
    private Double           start_lat;
    private Double           start_lon;
    private Double           end_lat;
    private Double           end_lon;
    private double[]         desLatLng;
    private LocationManager  mLocationManager;
    private GPSUtil          gpsUtil = new GPSUtil();
    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            mCurrentLat = location.getLatitude();
            mCurrentLng = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.addActivity(DemoMainActivity.this);
        setContentView(R.layout.normal_demo_activity_main);
         start_lat = getIntent().getDoubleExtra("start_lat",0);
         start_lon = getIntent().getDoubleExtra("start_lon",0);
         end_lat = getIntent().getDoubleExtra("end_lat",0);
         end_lon = getIntent().getDoubleExtra("end_lon",0);
// 将GPS设备采集的原始GPS坐标转换成百度坐标
         desLatLng = GPSUtil.bd09_To_gps84(end_lat,end_lon);
        if (initDirs()) {
            initNavi();
        }
        initLocation();
        if (hasInitSuccess){
            if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                BaiduNaviManagerFactory.getBaiduNaviManager().enableOutLog(true);
                BNRoutePlanNode sNode = new BNRoutePlanNode.Builder()
                        .latitude(start_lat)
                        .longitude(start_lon)
                        .coordinateType(BNRoutePlanNode.CoordinateType.BD09LL)
                        .build();
                BNRoutePlanNode eNode = new BNRoutePlanNode.Builder()
                        .latitude(end_lat)
                        .longitude(end_lon)
                        .coordinateType(BNRoutePlanNode.CoordinateType.BD09LL)
                        .build();
                routePlanToNavi(sNode, eNode, NORMAL);
            }
        }
    }

    private void initLocation() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                    .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000, 1000, mLocationListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean hasBasePhoneAuth() {
        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager
                    .PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void initNavi() {
        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (!hasBasePhoneAuth()) {
                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;
            }
        }

        if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
            hasInitSuccess = true;
            return;
        }

        BaiduNaviManagerFactory.getBaiduNaviManager().init(getApplicationContext(),
                mSDCardPath, APP_FOLDER_NAME, new IBaiduNaviManager.INaviInitListener() {

                    @Override
                    public void onAuthResult(int status, String msg) {
                        String result;
                        if (0 == status) {
                            result = "key校验成功!";
                        } else {
                            result = "key校验失败, " + msg;
                        }
//                        Toast.makeText(DemoMainActivity.this, result, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void initStart() {
//                        Toast.makeText(DemoMainActivity.this.getApplicationContext(),
//                                "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void initSuccess() {
//                        Toast.makeText(DemoMainActivity.this.getApplicationContext(),
//                                "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                        hasInitSuccess = true;
                        // 初始化tts
                        initTTS();
                        if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        BaiduNaviManagerFactory.getBaiduNaviManager().enableOutLog(true);
                        BNRoutePlanNode sNode = new BNRoutePlanNode.Builder()
                                .latitude(start_lat)
                                .longitude(start_lon)
                                .coordinateType(BNRoutePlanNode.CoordinateType.BD09LL)
                                .build();
                        BNRoutePlanNode eNode = new BNRoutePlanNode.Builder()
                                .latitude(end_lat)
                                .longitude(end_lon)
                                .coordinateType(BNRoutePlanNode.CoordinateType.BD09LL)
                                .build();
                        routePlanToNavi(sNode, eNode, NORMAL);}
                    }
                    @Override
                    public void initFailed(int errCode) {
//                        Toast.makeText(DemoMainActivity.this.getApplicationContext(),
//                                "百度导航引擎初始化失败 " + errCode, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private void initTTS() {
        // 使用内置TTS
        BaiduNaviManagerFactory.getTTSManager().initTTS(getApplicationContext(),
                getSdcardDir(), APP_FOLDER_NAME, NormalUtils.getTTSAppID());

    }


    private void routePlanToNavi(BNRoutePlanNode sNode, BNRoutePlanNode eNode, final int from) {
        List<BNRoutePlanNode> list = new ArrayList<>();
        list.add(sNode);
        list.add(eNode);

        BaiduNaviManagerFactory.getCommonSettingManager().setCarNum(this, "粤B66666");
        BaiduNaviManagerFactory.getRoutePlanManager().routeplanToNavi(
                list,
                IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT,
                null,
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START:
//                                Toast.makeText(DemoMainActivity.this.getApplicationContext(),
//                                        "算路开始", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
//                                Toast.makeText(DemoMainActivity.this.getApplicationContext(),
//                                        "算路成功", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
//                                Toast.makeText(DemoMainActivity.this.getApplicationContext(),
//                                        "算路失败", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_TO_NAVI:
//                                Toast.makeText(DemoMainActivity.this.getApplicationContext(),
//                                        "算路成功准备进入导航", Toast.LENGTH_SHORT).show();

                                Intent intent = null;
                                if (from == NORMAL) {
                                    intent = new Intent(DemoMainActivity.this,
                                            DemoGuideActivity.class);
                                    AppManager.finishCurrentActivity();
                                } else if (from == EXTERNAL) {
                                    intent = new Intent(DemoMainActivity.this,
                                            DemoExtGpsActivity.class);
                                }

                                startActivity(intent);
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == authBaseRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                } else {
//                    Toast.makeText(DemoMainActivity.this.getApplicationContext(),
//                            "缺少导航基本的权限!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            initNavi();
        }
    }
}
