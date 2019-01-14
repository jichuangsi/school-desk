package cn.netin.kidsbrowser;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


@SuppressWarnings("deprecation")
public class MyWebView extends WebView {
    private final static String TAG = "EL MyWebView";

    private ProgressBar mProgressBar;
    private Context mContext;
    private Activity mActivity ;
    
	/***************************************************************
	以下代码为支持 <inuput type="file"> ,调用摄像头及文件选择器。如果不需要支持，以下代码可删除
	 ***************************************************************/

	public static final int INPUT_FILE_REQUEST_CODE = 1;
	private ValueCallback<Uri> mUploadMessage;
	private final static int FILECHOOSER_RESULTCODE = 2;
	private ValueCallback<Uri[]> mFilePathCallback;

	private String mCameraPhotoPath;

	//在sdcard卡创建缩略图
	//createImageFileInSdcard
	@SuppressLint("SdCardPath")
	private File createImageFile() {
		//mCameraPhotoPath="/mnt/sdcard/tmp.png";
		File file=new File(Environment.getExternalStorageDirectory()+"/","browser_photo.jpg");
		mCameraPhotoPath=file.getAbsolutePath();
		if(!file.exists())
		{
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public void setActivity(Activity a) {
		mActivity = a ;
	}
    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT, 3, 0, 0));
        mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar));
        addView(mProgressBar);
        setWebChromeClient(new WebChromeClient());
        
        
        WebSettings webSettings = getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setPluginState(PluginState.ON);
		
		webSettings.setUserAgentString("Mozilla/5.0(Linux;Android 6.0) Chrome/43.0.2357.121");
		
		webSettings.setAllowFileAccess(true); // 允许访问文件
		webSettings.setAllowContentAccess(true);

		webSettings.setUseWideViewPort(true); 
		// 设置可以支持缩放   
		webSettings.setSupportZoom(true);   
		// 设置出现缩放工具   
		webSettings.setBuiltInZoomControls(true);  
		//扩大比例的缩放  
		webSettings.setUseWideViewPort(true);  
		//自适应屏幕  
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);  
		webSettings.setLoadWithOverviewMode(true);  
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //Log.d(TAG, "newProgress" + newProgress);
            if (newProgress == 100) {
                mProgressBar.setVisibility(GONE);
            } else {
                if (mProgressBar.getVisibility() == GONE)
                    mProgressBar.setVisibility(VISIBLE);
                mProgressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

        // 处理javascript中的console.log
        @Override
        public boolean onConsoleMessage(ConsoleMessage cm){
            android.util.Log.d(TAG, "webview console " + cm.lineNumber() + " of " + cm.sourceId() + " : " + cm.message());
            return true;
        }

        // 处理javascript中的alert()
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            result.cancel();
            return true;
        }
        
    	// android 5.0 这里需要使用android5.0 sdk
		public boolean onShowFileChooser(
				WebView webView, ValueCallback<Uri[]> filePathCallback,
				WebChromeClient.FileChooserParams fileChooserParams) {

			Log.d(TAG, "onShowFileChooser");
			if (mFilePathCallback != null) {
				mFilePathCallback.onReceiveValue(null);
			}


			mFilePathCallback = filePathCallback;

			/**
  	Open Declaration   String android.provider.MediaStore.ACTION_IMAGE_CAPTURE = "android.media.action.IMAGE_CAPTURE"
	Standard Intent action that can be sent to have the camera application capture an image and return it. 
	The caller may pass an extra EXTRA_OUTPUT to control where this image will be written. If the EXTRA_OUTPUT is not present, then a small sized image is returned as a Bitmap object in the extra field. This is useful for applications that only need a small image. If the EXTRA_OUTPUT is present, then the full-sized image will be written to the Uri value of EXTRA_OUTPUT. As of android.os.Build.VERSION_CODES.LOLLIPOP, this uri can also be supplied through android.content.Intent.setClipData(ClipData). If using this approach, you still must supply the uri through the EXTRA_OUTPUT field for compatibility with old applications. If you don't set a ClipData, it will be copied there for you when calling Context.startActivity(Intent).
	See Also:EXTRA_OUTPUT
	标准意图，被发送到相机应用程序捕获一个图像，并返回它。通过一个额外的extra_output控制这个图像将被写入。如果extra_output是不存在的，
	那么一个小尺寸的图像作为位图对象返回。如果extra_output是存在的，那么全尺寸的图像将被写入extra_output URI值。
			 */
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
				// Create the File where the photo should go
				File photoFile = null;
				try {
					//设置MediaStore.EXTRA_OUTPUT路径,相机拍照写入的全路径
					photoFile = createImageFile();
					takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
				} catch (Exception ex) {
					// Error occurred while creating the File
					Log.e(TAG, "Unable to create Image File", ex);
				}

				// Continue only if the File was successfully created
				if (photoFile != null) {
					mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(photoFile));
					Log.d(TAG, mCameraPhotoPath);
				} else {
					takePictureIntent = null;
				}
			}

			Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
			contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
			contentSelectionIntent.setType("image/*");

			Intent[] intentArray;
			if (takePictureIntent != null) {
				intentArray = new Intent[]{takePictureIntent};
			} else {
				intentArray = new Intent[0];
			}

			Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
			chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
			chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

			mActivity.startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

			return true;
		}



		//The undocumented magic method override
		//Eclipse will swear at you if you try to put @Override here
		// For Android 3.0+
		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
			Log.d(TAG, "openFileChooser1");

			mUploadMessage = uploadMsg;
			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.addCategory(Intent.CATEGORY_OPENABLE);
			i.setType("image/*");
			mActivity.startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILECHOOSER_RESULTCODE);

		}

		// For Android 3.0+
		public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
			Log.d(TAG, "openFileChooser2");
			mUploadMessage = uploadMsg;
			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.addCategory(Intent.CATEGORY_OPENABLE);
			i.setType("image/*");
			mActivity.startActivityForResult(
					Intent.createChooser(i, "Image Chooser"),
					FILECHOOSER_RESULTCODE);
		}

		//For Android 4.1
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
			Log.d(TAG, "openFileChooser3");

			mUploadMessage = uploadMsg;
			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.addCategory(Intent.CATEGORY_OPENABLE);
			i.setType("image/*");
			mActivity.startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILECHOOSER_RESULTCODE);

		}

    }
    
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		//Log.d(TAG, "onActivityResult");

		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage) return false ;
			Uri result = data == null || resultCode != Activity.RESULT_OK ? null
					: data.getData();
			if (result != null) {
				String imagePath = ImageFilePath.getPath(mContext, result);
				if (!TextUtils.isEmpty(imagePath)) {
					result = Uri.parse("file:///" + imagePath);
				}
			}
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		} else if (requestCode == INPUT_FILE_REQUEST_CODE && mFilePathCallback != null) {
			// 5.0的回调
			Uri[] results = null;

			// Check that the response is a good one
			if (resultCode == Activity.RESULT_OK) {
				if (data == null) {
					// If there is not data, then we may have taken a photo
					if (mCameraPhotoPath != null) {
						Log.d(TAG, mCameraPhotoPath);
						results = new Uri[]{Uri.parse(mCameraPhotoPath)};
					}
				} else {
					String dataString = data.getDataString();
					Log.d(TAG, dataString);
					if (dataString != null) {
						results = new Uri[]{Uri.parse(dataString)};
					}
				}
			}

			mFilePathCallback.onReceiveValue(results);
			mFilePathCallback = null;
		} else {
			return false ;
		}
		return true ;
	}

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) mProgressBar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        mProgressBar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }
}