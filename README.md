# android-navi
Android 导航SDK demo

## 前述 ##
- [高德官网申请Key](http://lbs.amap.com/dev/#/).
- [开发指南](http://lbs.amap.com/api/android-navi-sdk/summary/).
- 阅读[参考手册](http://a.amap.com/lbs/static/unzip/Android_Navi_Doc/index.html).
- 工程基于高德导航DK实现


## 说明 ##

`导航组件`

| 功能说明 | 对应文件名 |
| -----|:-----:|
|使用导航组件|IndexActivity|


`出行路线规划`

| 功能说明 | 对应文件名 |
| -----|:-----:|
|驾车路线规划|SingleRouteCalculateActivity, RestRouteShowActivity|
|步行路线规划|WalkRouteCalculateActivity|
|骑行路线规划|RideRouteCalculateActivity|


`在地图上导航`

| 功能说明 | 对应文件名 |
| -----|:-----:|
|实时导航|GPSNaviActivity|
|模拟导航|EmulatorActivity|
|智能巡航|IntelligentBroadcastActivity|
|传入外部GPS数据导航|UseExtraGpsDataActivity|


`导航UI定制化`

| 功能说明 | 对应文件名 |
| -----|:-----:|
|自定义路段|CustomRouteActivity|
|自定义路线纹理|CustomRouteTextureInAMapNaviViewActivity|
|自定义路口放大图|CustomZoomInIntersectionViewActivity|
|自定义导航光柱|CustomTrafficBarViewActivity|
|自定义自车|CustomCarActivity|
|自定义全览按钮|OverviewModeActivity|
|自定义指南针|CustomDirectionViewActivity|
|自定义路况按钮|CustomTrafficButtonViewActivity|
|自定义放大缩小按钮|CustomZoomButtonViewActivity|
|自定义转向提示|CustomNextTurnTipViewActivity|
|正北模式|NorthModeActivity|


`HUD导航模式`

| 功能说明 | 对应文件名 |
| -----|:-----:|
|HUD导航|HudDisplayActivity|


`获取导航数据`

| 功能说明 | 对应文件名 |
| -----|:-----:|
|导航回调数据|NaviInfoActivity|
