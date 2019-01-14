package cn.netin.kidsbrowser;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient.FileChooserParams;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity2 extends Activity {

	private static final String TAG = "EL KidsBrowser" ;
	private UrlAdapter mGriddapter;
	private WebAccessData mData;
	private List<String[]> mBookmarkList ;
	private List<String> mWhiteList ;
	private WebView mWebView ;
	private GridView mGridView ;
	private EditText mUrlEdit ;
	private ImageButton mReloadButton ;
	private ImageButton mStopButton ;
	private ImageButton mForwardButton ;
	private ImageButton mBackwardButton ;
	private boolean mIsLoading = false ;
	private boolean mIsLimited = false ;


	//全屏显示
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

	//得到焦点时全屏显示
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		Log.d(TAG, "onWindowFocusChanged hasFocus " + hasFocus) ;
		if (hasFocus) {
			setFullScreen() ;
		}
	}


	//关闭输出法
	private void closeInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
			imm.hideSoftInputFromWindow(mUrlEdit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setFullScreen() ;
		setContentView(R.layout.activity_main);


		mData = new WebAccessData(this) ;
		//书签
		mBookmarkList = mData.getBookmarks() ;
		//白名单
		mWhiteList = mData.getUrls() ;
		//是否限制上网
		mIsLimited = mData.isEnable() ; 

		mReloadButton = (ImageButton) findViewById(R.id.reloadButton) ;
		//mStopButton = (ImageButton) findViewById(R.id.reloadButton) ;
		mForwardButton = (ImageButton) findViewById(R.id.forwardButton) ;
		mBackwardButton = (ImageButton) findViewById(R.id.backwardButton) ;
		mForwardButton.setVisibility(View.INVISIBLE);		
		mBackwardButton.setVisibility(View.INVISIBLE);		
		mUrlEdit = (EditText) findViewById(R.id.urlEdit) ;
		//实现按回车就提交浏览
		mUrlEdit.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				//Log.d(TAG, "onEditorAction") ;
				browse(v.getText().toString()) ;

				return false;
			}
		});

		mUrlEdit.setEnabled(!mIsLimited);

		//设置webview属性
		setupWebview() ;

		//显示书签
		mGridView = (GridView) findViewById(R.id.gridView) ;
		mGriddapter = new UrlAdapter(this) ;
		mGriddapter.setData(mBookmarkList);
		mGridView.setAdapter(mGriddapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String url = mBookmarkList.get(position)[1] ;
				browse(url) ;
			}
		});

		//处理intent
		handleIntent(getIntent()) ;

		/*
		String url = "http://www.netin.cn/i.htm" ;
		mWebView.loadUrl(url);
		mUrlEdit.setText(url);
		 */
	}

	//设置WebView相关属性
	private void setupWebview() {

		mWebView = (WebView) findViewById(R.id.webView) ;

		mWebView.setWebViewClient(new MyWebViewClient());

		//如果不用支持<input type="file">, 可以注释掉下面这行
		mWebView.setWebChromeClient(new MyWebChromeClient());

		mWebView.setVisibility(View.INVISIBLE);

		WebSettings webSettings = mWebView.getSettings();
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

	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(TAG, "onNewIntent");	
		handleIntent(intent);
	}

	//处理Intent
	@SuppressWarnings("deprecation")
	private void handleIntent(Intent intent) {
		Log.d(TAG, "handelIntent");
		String url = intent.getStringExtra("URL") ;
		if (url == null) {
			return ;
		}
		String params = intent.getStringExtra("PARAMS") ;
		Log.d(TAG, "PARAMS=" + params) ;
		if (params != null) {
			try {
				//如果带PARAMS参数，就用post方法提交登录
				mWebView.postUrl(url, params.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//mWebView.postUrl(url, EncodingUtils.getBytes(params, "BASE64"));  

		}else{
			//不带PARAMS，就访问URL
			mWebView.loadUrl(url);
		}
		mWebView.setVisibility(View.VISIBLE);
		mGridView.setVisibility(View.INVISIBLE);
		mUrlEdit.setText(url);
	}

	//定义浏览器的一些行为
	private class MyWebViewClient extends WebViewClient {

		//开始载入页面时，改变按钮状态
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			Log.d(TAG, "onPageStarted ") ;
			mReloadButton.setBackgroundResource(R.drawable.btn_stop);
			mIsLoading = true ;
			updateButtons() ;
		}

		//载入页面完成时，改变按钮状态
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			Log.d(TAG, "onPageFinished ") ;
			mReloadButton.setBackgroundResource(R.drawable.btn_reload);
			mIsLoading = false ;
			updateButtons() ;
		}


		/* Depricated
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);

			//Toast.makeText(MainActivity.this, description, Toast.LENGTH_LONG).show();
		}
		 */

		/*
		@Override
		public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
			super.onReceivedError(view, request, error);
			//Toast.makeText(MainActivity.this, description, Toast.LENGTH_LONG).show();

		}
		 */

		//控制访问网址
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			if (url != null && url.startsWith("genseet://")) {
				Log.d(TAG, "genseet") ;
				try {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(intent);
					return true;
				} catch (Exception e) { 
					return false;
				}
			} 

			do {
				//如果家长没有设定限制，忽略
				if (!mIsLimited) {
					break ;
				}
				//检查网址是否允许访问
				boolean ok = checkUrl(mWhiteList, url) ;
				if (ok) {
					break ;
				}
				Toast.makeText(MainActivity2.this, R.string.access_denied, Toast.LENGTH_SHORT).show() ;
				return true ;
			}while(false) ;

			mUrlEdit.setText(url);
			updateButtons() ;
			return false;

		}


	}

	//访问url,设置界面
	private void browse(String url) {
		closeInputMethod() ;
		mUrlEdit.clearFocus();
		setFullScreen() ;
		if (!url.toLowerCase(Locale.CHINA).startsWith("http")) {
			url = "http://" + url ;
		}
		mWebView.setVisibility(View.VISIBLE);
		mGridView.setVisibility(View.INVISIBLE);
		mUrlEdit.setText(url);
		mWebView.loadUrl(url);
		updateButtons() ;
	}

	//更新按钮状态
	private void updateButtons() {
		//Log.d(TAG, "updateButtons") ;
		//Log.d(TAG, "updateButtons mWebView.canGoBack()=" + mWebView.canGoBack()) ;
		//Log.d(TAG, "updateButtons mWebView.canGoForward()=" + mWebView.canGoForward()) ;
		if (mWebView.canGoBack()) {
			mBackwardButton.setVisibility(View.VISIBLE);
		}else{
			mBackwardButton.setVisibility(View.INVISIBLE);			
		}
		if (mWebView.canGoForward()) {
			mForwardButton.setVisibility(View.VISIBLE);
		}else{
			mForwardButton.setVisibility(View.INVISIBLE);			
		}		

	}

	//处理按钮点击事件
	public void onClick (View v) {

		int id = v.getId();
		if (id == R.id.homeButton) {
			mWebView.setVisibility(View.INVISIBLE);
			mGridView.setVisibility(View.VISIBLE);
		} else if (id == R.id.backwardButton) {
			mWebView.goBack();
		} else if (id == R.id.forwardButton) {
			mWebView.goForward();
		} else if (id == R.id.reloadButton) {
			if (mIsLoading) {
				Log.d(TAG, "reloadButton stopLoading") ;
				mWebView.stopLoading();
				mIsLoading = false ;
			}else{
				Log.d(TAG, "reloadButton browse " + mUrlEdit.getText().toString()) ;
				browse(mUrlEdit.getText().toString()) ;

			}
		} else if (id == R.id.closeButton) {
			this.finish();
		}
	}

	//Activity退出之前，让浏览器访问空白页，避免原来网页的声音继续播放之类
	@Override  
	protected void onPause(){  
		super.onPause();  
		mWebView.pauseTimers();  
		if(isFinishing()){  
			mWebView.loadUrl("about:blank");  
			//setContentView(new FrameLayout(this));  
		}  
	}  

	//Activity恢复时，读取白名单
	@Override  
	protected void onResume(){  
		super.onResume();  
		mWebView.resumeTimers();  
		mWhiteList = mData.getUrls() ;
	}  


	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWebView.destroy() ;
	}
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	 */

	//处理返回按键行为
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView.canGoBack()){
				mWebView.goBack();  
				return true;  
			}else{
				finish();
			}
		}

		return super.onKeyDown(keyCode, event);  
	}


	/***********************************************************************
	 * 以下三个函数用于检查URL是否允许访问
	 **********************************************************************/

	//判断是否匹配
	private static boolean match(String filter, String s){
		String ft = filter.replace("http://", "") ;
		ft = ft.replace("https://", "");
		//match *.abc.com
		int pos  ;
		if (ft.startsWith("*.")) {
			String trunk = ft.substring(2) ;	
			pos = s.indexOf('.') ;
			String s1 = s.substring(pos + 1) ;
			if (s1.equals(trunk)){
				return true ;
			}
		}else{ // match www.abc.com/*
			if (ft.endsWith("/*")){
				String filterDomain = ft.substring(0, ft.length() -2) ;
				if (filterDomain.equals(s)) {
					return true ;
				}
			}
		}

		return false ;
	}

	//提取域名
	private static String getDomain(String s) {
		String domain = s.replace("http://", "") ;
		domain = domain.replace("https://", "");
		int pos  = domain.indexOf('/') ;
		if (pos != -1) {
			return domain.substring(0, pos) ;
		}
		return domain ;
	}

	//检查URL是否允许访问
	public static boolean  checkUrl(List<String> filters, String s) {
		String domain = getDomain(s) ;

		for (String filter : filters) {

			if (filter.indexOf("*") != -1) {
				if (match(filter, domain)) {
					return true ;
				}else{
					Log.d(TAG, "domain=" + domain + " not match " + filter) ;
				}
			}else{
				if (domain.equals(filter)) {
					return true ;
				}else{
					Log.d(TAG, "domain=" + domain + " != " + filter) ;
				}
			}
		}

		return false ;
	}

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

	private class MyWebChromeClient extends WebChromeClient{

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
			if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
				// Create the File where the photo should go
				File photoFile = null;
				try {
					//设置MediaStore.EXTRA_OUTPUT路径,相机拍照写入的全路径
					photoFile = createImageFile();
					takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
				} catch (Exception ex) {
					// Error occurred while creating the File
					Log.e("WebViewSetting", "Unable to create Image File", ex);
				}

				// Continue only if the File was successfully created
				if (photoFile != null) {
					mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(photoFile));
					System.out.println(mCameraPhotoPath);
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
				System.out.println(takePictureIntent);
			} else {
				intentArray = new Intent[0];
			}

			Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
			chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
			chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

			startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

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
			MainActivity2.this.startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILECHOOSER_RESULTCODE);

		}

		// For Android 3.0+
		public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
			Log.d(TAG, "openFileChooser2");
			mUploadMessage = uploadMsg;
			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.addCategory(Intent.CATEGORY_OPENABLE);
			i.setType("image/*");
			MainActivity2.this.startActivityForResult(
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
			MainActivity2.this.startActivityForResult(Intent.createChooser(i, "Image Chooser"), MainActivity2.FILECHOOSER_RESULTCODE);

		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult");

		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage) return;
			Uri result = data == null || resultCode != RESULT_OK ? null
					: data.getData();
			if (result != null) {
				String imagePath = ImageFilePath.getPath(this, result);
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
						Logger.d("camera_photo_path", mCameraPhotoPath);
						results = new Uri[]{Uri.parse(mCameraPhotoPath)};
					}
				} else {
					String dataString = data.getDataString();
					Logger.d("camera_dataString", dataString);
					if (dataString != null) {
						results = new Uri[]{Uri.parse(dataString)};
					}
				}
			}

			mFilePathCallback.onReceiveValue(results);
			mFilePathCallback = null;
		} else {
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}
	}




}
