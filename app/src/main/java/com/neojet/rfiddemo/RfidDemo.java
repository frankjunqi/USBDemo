package com.neojet.rfiddemo;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.neojet.scan.ScanManager;
import com.neojet.utils.Constant;
import com.neojet.utils.RfidDataListener;
import com.neojet.utils.RfidManager;

public class RfidDemo extends Activity implements OnClickListener{


	private final static String TAG = "RfidDemo";
	private RfidManager rfidManager;
	private ScanManager scanManager;
	private Button cardSN,read,write ,settingBtn,scanBtn;
	private EditText msgWindow;
	private String msg = "";
	private Timer timer;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.rfid);
		setupViews();


		rfidManager = new RfidManager(this);

		rfidManager.setOnRfidDataListener(new RfidDataListener() {//监听请求rfidManager后返回的来的数据

			@Override
			public void onDataReady(int whichAction) {
				int[] buf;
				switch (whichAction) {//whichaAction:读块，写块和读卡号
					case Constant.REQUEST_CARDSN://读卡号
						buf = rfidManager.getRfidData(Constant.REQUEST_CARDSN);
						if ((buf.length == 2) && (buf[0] == 0)) {
							msg += "Read Card Error";
						}else {
							msg += "Card SN : ";
							for (int i = 0; i < buf.length; i++) {
								msg += Integer.toHexString(buf[i]) + "  ";
							}
						}

						msg += "\r\n";
						msgWindow.setText(msg);
						msgWindow.setSelection(msgWindow.getText().length(), msgWindow.getText().length());

						break;
					case Constant.REQUEST_READ://读块
						buf = rfidManager.getRfidData(Constant.REQUEST_READ);
						for (int i = 0; i < buf.length; i++) {
							msg += Integer.toHexString(buf[i]) + " ";
						}
						msg += "\r\n";
						msgWindow.setText(msg);
						msgWindow.setSelection(msgWindow.getText().length(), msgWindow.getText().length());
						break;
					case Constant.REQUEST_WRITE://写块
						buf = rfidManager.getRfidData(Constant.REQUEST_WRITE);
						for (int i = 0; i < buf.length; i++) {
							msg += Integer.toHexString(buf[i]);
						}
						msg += "\r\n";
						msgWindow.setText(msg);
						msgWindow.setSelection(msgWindow.getText().length(), msgWindow.getText().length());
						break;
					default:
						break;

				}

			}
		});


		scanManager = new ScanManager(this);
		scanManager.setOnRfidDataListener(new RfidDataListener() {

			@Override
			public void onDataReady(int whichAction) {
				int[] buf;
				switch (whichAction) {
					case Constant.REQUEST_SCAN:
						buf = scanManager.getScanData();
						if (buf.length == 1) {
							msg += "Scan Error";

						}else {
							msg += "Scan Result : ";
							String str = "";
							for (int i = 0; i < buf.length; i++) {
								str = Integer.toHexString(buf[i]);
								msg += str.substring(str.length()-1);
							}

						}
						msg += "\r\n";
						msgWindow.setText(msg);
						msgWindow.setSelection(msgWindow.getText().length(), msgWindow.getText().length());
						break;


					default:
						break;
				}

			}
		});

	}



	private void setupViews() {
		cardSN = (Button)findViewById(R.id.card_sn);
		cardSN.setOnClickListener(this);

		settingBtn = (Button)findViewById(R.id.setting);
		settingBtn.setOnClickListener(this);

		scanBtn = (Button)findViewById(R.id.scan);
		scanBtn.setOnClickListener(this);

	/*	read = (Button)findViewById(R.id.read);
		read.setOnClickListener(this);

		write = (Button)findViewById(R.id.write);
		write.setOnClickListener(this);*/

		msgWindow = (EditText)findViewById(R.id.logcat);
		msgWindow.setMovementMethod(ScrollingMovementMethod.getInstance());

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.card_sn:
				rfidManager.requestCardSN();//读卡号,如读卡成功则返回装卡号的数组，反之返回0
				break;
/*		case R.id.read:
			int[] key = {0xff,0xff,0xff,0xff,0xff,0xff};
			rfidManager.requestRead(0x07, 0x60, key);//读块,0x04：扇区1，块0；0x60：keyA; key:6个字节0xff，初始默认密码，如卡密码已改，则需替换为改后的密码

			break;
		case R.id.write:
			int[] dataBuf = {0x05 ,0x05 ,0x05 ,0x05 ,0x05 ,0x05 ,0x05 ,0x05 ,0x05 ,0x05 ,0x05 ,0x05 ,0x05 ,0x05 ,0x05 ,0x67};
			int[] key1 = {0xff,0xff,0xff,0xff,0xff,0xff};
			rfidManager.requestWrite(0x04, 0x60, key1, dataBuf);//写块,0x04：扇区1，块0；0x60：keyA; key1:6个字节0xff，初始默认密码，如卡密码已改，则需替换为改后的密码；
																//	dataBuf:写到块里的数据，共12个字节
			break;*/
			case R.id.scan:
				scanManager.requestScan();
				break;
			case R.id.setting:
				break;

			default:
				break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_CAMERA://RFID KEY
				rfidManager.requestCardSN();//add the key's function here
				break;

			default:
				break;
		}

		return super.onKeyDown(keyCode, event);
	}

}