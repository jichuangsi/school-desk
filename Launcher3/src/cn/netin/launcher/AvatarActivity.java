package cn.netin.launcher;



import java.util.ArrayList;

import com.hanceedu.common.HanceApplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;
import cn.netin.launcher.data.Constants;


public class AvatarActivity extends Activity  implements AdapterView.OnItemSelectedListener,
OnClickListener {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100 ;
	private static final int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 101 ;
	private static final String TAG = "HANCE";
	private Gallery mGallery;
	private ImageView mAvatarView;
	private EditText mNameEdit ;
	private ArrayList<Integer> mAvatars = null;
	private HanceApplication mApp;
	private Uri mPhotoUri ;
	private int mSelectedIndex = -1 ;


	private class ImageAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		ImageAdapter(Context context) {
			mLayoutInflater = ((Activity) context).getLayoutInflater();
		}

		public int getCount() {
			if (mAvatars == null) {
				return 0 ;
			}
			return mAvatars.size() + 2;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView image;

			
			if (convertView == null) {
				image = (ImageView) mLayoutInflater.inflate(R.layout.avatar_item, parent, false);
			} else {
				image = (ImageView) convertView;
			}
			if (position == 0) {
				image.setImageResource(R.drawable.logo);
			}
			else if (position == 1) {
				image.setImageBitmap(Avatar.getDefault(AvatarActivity.this, false));
			}
			
			else{
				int avatarRes = mAvatars.get(position - 2);
				image.setImageResource(avatarRes);
			}


			Drawable thumbDrawable = image.getDrawable();
			if (thumbDrawable != null) {
				thumbDrawable.setDither(true);
			} else {
				Log.e(TAG, String.format(
						"Error decoding avatar #%d", position));
			}
			return image;
		}
	}
	

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void setFullScreen() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}else{			
			getWindow().getDecorView().setSystemUiVisibility(8); // 4.0全屏设置,仅特制系统可用
		}
	} 

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.avatar) ;
		//setFullScreen();

		mApp = ((HanceApplication)getApplicationContext()) ;

		findAvatars();
		mGallery = (Gallery) findViewById(R.id.gallery);
		mGallery.setAdapter(new ImageAdapter(this));
		mGallery.setOnItemSelectedListener(this);
		mGallery.setCallbackDuringFling(false);

		mAvatarView = (ImageView) findViewById(R.id.avatarView);
		mNameEdit = (EditText) findViewById(R.id.nameEdit);
		mNameEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(24)});
		
		String name = Avatar.getName(this) ;
		mNameEdit.setText(name);

	}

	private void findAvatars() {
		final int COUNT = Constants.AVATAR_COUNT ;
		String[] names = new String[COUNT] ; 	
		mAvatars = new ArrayList<Integer>(COUNT);
		final Resources resources = getResources();
		final String packageName = getApplication().getPackageName();

		for (int i = 0 ; i < COUNT ; i++) {
			names[i] = "avatar_" + i ;
		}
		final String defType = "drawable" ;
		for (String name : names) {
			final int resId = resources.getIdentifier(name, defType, packageName);
			//Log.d(TAG, "resId=" + resId) ;
			if (resId != 0) {
				mAvatars.add(resId);
			}
		}
	}

	private void saveAvatar() {
		Log.d(TAG, "saveAvatar mSelectedIndex=" + mSelectedIndex) ;
		if (mSelectedIndex > 0) {
			Avatar.saveDefault(this.getApplicationContext(), (BitmapDrawable) mAvatarView.getDrawable()) ;
		}
		Avatar.saveName(this, mNameEdit.getText().toString(), (mSelectedIndex == 0) );
		int flag = 0 ;
		if (mSelectedIndex == 0) {
			flag = 1 ;
		}
		mApp.sendMessage(Constants.MSG_AVATAR_SET, flag, 0);
	}

	private void startCamera() {
		// create Intent to take a picture and return control to the calling application
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		mPhotoUri = Avatar.getOutputMediaFileUri(Avatar.MEDIA_TYPE_IMAGE); // create a file to save the image
		//Log.i(TAG, "fileUri:" + mPhotoUri.toString()) ;
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri); // set the image file name
		intent.putExtra("return-data", true); 
		// start the image capture Intent
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

	}

	private void startAlbum() {
		Intent i = new Intent(
				Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(i, PICK_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				if (data == null) {
					Log.e(TAG, "onActivityResult data == null") ;
				}else{
					// Image captured and saved to fileUri specified in the Intent
					Toast.makeText(this, "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
				}
				String path = mPhotoUri.toString().replace("file://", "") ;
				Bitmap bitmap = Avatar.getAvatar(path, this) ;
				if (bitmap == null) {
					Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_LONG).show();
					return  ;
				}
				mAvatarView.setImageBitmap(bitmap);
				mSelectedIndex = 1 ;

			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_LONG).show();
			}
		}
		

		else if (requestCode == PICK_IMAGE_ACTIVITY_REQUEST_CODE ) {
			
			if (resultCode == RESULT_OK) {
				if (data == null) {
					Log.e(TAG, "onActivityResult data == null") ;
					return ;
				}
				Uri uri = data.getData();
				//Log.d(TAG, "onActivityResult uri " + uri) ;
				String[] columns = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(uri,
						columns, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(columns[0]);
				String path = cursor.getString(columnIndex);
				cursor.close();
				//Log.d(TAG, "onActivityResult path " + path) ;
				Bitmap bitmap = Avatar.getAvatar(path, this) ;
				if (bitmap == null) {
					Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_LONG).show();
					return  ;
				}
				mAvatarView.setImageBitmap(bitmap);
				mSelectedIndex = 1 ;

			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_LONG).show();
			}	
			


			// String picturePath contains the path of selected Image
		}

	}

	public void handleClick(View source) {
		//Log.i(TAG, "handleClick: " + source.getId()) ;
		switch(source.getId()) {
		case R.id.closeButton:

			saveAvatar() ;
			this.finish();
			break ;

		case R.id.cameraButton:
			startCamera() ;
			break ;
			
		case R.id.albumButton :
			startAlbum() ;
			break ;

		default:

			break ;

		}

	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onItemSelected(AdapterView<?> adaperView, View v, int position,
			long id) {
		ImageView imageView = (ImageView) v ;
		BitmapDrawable drawable = (BitmapDrawable)imageView.getDrawable() ;
		Log.d(TAG, "onItemSelected mSelectedIndex=" + position) ;
		mSelectedIndex = position ;
		
		if (position < 1) {
			mAvatarView.setImageDrawable(drawable);
		}else{
			mAvatarView.setImageBitmap(Avatar.getAvatar(drawable, this));
		}

	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onNothingSelected") ;

	}



}
