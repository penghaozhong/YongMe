package com.kids21.sjb.app;

import android.graphics.Bitmap;

import com.kids21.activity.R;
import com.kids21.sjb.Kids21Application;

public interface ImageCache {
	public static Bitmap mDefaultBitmap = ImageManager
			.drawableToBitmap(Kids21Application.mContext.getResources()
					.getDrawable(R.drawable.common_icon_empty_user));

	public Bitmap get(String url);

	public void put(String url, Bitmap bitmap);
}
