/**
 * 
 */
package com.hanceedu.common.widget;


import com.hanceedu.common.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 页码导航工具条
 * 
 */
public class PageNavi extends LinearLayout
{
	/**
	 * 事件监听器接口
	 * showPageNumInput() ： 显示数字输入虚拟键盘
	 * onPageChanged(int pageNo) ：页码变动
	 */
	public interface PageNaviListener
	{
		void showPageNumInput();
		void onPageChanged(int pageNo) ;
	}
	
	/** 该翻页控制器提供的最大页数 */
	private int totalPage = 10;
	
	/** 该翻页控制器当前显示的页数 */
	private int currentPage = 1;
	
	/** 页面显示对象  显示格式为 currentPage/totalPage */
	private TextView mShowNum;
	
	/** 各个按钮对象 */
	private ImageButton first;
	private ImageButton next;
	private ImageButton prev;
	private ImageButton last;
	private PageNaviListener pageNaviListener;
	/**
	 * 设置按钮监听事件
	 */
	private OnClickListener listener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			if (v.getId() == R.id.first_btn) {
				gotoPage(1);
			} else if (v.getId() == R.id.prev_btn) {
				gotoPage(currentPage - 1);
			} else if (v.getId() == R.id.next_btn) {
				gotoPage(currentPage + 1);
			} else if (v.getId() == R.id.last_btn) {
				gotoPage(totalPage);
			} else if (v.getId() == R.id.showNum) {
				pageNaviListener.showPageNumInput();
			}
		}
	};


	public void setPageNaviListener(PageNaviListener listener)
	{
		pageNaviListener = listener;
	}
	
	public PageNavi(Context context)
	{
		super(context);
		initView(context) ;
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public PageNavi(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context) ;

	}
	
	private void initView(Context context) {
		
		
		
		
		ViewGroup layout = (ViewGroup) inflate(context, R.layout.pagenavi, this);
		mShowNum = (TextView) layout.findViewById(R.id.showNum);
		first = (ImageButton) layout.findViewById(R.id.first_btn);
		last = (ImageButton) layout.findViewById(R.id.last_btn);
		next = (ImageButton) layout.findViewById(R.id.next_btn);
		prev = (ImageButton) layout.findViewById(R.id.prev_btn);

		mShowNum.setOnClickListener(listener);
		first.setOnClickListener(listener);
		last.setOnClickListener(listener);
		next.setOnClickListener(listener);
		prev.setOnClickListener(listener);

	}

	/**
	 * 设置总页数总页数
	 * 
	 * @param pageTotal
	 */
	public void setPageTotal(int pageTotal)
	{
		this.totalPage = pageTotal;
		String showText = currentPage + "/" + pageTotal;
		mShowNum.setText(showText);
		enableButtons();
	}

	/**
	 * 判断页码控制前翻和后翻按钮
	 */
	private void enableButtons()
	{
		if (currentPage == 1)
		{
			first.setEnabled(false);
			prev.setEnabled(false);
			last.setEnabled(true);
			next.setEnabled(true);
		} else if (currentPage == totalPage)
		{
			last.setEnabled(false);
			next.setEnabled(false);
			first.setEnabled(true);
			prev.setEnabled(true);
		} else
		{
			first.setEnabled(true);
			prev.setEnabled(true);
			last.setEnabled(true);
			next.setEnabled(true);
		}
	}


	/**
	 * 跳转目标页面
	 * 与setPageNo的区别是，这个方法回调onPageChanged(pageNo) 
	 * @param pageNo
	 */
	public void gotoPage(int pageNo)
	{
		setPageNo(pageNo) ;
		
		pageNaviListener.onPageChanged(pageNo) ;
	}
	
	/**
	 * 设置当前页码
	 * 与gotoPage的区别是，这个方法不回调onPageChanged(pageNo) 
	 * @param pageNo
	 */	
	public void setPageNo(int pageNo)
	{
		if (pageNo < 1)
		{
			pageNo = 1 ;
		} else if (pageNo > totalPage)
		{
			pageNo = totalPage ;
		} 
		
		this.currentPage = pageNo;
		String showText = pageNo + "/" + totalPage;
		mShowNum.setText(showText);
		enableButtons();
		
	}

	public int getPageTotal()
	{
		return this.totalPage;
	}
}
