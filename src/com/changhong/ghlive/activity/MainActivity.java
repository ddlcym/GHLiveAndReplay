package com.changhong.ghlive.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.changhong.gehua.common.ChannelInfo;
import com.changhong.gehua.common.ProcessData;
import com.changhong.gehua.common.VolleyTool;
import com.changhong.ghlive.datafactory.ChannelListAdapter;
import com.changhong.ghlive.datafactory.HandleLiveData;
import com.changhong.ghlive.service.HttpService;
import com.changhong.ghliveandreplay.R;

public class MainActivity extends BaseActivity {

	private String TAG = "mmmm";

	private VolleyTool volleyTool;
	private RequestQueue mReQueue;

	private ProcessData processData;
	private List<ChannelInfo> channels = null;

	private ListView chListView;
	private ChannelListAdapter chLstAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		volleyTool = VolleyTool.getInstance();
		mReQueue = volleyTool.getRequestQueue();
		if (null == processData) {
			processData = new ProcessData();
		}
		startHttpSer();
		getChannelList();
		// Log.i("mmmm", "c"+date.getHours()+"-"+date.getMonth());
	}

	@Override
	protected void initView() {

		// TODO Auto-generated method stub
		setContentView(R.layout.channellist);
		chListView = (ListView) findViewById(R.id.id_epg_chlist);
		chLstAdapter = new ChannelListAdapter(MainActivity.this);
		 Log.i("mmmm", "chListView" + chListView);
		 chListView.setAdapter(chLstAdapter);
	}

	private void startHttpSer() {
		Intent intent = new Intent(this, HttpService.class);
		startService(intent);
	}

	private void getChannelList() {
		// 传入URL请求链接
		String URL = processData.getChannelList();
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
				new Response.Listener<org.json.JSONObject>() {

					@Override
					public void onResponse(org.json.JSONObject arg0) {
						// TODO Auto-generated method stub
						// 相应成功
						// Log.i(TAG, "HttpService=channle:" + arg0);
						channels = HandleLiveData.getInstance().dealChannelJson(arg0);
						// setadapter
						chLstAdapter.setData(channels);
						// if (channels.size() <= 0) {
						// // focusView.setVisibility(View.INVISIBLE);
						// }
					}
				}, errorListener);
		jsonObjectRequest.setTag(HttpService.class.getSimpleName());// 设置tag,cancelAll的时候使用
		mReQueue.add(jsonObjectRequest);
	}

	private Response.ErrorListener errorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			Log.i(TAG, "HttpService=error：" + arg0);
		}
	};

	private void showTime() {
	}


	// ================================================================================
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
