package com.hanceedu.common.widget;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.xml.sax.XMLReader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Html.TagHandler;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.hanceedu.common.R;
import com.hanceedu.common.data.HanceData;

/**
 * 用于显示html文字<br>
 * 将html装换为CharSequence 对象时使用了AsyncTask异步处理
 * @author long.wu
 *
 */
public class CTextView extends TextView {

	private static final String TAG = "CEditText";
	private Context mContext;
	/** 设置显示的编码 */
	private String mEncoding = "GBK";
	/** 网页文件路径 */
	private String mFilePath = "";
	private String mDataTitle = "";
	private int mImgCate ;
	//private HtmlFormTask mTask  ;
	private boolean mIsIdle = true ;

	private String mBasePath ;

	public CTextView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}


	public CTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public CTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public void init() {
		//this.setMovementMethod(ScrollingMovementMethod.getInstance());

	}
	public boolean isIdle() {
		return mIsIdle ;
	}

	public void setContent(String html, String title, String basePath) {
		this.scrollTo(0, 0) ;
		mDataTitle = title;
		mImgCate = -1 ;
		mBasePath = basePath ;
		mIsIdle = false ;

		this.mDataTitle = title;

		HtmlFormTask mTask = new HtmlFormTask(mContext);
		mTask.setTitle( title) ;
		mTask.execute(html);

	}

	public void setContent(String html, String title, int imgCate) {
		this.scrollTo(0, 0) ;
		mDataTitle = title;
		mImgCate = imgCate ;
		mIsIdle = false ;
		this.mDataTitle = title;
		HtmlFormTask mTask = new HtmlFormTask(mContext);
		mTask.setTitle( title) ;
		mTask.execute(html);

	}

	public void setContent(int cate, int id, int imgCate, String title)
	{
		this.scrollTo(0, 0) ;
		mDataTitle = title;
		mImgCate = imgCate ;
		byte[] b;
		b = HanceData.get(cate, id);//获取加密数据
		String contentHtml = "" ;
		if (b != null) {
			try {
				contentHtml = new String(b, "GBK");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				mIsIdle = true ;
				return ;
			}
		}else{
			System.out.println("CTextView get data = null");
			mIsIdle = true ;
			return ;
		}
		this.mDataTitle = title;

		//System.out.println("contentHtml: " + contentHtml);
		// 异步加载
		HtmlFormTask mTask = new HtmlFormTask(mContext);
		mTask.setTitle( title) ;
		mTask.execute(contentHtml);

	}


	/**
	 * 读取网页的内容并返回
	 * 
	 * @return 网页内容
	 */
	private String readHtml(File file) throws Exception {
		String content = null;
		InputStream in = new FileInputStream(file);
		int byteSize = in.available();
		byte[] buf = new byte[byteSize];
		in.read(buf);
		in.close();

		return new String(buf, mEncoding);


	}

	/**
	 * 图片处理
	 */
	private ImageGetter imgGetter = new Html.ImageGetter() {
		@Override
		public Drawable getDrawable(String source) {
			Drawable drawable = null;
			try {
				InputStream in = null;
				if (mImgCate == -1) {
					File f =  new File(mBasePath + source) ;
					System.out.println("ctextview img path=" + mBasePath + source);
					if (f.exists()){
						in = new FileInputStream(f) ;
					}

				}else{
					byte[]  binary = HanceData.get(mImgCate,
							Integer.parseInt(source.substring(0, source.lastIndexOf('.'))));

					in = new ByteArrayInputStream(binary);
				}
				if (in != null) {
					drawable = Drawable.createFromStream(in, source);
					if (drawable == null) {
						Log.e(TAG, "drawable为空");
					}else{
						drawable.setBounds(0, 0, (int) (drawable.getMinimumWidth() ), (int)(drawable.getMinimumHeight() ));
						return drawable;
					}
				}

			} catch (Exception e) {
				Log.e(TAG, "图片出错");
				e.printStackTrace();

			}

			drawable = CTextView.this.mContext.getResources().getDrawable(R.drawable.transparent);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
			return drawable;

		}
	};

	TagHandler tagHandler = new TagHandler() {
		@Override
		public void handleTag(boolean opening, String tag, Editable output,
				XMLReader xmlReader) {
			// 如果获取到的是图片标签，提取出里面的宽度跟高度
			if (tag.equals("title")) {
				try {
					output.clear();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	class HtmlFormTask extends AsyncTask<String, Integer, String> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		private Spanned mContenHtml;
		private String mDataTitle = "";

		public HtmlFormTask(Context context) {

		}

		public void setTitle(String dataTitle) {
			this.mDataTitle = dataTitle;
		}

		@Override
		protected String doInBackground(String... params) {
			mContenHtml = Html.fromHtml(params[0], imgGetter, tagHandler);
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			System.out.println("CTextView onCancelled") ;
			mIsIdle = true ;
		}

		@Override
		protected void onPostExecute(String result) {
			// 返回HTML页面的内容
			//if (mDataTitle.equals(CTextView.this.mDataTitle)) {
			//CTextView.this.setGravity(Gravity.NO_GRAVITY);
			CTextView.this.setText( (CharSequence) mContenHtml);
			mIsIdle = true ;
			//}

		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			//CTextView.this.setGravity(Gravity.CENTER);
			CTextView.this.setText("" + mDataTitle + "\n数据加载中\n请稍候...");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		try{
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}catch (Exception e){
			super.setText(getText().toString());
			super.onMeasure(widthMeasureSpec, heightMeasureSpec); 
		}
	}	
	@Override
	public void setGravity(int gravity){
		try{
			super.setGravity(gravity);
		}catch (Exception e){
			setText(getText().toString());
			super.setGravity(gravity); 
		}
	}
	/*
	@Override
	public void setText(CharSequence text, BufferType type) {
		try{
			super.setText(text, type);
		}catch (ArrayIndexOutOfBoundsException e){
			setText(text.toString());
		}
	}
	*/
	
	@Override
	public void setText(CharSequence text, BufferType type) {
		try{
			super.setText(text, type);
		} catch (Exception e){
			super.setText(getText().toString());
		}
	}

	



}
