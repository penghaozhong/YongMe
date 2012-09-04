package com.kids21.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.kids21.http.HttpClient;
import com.kids21.http.HttpException;
import com.kids21.http.Response;
import com.kids21.http.ResponseException;
import com.kids21.sjb.Configuration;
import com.kids21.sjb.Kids21Application;
import com.kids21.sjb.app.ImageManager;
import com.kids21.sjb.task.GenericTask;
import com.kids21.sjb.task.TaskAdapter;
import com.kids21.sjb.task.TaskListener;
import com.kids21.sjb.task.TaskParams;
import com.kids21.sjb.task.TaskResult;
import com.kids21.util.FileHelper;

public class UserAvatarActivity extends Activity {
	
	
	// FIXME: for debug, delete me
	private long startTime = -1;
	private long endTime = -1;

	private TextView txTake = null;
	private TextView txAlbum = null;
	private boolean withPic = false;
	private File mFile;

	// 图片路径
	private File mImageFile;
	private Uri mImageUri;
	private ProgressDialog dialog;

	private static final String TAG = "UserAvatarActivity";
	private static final int REQUEST_IMAGE_CAPTURE = 2;
	private static final int REQUEST_PHOTO_LIBRARY = 3;
	private static final int MAX_BITMAP_SIZE = 400;
	
	
	
	// Task
	private GenericTask mSendTask;

