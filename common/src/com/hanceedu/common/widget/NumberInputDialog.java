package com.hanceedu.common.widget;


import com.hanceedu.common.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 通过xml控制组件布局,使用android自带控件组合成为一个自定义的虚拟键盘
 * 
 * @author ben.upsilon@gmail.com
 * @date 2011-9-22
 * @~ com.hanceedu.common.widget//KeyboardByWidget
 */
public class NumberInputDialog extends Dialog {
	
	public interface NumberInputListener {
		void onSubmit(int pageNo);

		//void onCancel();
	}

	/** 该虚拟键盘输入的最大值 */
	private int maxNum = 100;

	private int minNum = 1 ;

	private NumberInputListener numberInputListener;

	/** 虚拟键盘中的虚拟显示界面 */
	private EditText pageOutputer;

	/** 存储虚拟键盘显示的内容及输出的内容 */
	private StringBuffer numberStr = new StringBuffer();

	/** 虚拟键盘的各个按钮组件对象 */
	private Button submit_btn, num_1_btn, num_2_btn, num_3_btn, num_4_btn,
			num_5_btn, num_6_btn, num_7_btn, num_8_btn, num_9_btn, num_0_btn,
			cancel_btn;

	private ImageButton del_btn;

	private android.view.View.OnClickListener clickListener = new _OnClickListener();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.number_input_dialog);
		bindID();
		bindListener();
	}

	/**
	 * 绑定监听器
	 */
	private void bindListener() {
		submit_btn.setOnClickListener(clickListener);
		del_btn.setOnClickListener(clickListener);
		pageOutputer.setOnClickListener(clickListener);
		num_0_btn.setOnClickListener(clickListener);
		num_1_btn.setOnClickListener(clickListener);
		num_2_btn.setOnClickListener(clickListener);
		num_3_btn.setOnClickListener(clickListener);
		num_4_btn.setOnClickListener(clickListener);
		num_5_btn.setOnClickListener(clickListener);
		num_6_btn.setOnClickListener(clickListener);
		num_7_btn.setOnClickListener(clickListener);
		num_8_btn.setOnClickListener(clickListener);
		num_9_btn.setOnClickListener(clickListener);
		cancel_btn.setOnClickListener(clickListener);

		/*
		// 添加输出控制监听器,这里主要实现功能是:控制用户输入的数字不能超过传递来的最大值
		pageOutputer.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!s.toString().equals("")) {
					if (Integer.parseInt(s.toString()) > maxNum) {
						pageOutputer.setText(maxNum + "");
					}
					if (Integer.parseInt(s.toString()) < minNum) {
						pageOutputer.setText(minNum + "");
					}
				}
			}
		});
		*/
		
	}

	/**
	 * 绑定组件id
	 */
	private void bindID() {
		submit_btn = (Button) findViewById(R.id.submit_btn);
		del_btn = (ImageButton) findViewById(R.id.del_btn);
		pageOutputer = (EditText) findViewById(R.id.editPageNum);

		num_0_btn = (Button) findViewById(R.id.num_0_btn);
		num_1_btn = (Button) findViewById(R.id.num_1_btn);
		num_2_btn = (Button) findViewById(R.id.num_2_btn);
		num_3_btn = (Button) findViewById(R.id.num_3_btn);
		num_4_btn = (Button) findViewById(R.id.num_4_btn);
		num_5_btn = (Button) findViewById(R.id.num_5_btn);
		num_6_btn = (Button) findViewById(R.id.num_6_btn);
		num_7_btn = (Button) findViewById(R.id.num_7_btn);
		num_8_btn = (Button) findViewById(R.id.num_8_btn);
		num_9_btn = (Button) findViewById(R.id.num_9_btn);
		cancel_btn = (Button) findViewById(R.id.cancel_btn);
	}

	public NumberInputDialog(Context context) {
		super(context, R.style.NumpadDialog);
	}
	
	public NumberInputDialog(Context context, int max) {
		super(context, R.style.NumpadDialog);
		this.maxNum = max;	
	}
	
	/**
	 * 创建一个数字输入对话框
	 * @param context
	 * @param max 最大数字
	 * @param min 最小数字
	 */
	public NumberInputDialog(Context context, int max, int min) {
		super(context, R.style.NumpadDialog);
		this.maxNum = max;	
		this.minNum = min ;
	}

	public void setNumberInputListener(NumberInputListener numberInputListener) {
		this.numberInputListener = numberInputListener;
	}

	/**
	 * 按钮内部监听使用的
	 */
	class _OnClickListener implements android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.del_btn) {
				if (numberStr.length() != 0) {
					numberStr.deleteCharAt(numberStr.length() - 1);
					pageOutputer.setText(numberStr);
				}
			} else if (v.getId() == R.id.cancel_btn) {
				numberStr.delete(0, numberStr.length());
				pageOutputer.setText("");
				//numberInputListener.onCancel();
				NumberInputDialog.this.dismiss() ;
			} else if (v.getId() == R.id.submit_btn) {
				if (numberStr.length() != 0) {
					numberInputListener.onSubmit(Integer.parseInt(numberStr.toString()));
				}
				NumberInputDialog.this.dismiss() ;
			} else if (v.getId() == R.id.num_0_btn) {
				add("0");
			} else if (v.getId() == R.id.num_1_btn) {
				add("1");
			} else if (v.getId() == R.id.num_2_btn) {
				add("2");
			} else if (v.getId() == R.id.num_3_btn) {
				add("3");
			} else if (v.getId() == R.id.num_4_btn) {
				add("4");
			} else if (v.getId() == R.id.num_5_btn) {
				add("5");
			} else if (v.getId() == R.id.num_6_btn) {
				add("6");
			} else if (v.getId() == R.id.num_7_btn) {
				add("7");
			} else if (v.getId() == R.id.num_8_btn) {
				add("8");
			} else if (v.getId() == R.id.num_9_btn) {
				add("9");
			}
		}

	}

	private void add(String ch) {
		numberStr.append(ch);

			if (Integer.parseInt(numberStr.toString()) > maxNum) {
				numberStr.delete(0, numberStr.length());
				numberStr.append(maxNum) ;
			}
			else if (Integer.parseInt(numberStr.toString()) < minNum) {
				numberStr.delete(0, numberStr.length());
				numberStr.append(minNum) ;
			}

		pageOutputer.setText(numberStr);
	}
}
