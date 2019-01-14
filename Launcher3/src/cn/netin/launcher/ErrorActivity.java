package cn.netin.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ErrorActivity extends FullScreenActivity {


	private static final  String TITLES[]  = {
			"未知错误",
			"无效的信息",
			"错误的激活码",
			"授权已经过期",
			"服务器错误", //4
			"激活次数超过限制",
			"授权与软件版本不一致",
			"读取序列号失败",

			"加密设备初始化失败",
			"加密设备没有找到",
			"加密设备开启错误", //10
			"加密设备运行错误",
			"加密设备读取错误",
			"加密设备写入错误",

			"无激活信息",
			"错误的激活信息",
			"硬件信息获取错误",
			"文件系统错误",
			"无法写入文件",
			"连接网络服务器失败", //19
			"硬件信息与激活信息不符", //20
			"非正常的软件使用状态",

			"需要升级本软件",
			"需要升级加密设备",
			"需要在Windows下使用",
			"需要写入日志",
			"已经有其它设备使用此激活码" //26
	}  ;


	private static final  String TEXTS[]  = {
			"抱歉，异常出错",
			"可能是设备不符合要求或数据传输出错",
			"请确认您输入的激活码是否正确",
			"请联系我们延长使用期限",
			"请联系我们，我们将尽快修复",
			"本激活码已经在多台设备上激活过， 无法再激活",
			"请下载安装相符的软件版本。可点切换授权方式查看详情",
			"可能加密设备已损坏",

			"加密设备初始化失败",
			"如使用U-Key或SD-Key，请重新拔插一次",
			"加密设备开启错误",
			"请重试",
			"可能加密设备已损坏",
			"可能加密设备已损坏",

			"请输入卡号和密码，进行联网激活",
			"如您更换了设备硬件，您需要重新激活。",
			"您的设备硬件信息异常，不能支持本软件运行",
			"您的设备文件系统异常，不能支持本软件运行",
			"请确保程序所在的目录是可写的",
			"请检查网络连接是否正常",
			"请使用正版软件，并正常激活",
			"请使用正版软件，并正常激活",

			"请下载安装最新版本的软件，才可继续使用",
			"需要升级加密设备，才可继续使用",
			"需要在Windows下更新加密设备，才可继续使用",
			"需要写入日志",
			"如您需要继续使用本设备，请使用激活码再次激活"
	}  ;

	private TextView mTitleView ;
	private TextView mContentView ;
	private TextView mCodeText ;
	private EditText mCodeEdit ;
	private Button mSubmitButton ;
	private Controller mController ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_error);

		mTitleView = (TextView)findViewById(R.id.error_title) ;
		mContentView = (TextView)findViewById(R.id.error_text) ;
		mCodeText = (TextView)findViewById(R.id.code_text) ;
		mCodeEdit = (EditText)findViewById(R.id.code_edit) ;

		mSubmitButton = (Button)findViewById(R.id.submit_button) ;
		mController = new Controller(this) ;
		handleIntent(getIntent()) ;
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent) ;
	}


	private void handleIntent(Intent intent){
		int error = intent.getIntExtra("ERROR", 0) ;
		if (error == 4 || error == 19) {
			displayCodeInput(false) ;
		}else{
			displayCodeInput(true) ;
		}
		showError(error) ;
	}
	
	private void showError(int error){
		if (error < 0 || error > 26) {
			error = 0 ;
		}
		mTitleView.setText(TITLES[error]);
		mContentView.setText(TEXTS[error]);
	}

	private void displayCodeInput(boolean visible) {
		if (visible) {
			mCodeText.setVisibility(View.VISIBLE);
			mCodeEdit.setVisibility(View.VISIBLE);
			mSubmitButton.setVisibility(View.VISIBLE);
		}else{
			mCodeText.setVisibility(View.INVISIBLE);
			mCodeEdit.setVisibility(View.INVISIBLE);
			mSubmitButton.setVisibility(View.INVISIBLE);
		}
	}

	
	public void onClick(View v){
		int id = v.getId() ;
		if (id == R.id.retry_button){
			mController.prepareItemList();
			this.finish();
		}
		else if (id == R.id.submit_button){
			activate() ;
		}
		else if (id == R.id.close_button){
			this.finish();
		}
	}
	
	
	private void activate(){
		String code = mCodeEdit.getText().toString() ;
		if (code.length() != 8) {
			Toast.makeText(this, "激活码长度为8", Toast.LENGTH_SHORT).show();
			return ;
		}
		int error = mController.activate(code) ;
		if (error == 0) {
			Toast.makeText(this, "激活成功", Toast.LENGTH_SHORT).show();
			mController.prepareItemList();
			this.finish();
			return ;
		}
		showError(error) ;
	}
}