	private TaskListener mSendTaskListener = new TaskAdapter() {
		@Override
		public void onPreExecute(GenericTask task) {
			onSendBegin();
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			endTime = System.currentTimeMillis();
			Log.d("LDS", "Sended a status in " + (endTime - startTime));

			if (result == TaskResult.OK) {
				onSendSuccess();
			} else if (result == TaskResult.IO_ERROR) {
				onSendFailure();
			}
		}

		private void onSendFailure() {
			if (dialog != null){
				dialog.setMessage(getString(R.string.page_status_unable_to_update));
				dialog.dismiss();
			}
			
		}
		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return "SendTask";
		}
	};
	
	private void onSendBegin() {
		
		dialog = ProgressDialog.show(UserAvatarActivity.this, "",
				getString(R.string.page_status_updating), true);
		if (dialog != null) {
			dialog.setCancelable(false);
		}
		
	}
	
	private void onSendSuccess() {
		if (dialog != null) {
			dialog.setMessage(getString(R.string.page_status_update_success));
			
			
			Intent intentUser = new Intent();
			intentUser.setAction("intentToTakePhoto");
			intentUser.putExtra(UserActivity.AVATAR_PIC, createThumbnailBitmap(mImageUri,
					MAX_BITMAP_SIZE));
			sendBroadcast(intentUser);
			
			dialog.dismiss();
			this.finish();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_avatar);

		// 调用相机
		txTake = (TextView) findViewById(R.id.textView_avatar_take);
		txTake.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				openImageCaptureMenu();

			}
		});
		
		
		// 图片库
		txAlbum = (TextView) findViewById(R.id.textView_avatar_album);
		txAlbum.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						openPhotoLibraryMenu();

					}
				});
		
		

	}

	private String _getPhotoFilename(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddKms");
		return dateFormat.format(date) + ".jpg";
	}

	protected void openImageCaptureMenu() {
		try {
			// TODO: API < 1.6, images size too small
			String filename = _getPhotoFilename(new Date());
			Log.d(TAG, "Photo filename=" + filename);
			mImageFile = new File(FileHelper.getBasePath(), filename);
			mImageUri = Uri.fromFile(mImageFile);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
			startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * 调用图片库
	 */
	protected void openPhotoLibraryMenu() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_PHOTO_LIBRARY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Intent intent = UserAvatarActivity.createImageIntent(this,
					mImageUri);

			// 照相完后不重新起一个WriteActivity
			getPic(intent, mImageUri);
			/*
			 * intent.setClass(this, WriteActivity.class);
			 * 
			 * startActivity(intent);
			 * 
			 * // 打开发送图片界面后将自身关闭 finish();
			 */
		} else if (requestCode == REQUEST_PHOTO_LIBRARY
				&& resultCode == RESULT_OK) {
			mImageUri = data.getData();

			Intent intent = UserAvatarActivity.createImageIntent(this,
					mImageUri);

			// 选图片后不重新起一个WriteActivity
			getPic(intent, mImageUri);
			/*
			 * intent.setClass(this, WriteActivity.class);
			 * 
			 * startActivity(intent);
			 * 
			 * // 打开发送图片界面后将自身关闭 finish();
			 */
		}

	}

	public static Intent createImageIntent(Activity activity, Uri uri) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		try {
			UserAvatarActivity writeActivity = (UserAvatarActivity) activity;
			/*
			 * intent.putExtra(Intent.EXTRA_TEXT,
			 * writeActivity.mTweetEdit.getText());
			 * intent.putExtra(WriteActivity.EXTRA_REPLY_TO_NAME,
			 * writeActivity._reply_to_name);
			 * intent.putExtra(WriteActivity.EXTRA_REPLY_ID,
			 * writeActivity._reply_id);
			 * intent.putExtra(WriteActivity.EXTRA_REPOST_ID,
			 * writeActivity._repost_id);
			 */
		} catch (ClassCastException e) {
			// do nothing
		}
		return intent;

	}

	private void getPic(Intent intent, Uri uri) {

		// layout for picture mode
		// changeStyleWithPic();

		withPic = true;
		mFile = null;

		mImageUri = uri;
		if (uri.getScheme().equals("content")) {
			mFile = new File(getRealPathFromURI(mImageUri));
		} else {
			mFile = new File(mImageUri.getPath());
		}

		doSend();
		

		if (mFile == null) {
			// updateProgress("Could not locate picture file. Sorry!");
			// disableEntry();
		}
		
		//finish();

	}

	private String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaColumns.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	/**
	 * 制作微缩图
	 * 
	 * @param uri
	 * @param size
	 * @return
	 */
	private Bitmap createThumbnailBitmap(Uri uri, int size) {
		InputStream input = null;

		try {
			input = getContentResolver().openInputStream(uri);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(input, null, options);
			input.close();

			// Compute the scale.
			int scale = 1;
			while ((options.outWidth / scale > size)
					|| (options.outHeight / scale > size)) {
				scale *= 2;
			}

			options.inJustDecodeBounds = false;
			options.inSampleSize = scale;

			input = getContentResolver().openInputStream(uri);

			return BitmapFactory.decodeStream(input, null, options);
		} catch (IOException e) {
			Log.w(TAG, e);

			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					Log.w(TAG, e);
				}
			}
		}
	}
	
	private void doSend() {
		Log.d(TAG, "dosend  " + withPic);
		startTime = System.currentTimeMillis();

		if (mSendTask != null
				&& mSendTask.getStatus() == GenericTask.Status.RUNNING) {
			return;
		} else {
		//	String status = mTweetEdit.getText().toString();

			
				int mode=SendTask.TYPE_NORMAL;
				if (withPic) {
					mode = SendTask.TYPE_PHOTO;
				} 

				mSendTask = new SendTask();
				mSendTask.setListener(mSendTaskListener);

				TaskParams params = new TaskParams();
				params.put("mode", mode);
				mSendTask.execute(params);
			
		}
	}
	
	/*
	 * 上传图片方法
	 * post 方式参数参考
	 * http.httpRequest(
	   baseURL + "index.php?m=upfile&c=up",
	   createParams(new BasicNameValuePair("status", status),
	   new BasicNameValuePair("source", source)), mFile, true,
	   HttpPost.METHOD_NAME);
	 */
	private class SendTask extends GenericTask {

		public static final int TYPE_NORMAL = 0;
		public static final int TYPE_REPLY = 1;
		public static final int TYPE_REPOST = 2;
		public static final int TYPE_PHOTO = 3;

		@Override
		protected TaskResult _doInBackground(TaskParams... params) {
			TaskParams param = params[0];
			try {
				int mode = param.getInt("mode");

				// Send status in different way
				switch (mode) {

				case TYPE_REPLY:
					break;

				case TYPE_REPOST:
					break;

				case TYPE_PHOTO:
					if (null != mFile) {
						// Compress image
						try {
							mFile = getImageManager().compressImage(mFile, 100);
							// ImageManager.DEFAULT_COMPRESS_QUALITY);
						} catch (IOException ioe) {
							Log.e(TAG, "Cann't compress images.");
						}
						
						
						
								Response ress = Kids21Application.http.httpRequest(
								HttpClient.baseURL + "index.php?m=upfile&c=up",null, mFile, true,
								HttpPost.METHOD_NAME);
								
								// 解析json 显示结果
								if(showResult(ress)==1){
									return TaskResult.OK;
								}
								
					} else {
						Log.e(TAG,
								"Cann't send status in PICTURE mode, photo is null");
					}
					break;

				case TYPE_NORMAL:
				default:
					break;
				}
			} catch (HttpException e) {
				Log.e(TAG, e.getMessage(), e);

				if (e.getStatusCode() == HttpClient.NOT_AUTHORIZED) {
					return TaskResult.AUTH_ERROR;
				}
				return TaskResult.IO_ERROR;
			}

			return TaskResult.FAILED;
		}

		private ImageManager getImageManager() {
			return Kids21Application.mImageLoader.getImageManager();
		}
	}
	
	/**
	 * 生成POST Parameters助手
	 * 
	 * @param nameValuePair
	 *            参数(一个或多个)
	 * @return post parameters
	 */
	public ArrayList<BasicNameValuePair> createParams(
			BasicNameValuePair... nameValuePair) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		for (BasicNameValuePair param : nameValuePair) {
			params.add(param);
		}
		return params;
	}
	
	/**
	 * 返回结果
	 * @param res
	 * @return 1：成功  0：失败
	 */
	public int showResult(Response res){
		int re=0;
		if(res!=null){
			try {
				JSONObject json=res.asJSONObject();
				int resu=json.getInt("message");
				if(resu==1){
					// sucuss
					re=1;
				}else{
					//fail
				}
			} catch (ResponseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return re;
	}

}
