package com.kids21.sjb;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.kids21.http.HttpClient;
import com.kids21.sjb.app.LazyImageLoader;

public class Kids21Application extends Application {
	public static final String TAG = "Kids21Application";

	// public static ImageManager mImageManager;
	public static LazyImageLoader mImageLoader;

	public static Context mContext;
	public static SharedPreferences mPref;

	public static int networkType = 0;

	public final static boolean DEBUG = Configuration.getDebug();
	public static HttpClient http = null;

	@Override
	public void onCreate() {
		// FIXME: StrictMode类在1.6以下的版本中没有，会导致类加载失败。
		// 因此将这些代码设成关闭状态，仅在做性能调试时才打开。
		// //NOTE: StrictMode模式需要2.3+ API支持。
		// if (DEBUG){
		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// .detectAll()
		// .penaltyLog()
		// .build());
		// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		// .detectAll()
		// .penaltyLog()
		// .build());
		// }

		super.onCreate();

		mContext = this.getApplicationContext();
		// mImageManager = new ImageManager(this);
		mImageLoader = new LazyImageLoader();
		http = new HttpClient();

	}

	public String getNetworkType() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		// NetworkInfo mobNetInfo = connectivityManager
		// .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (activeNetInfo != null) {
			return activeNetInfo.getExtraInfo(); // 接入点名称: 此名称可被用户任意更改 如: cmwap,
													// cmnet,
													// internet ...
		} else {
			return null;
		}
	}

	@Override
	public void onTerminate() {
		// FIXME: 根据android文档，onTerminate不会在真实机器上被执行到
		// 因此这些清理动作需要再找合适的地方放置，以确保执行。
		cleanupImages();

		Toast.makeText(this, "exit app", Toast.LENGTH_LONG);

		super.onTerminate();
	}

	private void cleanupImages() {

		mImageLoader.getImageManager().clear();
	}

}
