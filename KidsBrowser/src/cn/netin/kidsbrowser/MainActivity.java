package cn.netin.kidsbrowser;


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = "EL KidsBrowser" ;
	private UrlAdapter mGriddapter;
	private WebAccessData mData;
	private List<String[]> mBookmarkList ;
	private List<String> mWhiteList ;
	private MyWebView mWebView ;
	private GridView mGridView ;
	private EditText mUrlEdit ;
	private ImageButton mReloadButton ;
	private ImageButton mStopButton ;
	private ImageButton mForwardButton ;
	private ImageButton mBackwardButton ;
	private boolean mIsLoading = false ;
	private boolean mIsLimited = false ;
	private boolean mKeyboardShown = false ;


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
		//Log.d(TAG, "onWindowFocusChanged hasFocus " + hasFocus) ;
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


		AndroidBug5497Workaround.assistActivity(this);
		final FrameLayout activityRootView = (FrameLayout) findViewById(R.id.FrameLayout1);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
				if (heightDiff > dpToPx(MainActivity.this, 150)) { // if more than 200 dp, it's probably a keyboard...
					Log.e(TAG, "kb show") ;
					mKeyboardShown = true ;
				}else {
					if (mKeyboardShown){
						Log.e(TAG, "kb hide") ;
						mKeyboardShown = false ;
						setFullScreen() ;
					}
				}
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

	public static float dpToPx(Context context, float valueInDp) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
	}

	//设置WebView相关属性
	private void setupWebview() {

		mWebView = (MyWebView) findViewById(R.id.webView) ;
		mWebView.setActivity(this);
		mWebView.setWebViewClient(new MyWebViewClient());
		mWebView.setVisibility(View.INVISIBLE);

		//mWebView.setDownloadListener(new HttpClientDownloadListener(this));
		mWebView.setDownloadListener(new DownloadServiceDownloadListener(this));

	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(TAG, "onNewIntent");	
		handleIntent(intent);
	}

	//处理Intent
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
			//Log.d(TAG, "onPageStarted ") ;
			mReloadButton.setBackgroundResource(R.drawable.btn_stop);
			mIsLoading = true ;
			updateButtons() ;
		}

		//载入页面完成时，改变按钮状态
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			//Log.d(TAG, "onPageFinished ") ;
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
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
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
				Toast.makeText(MainActivity.this, R.string.access_denied, Toast.LENGTH_SHORT).show() ;
				return true ;
			}while(false) ;

			mUrlEdit.setText(url);
			updateButtons() ;
			/* not work
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Accept-Language", "zh-CN,zh");
			view.loadUrl(url, headers);
			return true ;
			 */
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
		ViewGroup view = (ViewGroup) getWindow().getDecorView();
	    view.removeAllViews();
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



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult");
		if (!mWebView.onActivityResult( requestCode,  resultCode,  data)){
			super.onActivityResult(requestCode, resultCode, data);
		}
	}


}
