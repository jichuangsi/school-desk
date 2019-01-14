package cn.netin.wifisetting;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by Xiho on 2016/2/2.
 */
public class WifiStatusDialog extends Dialog {
    // Wifi管理类
    private WifiUtils mWifiAdmin;
    private ScanResult scanResult;
    private TextView txtWifiName;
    private TextView txtConnStatus;
    private TextView txtSinglStrength;
    private TextView txtSecurityLevel;
    private TextView txtIpAddress;
    private TextView txtBtnDisConn;
    private TextView txtBtnCancel;
    private String wifiName;
    private String securigyLevel;
    private int level;
    private static final String TAG = "WifiStatusDialog" ;

    public WifiStatusDialog(Context context, int theme) {
        super(context, theme);
        this.mWifiAdmin = new WifiUtils(context);
    }

    private WifiStatusDialog(Context context, int theme, String wifiName,
                             int singlStren, String securityLevl) {
        super(context, theme);
        this.wifiName = wifiName;
        this.level = singlStren;
        this.securigyLevel = securityLevl;
        this.mWifiAdmin = new WifiUtils(context);
        Log.d(TAG, "WifiStatusDialog 0 ") ;
    }

    public WifiStatusDialog(Context context, int theme, ScanResult scanResult,
                            OnNetworkChangeListener onNetworkChangeListener) {
        this(context, theme, scanResult.SSID, scanResult.level,
                scanResult.capabilities);
        this.scanResult = scanResult;
        this.mWifiAdmin = new WifiUtils(context);
        this.onNetworkChangeListener = onNetworkChangeListener;
        Log.d(TAG, "WifiStatusDialog 1 ") ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {       
        super.onCreate(savedInstanceState);
        Log.d(TAG, "WifiStatusDialog onCreate ") ;
        setContentView(R.layout.view_wifi_status);
        setCanceledOnTouchOutside(false);

        initView();
        setListener();
    }

    private void setListener() {

        txtBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                WifiStatusDialog.this.dismiss();
            }
        });

        txtBtnDisConn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int netId = mWifiAdmin.getNetworkId();
                Log.d(TAG, "忘记网络" + netId) ;
                mWifiAdmin.forgetWifi(netId);
                WifiStatusDialog.this.dismiss();
                onNetworkChangeListener.onNetWorkDisConnect();
            }
        });
    }

    private void initView() {
        txtWifiName = (TextView) findViewById(R.id.txt_wifi_name);
        txtConnStatus = (TextView) findViewById(R.id.txt_conn_status);
        txtSinglStrength = (TextView) findViewById(R.id.txt_signal_strength);
        txtSecurityLevel = (TextView) findViewById(R.id.txt_security_level);
        txtIpAddress = (TextView) findViewById(R.id.txt_ip_address);

        txtBtnCancel = (TextView) findViewById(R.id.txt_btn_cancel);
        txtBtnDisConn = (TextView) findViewById(R.id.txt_btn_disconnect);

        txtWifiName.setText(wifiName);
        boolean connected = mWifiAdmin.isConnected(scanResult) ;
        if (connected){
        	txtConnStatus.setText("已连接");        	
        }else{
        	txtConnStatus.setText("未连接");
        }
        
        txtSinglStrength.setText(WifiUtils.singlLevToStr(level));
        txtSecurityLevel.setText(securigyLevel);
        txtIpAddress
                .setText(mWifiAdmin.ipIntToString(mWifiAdmin.getIpAddress()));

    }

    @Override
    public void show() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);

        super.show();
        getWindow().setLayout((int) (size.x * 9 / 10),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }
  

    private OnNetworkChangeListener onNetworkChangeListener;

}
