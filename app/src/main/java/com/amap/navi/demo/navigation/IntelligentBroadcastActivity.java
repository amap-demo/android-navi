package com.amap.navi.demo.navigation;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.AimLessMode;
import com.amap.api.navi.model.AMapCongestionLink;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.navi.demo.R;
import com.amap.navi.demo.util.NaviUtil;
import com.amap.navi.demo.util.TTSController;
import com.autonavi.tbt.TrafficFacilityInfo;

import java.util.Timer;
import java.util.TimerTask;


public class IntelligentBroadcastActivity extends Activity implements AMapNaviListener {


    public static final String TAG = "wlx";
    private MapView mapView;
    private AMap aMap;
    private Marker myLocationMarker;
    private TextView trafficfacility_tv,congestioninfo_tv;

    // 是否需要跟随定位
    private boolean isNeedFollow = true;

    // 处理静止后跟随的timer
    private Timer needFollowTimer;

    // 屏幕静止DELAY_TIME之后，再次跟随
    private long DELAY_TIME = 5000;
    private AMapNavi aMapNavi;
    private TTSController ttsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intelligent_broadcast);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        init();
    }

    /**
     * 初始化各种对象
     */
    private void init() {
        if (aMap == null) {
            aMapNavi = AMapNavi.getInstance(this);
            aMapNavi.startAimlessMode(AimLessMode.CAMERA_AND_SPECIALROAD_DETECTED);//-巡航模式播报电子眼和特殊路段

            ttsManager = TTSController.getInstance(this);
            ttsManager.init();

            aMapNavi.addAMapNaviListener(this);
            aMapNavi.addAMapNaviListener(ttsManager);

            aMap = mapView.getMap();
            // 初始化 显示我的位置的Marker
            myLocationMarker = aMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.drawable.car))));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(16));

            setMapInteractiveListener();
        }
        trafficfacility_tv = (TextView)findViewById(R.id.trafficfacility);
        congestioninfo_tv = (TextView)findViewById(R.id.congestioninfo);

    }

    /**
     * 设置导航监听
     */
    private void setMapInteractiveListener() {
        Log.i("MY","setMapInteractiveListener");

        aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {

            @Override
            public void onTouch(MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        Log.i("MY","按下屏幕");
                        // 按下屏幕
                        // 如果timer在执行，关掉它
                        clearTimer();
                        // 改变跟随状态
                        isNeedFollow = false;
                        break;

                    case MotionEvent.ACTION_UP:
                        // 离开屏幕
                        Log.i("MY","离开屏幕");
                        startTimerSomeTimeLater();
                        break;

                    default:
                        break;
                }
            }
        });

    }

    /**
     * 取消timer任务
     */
    private void clearTimer() {
        if (needFollowTimer != null) {
            needFollowTimer.cancel();
            needFollowTimer = null;
        }
    }

    /**
     * 如果地图在静止的情况下
     */
    private void startTimerSomeTimeLater() {
        // 首先关闭上一个timer
        clearTimer();
        needFollowTimer = new Timer();
        // 开启一个延时任务，改变跟随状态
        needFollowTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                isNeedFollow = true;
            }
        }, DELAY_TIME);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        ttsManager.destroy();
        aMapNavi.stopAimlessMode();
        aMapNavi.destroy();
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation location) {
//        Log.i("MY","onLocationChange");
        if (location != null) {
            LatLng latLng = new LatLng(location.getCoord().getLatitude(),
                    location.getCoord().getLongitude());
            // 显示定位小图标，初始化时已经创建过了，这里修改位置即可
            myLocationMarker.setPosition(latLng);
            if (isNeedFollow) {
                // 跟随
                aMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng));
            }
        } else {
            Toast.makeText(IntelligentBroadcastActivity.this, "定位出现异常",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGetNavigationText(int i, String s) {
    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] amapServiceAreaInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {
        Log.i("MY","OnUpdateTrafficFacility11111--------------------");
        String type = getbroadcasttype(aMapNaviTrafficFacilityInfo.getBroadcastType());
        StringBuilder sb = new StringBuilder();
        sb.append("道路设施类型 :" + type);
        sb.append(" "+ NaviUtil.getFriendlyLength(aMapNaviTrafficFacilityInfo.getDistance()));
        Log.i("MY",sb.toString());
        trafficfacility_tv.setText(sb.toString());
//        Toast.makeText(this, "(trafficFacilityInfo.coor_X+trafficFacilityInfo.coor_Y+trafficFacilityInfo.distance+trafficFacilityInfo.limitSpeed):" + (aMapNaviTrafficFacilityInfo.getCoorX() + aMapNaviTrafficFacilityInfo.getCoorY() + aMapNaviTrafficFacilityInfo.getDistance() + aMapNaviTrafficFacilityInfo.getLimitSpeed()), Toast.LENGTH_LONG).show();
    }

    private String getbroadcasttype(int broadcastType) {
        /**
         * 获取道路设施类型 0：未知道路设施 4：测速摄像头、测速雷达 5：违章摄像头 10:请谨慎驾驶
         * 11:有连续拍照 12：铁路道口 13：注意落石（左侧） 14：事故易发地段 15：易滑 16：村庄
         * 18：前方学校 19：有人看管的铁路道口 20：无人看管的铁路道口 21：两侧变窄 22：向左急弯路
         * 23：向右急弯路 24：反向弯路 25：连续弯路 26：左侧合流标识牌 27：右侧合流标识牌
         * 28：监控摄像头 29：专用道摄像头 31：禁止超车 36：右侧变窄 37：左侧变窄 38：窄桥
         * 39：左右绕行 40：左侧绕行 41：右侧绕行 42：注意落石（右侧） 43：傍山险路（左侧）
         * 44：傍山险路（右侧） 47：上陡坡 48：下陡坡 49：过水路面 50：路面不平 52：慢行
         * 53：注意危险 58：隧道 59：渡口 92:闯红灯 93:应急车道 94:非机动车道
         * 100：不绑定电子眼高发地 101:车道违章 102:超速违章
         */
        String type = "";
        switch (broadcastType){
            case 0:
                type = "未知道路设施";
                break;
            case 4:
                type = "测速摄像头、测速雷达";
                break;
            case 5:
                type = "违章摄像头 ";
                break;
            case 10:
                type = "请谨慎驾驶";
                break;
            case 11:
                type = "有连续拍照";
                break;
            case 12:
                type = "铁路道口";
                break;
            case 13:
                type = "注意落石（左侧）";
                break;
            case 53:
                type = "注意危险";
                break;
            case 29:
                type = "专用道摄像头";
                break;
            case 94:
                type = "非机动车道";
                break;
            default:
                type = "其他道路设施："+broadcastType+"(请对应参考手册说明)";
        }

        return type;
    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    //巡航模式（无路线规划）下，道路设施信息更新回调
    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
//        Log.i("MY","OnUpdateTrafficFacility--------------------");
//        for (AMapNaviTrafficFacilityInfo info :
//                aMapNaviTrafficFacilityInfos) {
//            String type = getbroadcasttype(info.getBroadcastType());
//            StringBuilder sb = new StringBuilder();
//            sb.append("道路设施类型 :" + type);
//            sb.append("\n 距离："+ info.getDistance());
//            Log.i("MY",sb.toString());
////            Toast.makeText(this, "(trafficFacilityInfo.coor_X+trafficFacilityInfo.coor_Y+trafficFacilityInfo.distance+trafficFacilityInfo.limitSpeed):" + (info.getCoorX() + info.getCoorY() + info.getDistance() + info.getLimitSpeed()), Toast.LENGTH_LONG).show();
//        }
    }

    //巡航模式（无路线规划）下，统计信息更新回调 连续5个点大于15km/h后开始回调
    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {
//        Log.i("MY","updateAimlessModeStatistics--------------------");
//        Toast.makeText(this, "看log", Toast.LENGTH_SHORT).show();
        //巡航模式（无路线规划）下轨迹距离
        Log.d(TAG, "distance=" + aimLessModeStat.getAimlessModeDistance());
        //巡航模式（无路线规划）下运行时间
        Log.d(TAG, "time=" + aimLessModeStat.getAimlessModeTime());
    }


    //巡航模式（无路线规划）下，统计信息更新回调 当拥堵长度大于500米且拥堵时间大于5分钟时回调
    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {
//        Toast.makeText(this, "看log", Toast.LENGTH_SHORT).show();
        Log.i("MY","updateAimlessModeCongestionInfo--------------------");
        int status = aimLessModeCongestionInfo.getCongestionStatus();
        StringBuilder sb = new StringBuilder();
        if(status != 0){
            sb.append("拥堵道路名称:" + aimLessModeCongestionInfo.getRoadName())
                    .append("  拥堵状态:" + getcongestionstatus(status))
                    .append("\n拥堵区域路径长度:" + NaviUtil.getFriendlyLength(aimLessModeCongestionInfo.getLength()))
                    .append("  预计通过时间 =" + NaviUtil.getFriendlyTime(aimLessModeCongestionInfo.getTime()));
//                    .append("\neventLonLat=" + aimLessModeCongestionInfo.getEventLon() + "," + aimLessModeCongestionInfo.getEventLat())
//                    .append("\n事件类型:"+ aimLessModeCongestionInfo.getEventType());
            Log.i("MY",sb.toString());
            congestioninfo_tv.setText(sb.toString());
        }else {
            congestioninfo_tv.setText("");
        }

//        Toast.makeText(this,"roadName=" + aimLessModeCongestionInfo.getRoadName(), Toast.LENGTH_SHORT).show();
//        Log.d(TAG, "roadName=" + aimLessModeCongestionInfo.getRoadName());
//        Log.d(TAG, "CongestionStatus=" + aimLessModeCongestionInfo.getCongestionStatus());
//        Log.d(TAG, "eventLonLat=" + aimLessModeCongestionInfo.getEventLon() + "," + aimLessModeCongestionInfo.getEventLat());
//        Log.d(TAG, "length=" + aimLessModeCongestionInfo.getLength());
//        Log.d(TAG, "time=" + aimLessModeCongestionInfo.getTime());
        for (AMapCongestionLink link :
                aimLessModeCongestionInfo.getAmapCongestionLinks()) {
            Log.d(TAG, "status=" + link.getCongestionStatus());
            for (NaviLatLng latlng : link.getCoords()
                    ) {
                Log.d(TAG, latlng.toString());
            }
        }
    }
    public String getcongestionstatus(int status){
//        0未知状态，1通畅，2缓行，3 阻塞，4 严重阻塞
        String congestionstatus = "";
        switch (status){
            case 1:
                congestionstatus = "通畅";
                break;
            case 2:
                congestionstatus = "缓行";
                break;
            case 3:
                congestionstatus = "阻塞";
                break;
            case 4:
                congestionstatus = "严重阻塞";
                break;
            default:
                congestionstatus = "未知";
        }
        return congestionstatus;

    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviinfo) {
    }
}
