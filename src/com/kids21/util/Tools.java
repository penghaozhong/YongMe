package com.kids21.util;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;


/** 
 * @author pengluzhong
 * E-mail: penghaozhong@163.com
 * @version 创建时间：13 Aug 2012 14:13:14 
 * 类说明 
 */

public class Tools {
	public static int a(Activity paramActivity)
	  {
	    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
	    paramActivity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
	    return localDisplayMetrics.widthPixels;
	  }

	  public static final int a(Context paramContext, int paramInt)
	  {
	    return (int)(0.5F + paramContext.getResources().getDisplayMetrics().density * paramInt);
	  }
}
