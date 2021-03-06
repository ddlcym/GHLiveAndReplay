package com.changhong.gehua.widget;

import com.changhong.gehua.common.Class_Constant;
import com.changhong.ghliveandreplay.R;
import com.hisilicon.android.mediaplayer.TimedText.Text;

import android.R.integer;
import android.R.string;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author OscarChang
 *
 */
public class ReplayEndDialog extends Dialog {

	private Button okButton;
	private Button cancelButton;
	private TextView contentTextView;
	private String contentString;
	private Handler mHandler;
	private int Type;

	public ReplayEndDialog(Context context, Handler outterHandler ,int type , String content) {
		//super(context);
		super(context,R.style.replayDialog);
		mHandler = outterHandler;
		Type = type;
		contentString = content;
		setContentView(R.layout.replay_end_dia);
		initView();
		initData();
	}

	public void initView() {
		okButton = (Button) findViewById(R.id.dialog_ok);
		cancelButton = (Button) findViewById(R.id.dialog_cancel);
		contentTextView = (TextView)findViewById(R.id.dialog_text);
		Log.i("xb", "initView"+Type);
		switch (Type) {
		case 0://非最后一个节目播放完毕或快进节目结束
			okButton.setText("观看下一个节目");
			cancelButton.setText("重新播放");
			contentTextView.setText(contentString);
			break;
		case 1:
			okButton.setText("退出回看");
			cancelButton.setText("重新播放");
			contentTextView.setText(contentString);
			break;
		case 2:
			okButton.setText("观看上一个节目");
			cancelButton.setText("重新播放");
			contentTextView.setText(contentString);
			break;
		case 3:
			okButton.setText("退出回看");
			cancelButton.setText("确定播放");
			contentTextView.setText(contentString);
			break;
		case 4:
			okButton.setText("确定");
			cancelButton.setText("取消");
			contentTextView.setText(contentString);
			break;

		default:
			
			break;
		}
		
	}

	public void initData() {
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = Class_Constant.REPLAY_DIALOG_END_OK;
				Bundle bundle = new Bundle();
				Log.i("xb", "initData"+Type);
				bundle.putInt("dialogoktype", Type);
				msg.setData(bundle);
				mHandler.sendMessage(msg);
			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = Class_Constant.REPLAY_DIALOG_END_CANCEL;
				Bundle bundle = new Bundle();
				bundle.putInt("dialogcanceltype", Type);
				msg.setData(bundle);
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
