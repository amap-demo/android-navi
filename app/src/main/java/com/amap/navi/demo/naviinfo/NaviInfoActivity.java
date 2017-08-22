package com.amap.navi.demo.naviinfo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviGuide;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.navi.demo.R;
import com.amap.navi.demo.util.NaviUtil;
import com.autonavi.tbt.TrafficFacilityInfo;

import java.util.ArrayList;


public class NaviInfoActivity extends Activity implements AMapNaviListener, AMapNaviViewListener {
    private AMapNavi mAMapNavi;
    //起点终点
    protected NaviLatLng mNaviEnd = new NaviLatLng(40.011494, 116.467332);
    protected NaviLatLng mNaviStart = new NaviLatLng(39.994547,116.472546);
    //起点终点列表
    private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
    private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
    private ImageView imageView, crossimage;
    private TextView navipath_alllength, navipath_alltime, navipath_startandend,navipath_strategy;
    private TextView naviinfo_retaindistance, naviinfo_currentroad, naviinfo_nextroad, naviinfo_pathretaindistance, naviinfo_pathretaintime;
    private TextView location_coord, location_bearing;
    private TextView servicearea_info,camera_info,navistate;
    private LinearLayout cross,servicearea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_naviinfo);

        mAMapNavi = AMapNavi.getInstance(this);
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.setEmulatorNaviSpeed(200);
        initview();
    }

    private void initview() {
        navistate = (TextView)findViewById(R.id.navistate);
        navipath_alllength = (TextView)findViewById(R.id.navipath_alllength);
        navipath_alltime = (TextView)findViewById(R.id.navipath_alltime);
        navipath_startandend = (TextView)findViewById(R.id.navipath_startandend);
        navipath_strategy = (TextView)findViewById(R.id.navipath_strategy);
        imageView = (ImageView)findViewById(R.id.naviinfo_icon);
        naviinfo_retaindistance = (TextView)findViewById(R.id.naviinfo_retaindistance);
        naviinfo_currentroad = (TextView)findViewById(R.id.naviinfo_currentroad);
        naviinfo_nextroad = (TextView)findViewById(R.id.naviinfo_nextroad);
        naviinfo_pathretaindistance = (TextView)findViewById(R.id.naviinfo_pathretaindistance);
        naviinfo_pathretaintime = (TextView)findViewById(R.id.naviinfo_pathretaintime);
        location_coord = (TextView)findViewById(R.id.location_coord);
        location_bearing = (TextView)findViewById(R.id.location_bearing);
        servicearea_info = (TextView)findViewById(R.id.servicearea_info);
        camera_info = (TextView)findViewById(R.id.camera_info);
        crossimage = (ImageView) findViewById(R.id.crossimage);
        cross = (LinearLayout) findViewById(R.id.cross);
        servicearea = (LinearLayout) findViewById(R.id.servicearea);
        imageView.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNavi.stopNavi();
        mAMapNavi.destroy();
    }

    @Override
    public void onInitNaviFailure() {
        Toast.makeText(this, "初始化失败。", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInitNaviSuccess() {
        //初始化成功回调
        mStartPoints.add(mNaviStart);
        mEndPoints.add(mNaviEnd);
        int strategy = 0;
        try {
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavi.strategyConvert(true, false, false, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAMapNavi.calculateDriveRoute(mStartPoints, mEndPoints, null, strategy);
    }

    @Override
    public void onStartNavi(int type) {
        //开始导航回调
        navistate.setText("开始导航");
        AMapNaviPath navipath = mAMapNavi.getNaviPath();
        navipath_alllength.setText("总距离："+NaviUtil.getFriendlyLength(navipath.getAllLength()));
        navipath_alltime.setText("总时长："+NaviUtil.getFriendlyTime(navipath.getAllTime()));
        navipath_startandend.setText("起终点：\n"+navipath.getStartPoint().toString() +"\n"+ navipath.getEndPoint().toString());
//        navipath_strategy.setText("策略："+navipath.getStrategy());

    }

    @Override
    public void onTrafficStatusUpdate() {
        //交通信息更新回调
    }

    @Override
    public void onLocationChange(AMapNaviLocation location) {
        //当前车位置回调。
        location_coord.setText("位置坐标：\n"+location.getCoord().toString());
        location_bearing.setText("方向角度："+location.getBearing());
        /**
         * 回调对象说明，请参见官网API文档，http://amappc.cn-hangzhou.oss-pub.aliyun-inc.com/lbs/static/unzip/Android_Navi_Doc/index.html
         **/
    }

    @Override
    public void onGetNavigationText(int type, String text) {
        //语音播报回调,调用科大讯飞语音合成接口，将需要语音播报的text传入。
    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {
        //停止模拟导航回调
        navistate.setText("模拟导航结束");
    }

    @Override
    public void onArriveDestination() {
        //到达目的地回调
    }

    @Override
    public void onCalculateRouteFailure(int errorInfo) {
        //路径计算失败
        navistate.setText("路径计算失败");
    }

    @Override
    public void onReCalculateRouteForYaw() {
        //偏航重算回调
        navistate.setText("偏航重算");
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        //拥堵重算回调
        navistate.setText("拥堵重算");
    }

    @Override
    public void onArrivedWayPoint(int wayID) {
        //到达途径点
    }

    @Override
    public void onGpsOpenStatus(boolean enabled) {
        //GPS开关状态回调
    }

    @Override
    public void onNaviSetting() {
        //点击导航界面的Setting按钮回调
    }

    @Override
    public void onNaviMapMode(int isLock) {
        //导航界面地图状态的回调。
        //isLock 地图状态，0:车头朝上状态；1:非锁车状态,即车标可以任意显示在地图区域内。
    }

    @Override
    public void onNaviCancel() {
    }

    @Override
    public void onNaviTurnClick() {
        //界面左上角转向操作的点击回调。
    }

    @Override
    public void onNextRoadClick() {
        //界面下一道路名称的点击回调。
    }


    @Override
    public void onScanViewButtonClick() {
        //通知APP全览按钮被点击了
    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapCameraInfos) {
        boolean isshow = false;
        StringBuilder sb = new StringBuilder();
        for (AMapNaviCameraInfo camerainfo : aMapCameraInfos){
            if (camerainfo != null){
                sb.append(getcameratype(camerainfo.getCameraType()))
                        .append(NaviUtil.getFriendlyLength(camerainfo.getCameraDistance()))
                        .append("\n");
                if (camerainfo.getCameraDistance()>20){
                    isshow = true;
                }
            }
        }
        if (isshow){
            camera_info.setText(sb.toString());
            camera_info.setVisibility(View.VISIBLE);
        }else{
            camera_info.setVisibility(View.GONE);
        }

    }

    private String getcameratype(int cameraType) {
        //! 电子眼类型，0 测速摄像头，1为监控摄像头，2为闯红灯拍照，3为违章拍照，4为公交专用道摄像头,5-应急车道拍照
        String cameratype = "摄像头";
        switch (cameraType){
            case 0:
                cameratype = "测速摄像头";
                break;
            case 1:
                cameratype = "监控摄像头";
                break;
            case 2:
                cameratype = "闯红灯拍照";
                break;
            case 3:
                cameratype = "违章拍照";
                break;
            case 4:
                cameratype = "公交专用道摄像头";
                break;
            case 5:
                cameratype = "应急车道拍照";
                break;
        }
        return cameratype;
    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] amapServiceAreaInfos) {

        StringBuilder sb = new StringBuilder();
        for (AMapServiceAreaInfo serviceareainfo : amapServiceAreaInfos){
            if (serviceareainfo != null){
                sb.append(serviceareainfo.getName())
                        .append(NaviUtil.getFriendlyLength(serviceareainfo.getRemainDist()))
                        .append("\n");
            }
        }
        servicearea_info.setText(sb.toString());
  }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviinfo) {
        Bitmap bitmap;
//        byte[] bytes = naviinfo.getIconData();
//        if (bytes != null) {
//            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        } else {
//            bitmap = null;
//        }
//        if (bitmap != null){
//            imageView.setImageBitmap(bitmap);
//        }
        naviinfo_retaindistance.setText("导航动作类型："+naviinfo.getIconType()
        +"\n导航动作距离："+naviinfo.getCurStepRetainDistance()+"米");
        naviinfo_currentroad.setText("当前道路："+naviinfo.getCurrentRoadName());
        naviinfo_nextroad.setText("下一道路："+naviinfo.getNextRoadName());
        naviinfo_pathretaindistance.setText("剩余距离："+ NaviUtil.getFriendlyLength(naviinfo.getPathRetainDistance()));
        naviinfo_pathretaintime.setText("剩余时间："+NaviUtil.getFriendlyTime(naviinfo.getPathRetainTime()));
        /**
         * 回调对象说明，请参见官网API文档，http://amappc.cn-hangzhou.oss-pub.aliyun-inc.com/lbs/static/unzip/Android_Navi_Doc/index.html
         **/
    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {
        //1.8.0开始，不再回调该方法
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {
    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
        if (aMapNaviCross != null && aMapNaviCross.getBitmap() != null){
            crossimage.setImageBitmap(aMapNaviCross.getBitmap());
            crossimage.setVisibility(View.VISIBLE);
        }
        /**
         * 回调对象说明，请参见官网API文档，http://amappc.cn-hangzhou.oss-pub.aliyun-inc.com/lbs/static/unzip/Android_Navi_Doc/index.html
         **/
    }

    @Override
    public void hideCross() {
        //通知APP隐藏转弯信息。
        crossimage.setVisibility(View.GONE);
    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] laneInfos, byte[] laneBackgroundInfo, byte[] laneRecommendedInfo) {
        //通知APP显示车道信息。

        /**
         * 回调对象说明，请参见官网API文档，http://amappc.cn-hangzhou.oss-pub.aliyun-inc.com/lbs/static/unzip/Android_Navi_Doc/index.html
         **/
    }

    @Override
    public void hideLaneInfo() {
        //通知APP隐藏车道信息。
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        //多路线计算成功
        AMapNavi.getInstance(this).startNavi(NaviType.EMULATOR);
    }

    @Override
    public void notifyParallelRoad(int i) {
        //主辅路发生变化更新此方法
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
        //巡航模式（无路线规划）下，道路设施信息更新回调

        /**
         * 回调对象说明，请参见官网API文档，http://amappc.cn-hangzhou.oss-pub.aliyun-inc.com/lbs/static/unzip/Android_Navi_Doc/index.html
         **/
    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {
        //巡航模式（无路线规划）下，统计信息更新回调,连续5个点大于15km/h后开始回调

        /**
         * 回调对象说明，请参见官网API文档，http://amappc.cn-hangzhou.oss-pub.aliyun-inc.com/lbs/static/unzip/Android_Navi_Doc/index.html
         **/
    }


    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {
        //巡航模式（无路线规划）下，统计信息更新回调,当拥堵长度大于500米且拥堵时间大于5分钟时回调

        /**
         * 回调对象说明，请参见官网API文档，http://amappc.cn-hangzhou.oss-pub.aliyun-inc.com/lbs/static/unzip/Android_Navi_Doc/index.html
         **/
    }

    @Override
    public void onPlayRing(int i) {

    }


    @Override
    public void onLockMap(boolean isLock) {
        //当前地图锁定发生改变回调此方法，true表示地图是锁定状态。
    }

    @Override
    public void onNaviViewLoaded() {
        //导航页面加载成功
        //请不要使用AMapNaviView.getMap().setOnMapLoadedListener();会overwrite导航SDK内部画线逻辑
    }

    @Override
    public boolean onNaviBackClick() {
        //return true表示使用SDK的返回对话框。
        return false;
    }
}
