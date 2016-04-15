package com.changhong.gehua.widget;

import com.changhong.gehua.common.Class_Constant;
import com.changhong.ghliveandreplay.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

/**
 * @author OscarChang
 *
 */
public class ReplayEndDialog extends Dialog {

	private Button okButton;
	private Button cancelButton;
	private Handler mHandler;

	public ReplayEndDialog(Context context, Handler outterHandler) {
		super(context);
		mHandler = outterHandler;
		setContentView(R.layout.replayenddia);
		initView();
		initData();
		// TODO Auto-generated constructor stub
	}

	public void initView() {
		okButton = (Button) findViewById(R.id.dialog_ok);
		cancelButton = (Button) findViewById(R.id.dialog_cancel);
	}

	public void initData() {
		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = Class_Constant.REPLAY_DIALOG_END_OK;
				mHandler.sendMessage(msg);

			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = Class_Constant.REPLAY_DIALOG_END_CANCEL;
				mHandler.sendMessage(msg);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			dismiss();
			cancel();
			break;

		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
	}
}
