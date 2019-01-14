package com.hanceedu.common.widget;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hanceedu.common.R;



public class SeekBarDialog {

	private SeekBar mSeekbar ;
	private TextView mTextView ;
	private int mStart ;
	private SeekBarDialogListener mSeekBarDialogListener = null;
	private AlertDialog mAlertDialog ;


	public interface SeekBarDialogListener {
		void onChange(int value) ;
		void onConfirm(int value) ;
	}

	public void setListener(SeekBarDialogListener listener) {
		mSeekBarDialogListener = listener;
	}

	/**
	 * 创建跳转窗口
	 */
	public SeekBarDialog(Context context) {
		Builder builder = new Builder(context);// 实例化一个窗口
		builder.setTitle("滑动选择");// 设置标题
		LinearLayout content = new LinearLayout(context);// 实例化一个layout
		// 放置显示页数view和进度seekbar
		content.setPadding(5, 5, 5, 20);
		content.setOrientation(LinearLayout.VERTICAL);// 设置linearLayout 垂直布局
		mTextView = new TextView(context);
		mTextView.setTextSize(20) ;
		mSeekbar = new SeekBar(context);
		content.addView(mTextView);
		content.addView(mSeekbar);
		mTextView.setGravity(Gravity.CENTER);// 设置text的内容居中显示
		//mTextView.setText("当前第" + (mPageId) + "页");
		

		// 监听 seekbar进度条改变
		mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

				// 由于seekbar的值范围是0~max 而 页数的范围是1~总页数
				// 所以当前页数是 progress+1
				mTextView.setText("" + (progress + mStart));
				if (mSeekBarDialogListener != null) {
					mSeekBarDialogListener.onChange(progress + mStart) ;
				}
			}
		});

		builder.setView(content)// 设置显示的view
		.setPositiveButton("确定", 
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int pageId = mSeekbar.getProgress() + mStart ;
				if (mSeekBarDialogListener != null) {
					mSeekBarDialogListener.onConfirm(pageId) ;
				}

			}
		}).setCancelable(true)// 设置有取消按钮
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		

		mAlertDialog = builder.create() ;

	}

	/**设置标题*/
	public void setTitle(String title) {
		mAlertDialog.setTitle(title) ;
	}

	/**设置value*/
	public void setValue(int start, int max, int current) {
		// 设置 seekbar的最大值 由于seekbar的值范围是0~max 而 页数的范围是1~总页数，
		// 所以seekbar的最大值为总页数-1
		mSeekbar.setMax(max - start);
		mSeekbar.setProgress(current);// 设置seekbar当前显示值
		mStart = start ;
	}

	/**显示窗口*/
	public void show() {
		mAlertDialog.show() ;
	}

}
