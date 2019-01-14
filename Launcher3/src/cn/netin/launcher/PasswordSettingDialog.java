package cn.netin.launcher;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



public class PasswordSettingDialog {

	private String mPassword ;
	private TextView mCurrentLabel ;
	private TextView mPassLabel ;
	private TextView mConfirmLabel ;
	private EditText mCurrentText ;
	private EditText mPassText ;
	private EditText mConfirmText ;
	private Context mContext ;

	private PasswordSettingDialogListener mListener = null;
	private AlertDialog mAlertDialog ;


	public interface PasswordSettingDialogListener {
		void onConfirm(String password) ;
	}

	public void setListener(PasswordSettingDialogListener listener) {
		mListener = listener;
	}

	/**
	 * 创建跳转窗口
	 */
	public PasswordSettingDialog(Context context, String password) {
		mContext = context ;
		mPassword = password ;
		Builder builder = new Builder(context);// 实例化一个窗口
		builder.setTitle(R.string.set_pass);// 设置标题
		LinearLayout content = new LinearLayout(context);// 实例化一个layout
		
		content.setPadding(5, 5, 5, 20);
		content.setOrientation(LinearLayout.VERTICAL);// 设置linearLayout 垂直布局
		mCurrentLabel = new TextView(context);
		mCurrentLabel.setText(R.string.old_pass);
		mPassLabel = new TextView(context);
		mPassLabel.setText(R.string.new_pass);
		mConfirmLabel = new TextView(context);
		mConfirmLabel.setText(R.string.reenter_pass);
		
		mCurrentText = new EditText(context) ;
		mPassText = new EditText(context) ;
		mConfirmText = new EditText(context) ;
		
		content.addView(mCurrentLabel);
		content.addView(mCurrentText);
		content.addView(mPassLabel);
		content.addView(mPassText);
		content.addView(mConfirmLabel);
		content.addView(mConfirmText);
		
		mCurrentLabel.setGravity(Gravity.CENTER);// 设置text的内容居中显示
		

		builder.setView(content)// 设置显示的view
		.setPositiveButton(R.string.confirm, 
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				if (mPassword != null && !mPassword.equals("")) {
					String currentPass = mCurrentText.getText().toString().trim() ;
					if (currentPass.equals("")) {
						Toast.makeText(mContext, R.string.new_pass_please, Toast.LENGTH_SHORT).show();
						return ;
					}
					if (!currentPass.equals(mPassword)) {
						Toast.makeText(mContext, R.string.old_pass_wrong, Toast.LENGTH_SHORT).show();
						return ;
					}
					
				}
				String pass = mPassText.getText().toString().trim() ;
				String confirm = mConfirmText.getText().toString().trim() ;
				
				if (!pass.equals(confirm)) {
					Toast.makeText(mContext, R.string.not_same, Toast.LENGTH_SHORT).show();
					return ;
				}
				
				if (mListener != null) {
					mListener.onConfirm(pass) ;
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
		if (mPassword == null || mPassword.equals("")) {
			mCurrentLabel.setVisibility(View.GONE) ;
			mCurrentText.setVisibility(View.GONE) ;
		}else{
			mCurrentLabel.setVisibility(View.VISIBLE) ;
			mCurrentText.setVisibility(View.VISIBLE) ;	
		}
	}
	
	public void clear() {
		mCurrentText.setText("") ;
		mPassText.setText("") ;
		mConfirmText.setText("") ;
	}
	
	public void setPass(String password) {
		mPassword = password ;
	}
}
