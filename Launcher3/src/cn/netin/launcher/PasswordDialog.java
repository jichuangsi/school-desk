package cn.netin.launcher;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;





public class PasswordDialog {

	private TextView mTextView ;
	private EditText mEditText ;
	private PasswordDialogListener mListener = null;
	private AlertDialog mAlertDialog ;


	public interface PasswordDialogListener {
		void onConfirm(String password) ;
	}

	public void setListener(PasswordDialogListener listener) {
		mListener = listener;
	}

	/**
	 * 创建跳转窗口
	 */
	public PasswordDialog(Context context) {
		Builder builder = new Builder(context);// 实例化一个窗口
		builder.setTitle(R.string.input_pass);// 设置标题
		LinearLayout content = new LinearLayout(context);// 实例化一个layout
		
		content.setPadding(5, 5, 5, 20);
		content.setOrientation(LinearLayout.VERTICAL);// 设置linearLayout 垂直布局
		mTextView = new TextView(context);
		mTextView.setTextSize(20) ;
		mEditText = new EditText(context) ;
		mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});

		//mEditText.setWidth(150);
		content.addView(mTextView);
		content.addView(mEditText);
		mTextView.setGravity(Gravity.CENTER);// 设置text的内容居中显示
		//mTextView.setText("当前第" + (mPageId) + "页");
		

		builder.setView(content)// 设置显示的view
		.setPositiveButton(R.string.confirm,new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				if (mListener != null) {
					mListener.onConfirm(mEditText.getText().toString()) ;
				}

			}
		}).setCancelable(true)// 设置有取消按钮
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

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


	/**显示窗口*/
	public void show() {
		mAlertDialog.show() ;
	}
	
	public void clear() {
		mEditText.setText("") ;
	}
	


}
