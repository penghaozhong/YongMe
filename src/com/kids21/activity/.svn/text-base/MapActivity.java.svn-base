
/** 
* @author pengluzhong
* E-mail: penghaozhong@163.com
* @version 创建时间：9 Aug 2012 15:31:09 
* 类说明 
*/ 

package com.kids21.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;

public class MapActivity extends com.baidu.mapapi.MapActivity {
	
	private LocationClient mLocationClient = null;
	BMapManager mBMapMan=null;
	MKLocationManager mLocationManager=null;
	MapView mMapView =null;
	private Message msg=null;
	GeoPoint point = null;
	MapController mMapController = null;
    LocationClientOption option =null;
    
    private MKSearch mMKSearch;
	
	public static String BAITU_KEY = "5FB2ADB0EBE03CB6F039BC4B709955DE3D542953";
	
	// 定位成都中心
	private static final int latitudeE6 = (int) (30.660036 * 1000000);
	private static final int longitudeE6 = (int) (104.06526 * 1000000);
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init(BAITU_KEY, null);
		super.initMapActivity(mBMapMan);
		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapView.setBuiltInZoomControls(true);
		 mMapController = mMapView.getController();
		 point = new GeoPoint(latitudeE6,longitudeE6);  
			mMapController.setCenter(point);  //设置地图中心点
			mMapController.setZoom(20);
	
		
		
		
		
		
		// 启用线程查找当前位置
		new Thread() {
			@Override
			public void run() {
					msg = handler.obtainMessage();
					msg.arg1=1;
					handler.sendMessage(msg);
				
			};
		}.start();
		
		 
		/*mMKSearch = new MKSearch();
		mMKSearch.init(mBMapMan, new MySearchListener(this,mMapView));//注意，MKSearchListener只支持一个，以最后一次设置为准
		 mMKSearch.poiSearchNearBy("中餐", new GeoPoint(latitudeE6, longitudeE6), 5000);
		*/
		
		// 位置图标

		
		
	}
 
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected void onDestroy() {
	    if (mBMapMan != null) {
	        mBMapMan.destroy();
	        mBMapMan = null;
	    }
	    if (mLocationClient != null && mLocationClient.isStarted()){
    		mLocationClient.stop();
    		mLocationClient = null;
    	}
	    super.onDestroy();
	}
	@Override
	protected void onPause() {
	    if (mBMapMan != null) {
	        mBMapMan.stop();
	    }
	    super.onPause();
	}
	@Override
	protected void onResume() {
	    if (mBMapMan != null) {
	        mBMapMan.start();
	    }
	    super.onResume();
	}
	
	

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			try {
					if (msg.arg1 == 1) {
						mLocationClient = new LocationClient(MapActivity.this);
				         option = new LocationClientOption();
				        option.setOpenGps(true);								//打开gps
				        option.setCoorType("bd09ll");							//设置坐标类型为bd09ll
				        option.setPriority(LocationClientOption.NetWorkFirst);	//设置网络优先
				        option.setProdName("YANGGRIL");						//设置产品线名称
				        option.setScanSpan(5000);								//定时定位，每隔5秒钟定位一次。
				        mLocationClient.setLocOption(option);
				        mLocationClient.registerLocationListener(new BDLocationListener() {
							
							public void onReceiveLocation(BDLocation location) {
								if (location == null){
									Toast.makeText(MapActivity.this,
											"还没获取到您的位置！请稍候在试", Toast.LENGTH_LONG).show();
									return ;
								}
								 mMapController = mMapView.getController();  // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
								
								int lotitude=(int) (location.getLatitude() * 1E6);
								int longitude=  (int) (location.getLongitude() * 1E6);
								 point = new GeoPoint(lotitude, longitude );  
								mMapController.setCenter(point);  //设置地图中心点
								
								Drawable marker = getResources().getDrawable(R.drawable.default_cat_icon);  //得到需要标在地图上的资源
								mMapView.getOverlays().add(new OverItemT(marker,MapActivity.this,lotitude,longitude)); //添加ItemizedOverlay实例到mMapView
								/*StringBuffer sb = new StringBuffer(256);
								sb.append("time : ");
								sb.append(location.getTime());
								sb.append("\nerror code : ");
								sb.append(location.getLocType());
								sb.append("\nlatitude : ");
								sb.append(location.getLatitude());
								sb.append("\nlontitude : ");
								sb.append(location.getLongitude());
								sb.append("\nradius : ");
								sb.append(location.getRadius());
								if (location.getLocType() == BDLocation.TypeGpsLocation){
									sb.append("\nspeed : ");
									sb.append(location.getSpeed());
									sb.append("\nsatellite : ");
									sb.append(location.getSatelliteNumber());
								} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
									sb.append("\naddr : ");
									sb.append(location.getAddrStr());
								}
								sb.append("\nsdk version : ");
								sb.append(mLocationClient.getVersion());*/
								
							}
							
					        public void onReceivePoi(BDLocation location){
					        	//return ;
					        }
						});
						
				        mLocationClient.start();
						
					} 
			} catch (Exception e) {
				e.printStackTrace();
			}
		

		};
	};
}
