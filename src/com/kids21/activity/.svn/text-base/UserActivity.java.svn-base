package com.kids21.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kids21.view.ItemView;

public class UserActivity extends Activity {
	private Button bnBack = null;
	private ItemView avatar = null;
	private BroadcastReceiver setTakePhotoreceiver;
	IntentFilter takePhotoFilter;

	public static String AVATAR_PIC = "avatar_pic";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);

		// 标题
		TextView textview = (TextView) findViewById(R.id.title_text);
		textview.setText(R.string.user_label_title);
		textview.setVisibility(View.VISIBLE);

		// 返回事件
		bnBack = (Button) findViewById(R.id.title_left);
		bnBack.setOnClickListener(new ImageButton.OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});

		// 初始化上传图片接收器
		initTakePhotoBroadCast();

		// 修改头像
		avatar = (ItemView) findViewById(R.id.user_avatar);
		avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(UserActivity.this,
						UserAvatarActivity.class);
				startActivity(intent);

			}
		});

	}

	// 上传图片广播
	public void initTakePhotoBroadCast() {
		setTakePhotoreceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// 图片更新
				Bitmap bitmap = (Bitmap) intent.getParcelableExtra(AVATAR_PIC);
				// 将图片显示在ImageView里
				if (bitmap != null) {
					((ImageView) findViewById(R.id.image))
							.setImageBitmap(bitmap);

				}else{
					// 图片错误
					((TextView) findViewById(R.id.desc)).setText(R.string.page_status_error);
					
				}
						

			}
		};
		takePhotoFilter = new IntentFilter("intentToTakePhoto");
		registerReceiver(setTakePhotoreceiver, takePhotoFilter);
	}

}
