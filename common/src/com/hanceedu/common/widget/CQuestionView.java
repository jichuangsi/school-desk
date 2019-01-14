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
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.hanceedu.common.data.SearchData;


/**
 * 用于显示html文字<br>
 * 将html装换为CharSequence 对象时使用了AsyncTask异步处理
 * @author long.wu
 *
 */
public class CQuestionView extends TextView {

	private static final String TAG = "CEditText";
	private Context mContext;
	/** 设置显示的编码 */
	private String mEncoding = "GBK";
	/** 网页文件路径 */

	private String mDataTitle = "";
	
	private boolean mIsIdle = true ;

	public CQuestionView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}


	public CQuestionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public CQuestionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public void init() {
		this.setMovementMethod(ScrollingMovementMethod.getInstance());
	}
	
	public boolean isIdle() {
		return mIsIdle ;
	}


	public void setQuestion(byte[] sId)
	{
		this.scrollTo(0, 0) ;
		mIsIdle = false ;
		byte[] b = SearchData.getQuestion(sId);
		//System.out.println("contentHtml len" + b.length);
		String contentHtml = "";
		if (b != null) {
			try {
				contentHtml = new String(b, "GBK");
				//System.out.println("contentHtml=" + contentHtml);
				// 异步加载
				HtmlFormTask task = new HtmlFormTask(mContext);
				task.execute(contentHtml);

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mIsIdle = true ;
			}
		}

	}

	public void setAnswer(byte[] sId)
	{
		this.scrollTo(0, 0) ;
		mIsIdle = false ;
		byte[] b = SearchData.getAnswer(sId);
		String contentHtml = "";
		if (b != null) {
			try {
				contentHtml = new String(b, "GBK");
				// 异步加载
				HtmlFormTask task = new HtmlFormTask(mContext);
				task.execute(contentHtml);

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mIsIdle = true ;
			}
		}

	}

	public void setContent(byte[] sId)
	{
		this.scrollTo(0, 0) ;
		mIsIdle = false ;
		byte[] b = SearchData.getAnswer(sId);
		if (b == null || b.length == 0) {
			return ;
		}
		String content = "";
		try {
			content = new String(b, "GBK");	
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			mIsIdle = true ;
			return ;
		}

		this.setText(content);
		mIsIdle = true ;

	}

	public void setWritingContent(byte[] sId)
	{
		this.scrollTo(0, 0) ;
		mIsIdle = false ;
		byte[] b = SearchData.getAnswer(sId);
		if (b == null || b.length == 0) {
			mIsIdle = true ;
			return ;
		}
		String content = "";
		try {
			content = new String(b, "GBK");	
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			mIsIdle = true ;
			return ;
		}
		//点评
		b = SearchData.getQuestion(sId);
		if (b != null && b.length > 0) {
			String comment = "";
			try {
				comment = new String(b, "GBK");	
				content += "\n\n---------------------------------------------\n\n" + comment ;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				mIsIdle = true ;
				return ;
			}
		}
		content = content.replace("本文系本站用户原创文章，未经允许禁止转载！", "")
		.replace("[小x山s屋-作z文w网]", "").replace("\r\n\r\n", "\n") ;

		/*
		 * (?s) =  空行也匹配
		 * X? X, once or not at all , greedy
		 * X* X, zero or more times , greedy
		 * X+ X, one or more times , greedy
		 * 
		 * X?? X, once or not at all , none greedy
		 * X*? X, zero or more times , none greedy
		 * X+? X, one or more times , none greedy
		 */
		content = content.replaceAll("(?s)&.{3,10}?;", "") ;
		content = content.replaceAll("(?s)&.{2,10}?p", "") ;		
		this.setText(content);
		mIsIdle = true ;

	}


	/**
	 * 图片处理
	 */
	private ImageGetter imgGetter = new Html.ImageGetter() {
		@Override
		public Drawable getDrawable(String source) {
			Drawable drawable = null;
			try {
				int id = Integer.parseInt(source.substring(0,source.lastIndexOf('.'))) ;
				byte[] image = SearchData.getPicture(id);
				InputStream in = new ByteArrayInputStream(image);
				drawable = Drawable.createFromStream(in, source);
				if (drawable == null) {
					Log.e(TAG, "drawable为空");
				}
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				return drawable;
			} catch (Exception e) {
				Log.e(TAG, "图片出错");
				e.printStackTrace();
				/*
				drawable = CTextView.this.mContext.getResources().getDrawable(
						R.drawable.transparent);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(),
						drawable.getMinimumHeight());
				 */
				return drawable;
			}

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

		public HtmlFormTask(Context context) {

		}

		@Override
		protected String doInBackground(String... params) {
			mContenHtml = Html.fromHtml(params[0], imgGetter, tagHandler);
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mIsIdle = true ;
		}

		@Override
		protected void onPostExecute(String result) {
			CQuestionView.this.setGravity(Gravity.NO_GRAVITY);
			CQuestionView.this.setText(mContenHtml);
			mIsIdle = true ;
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			//CQuestionView.this.setGravity(Gravity.CENTER);
			//CQuestionView.this.setText("[" + mDataTitle + "]\n数据加载中\n请稍候...");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度
		}

	}

	
}
