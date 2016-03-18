package com.changhong.ghlive.activity;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

/**
 * jsonRequest
 * @author chen
 *
 */
public class JsonRequestAcitivity extends Activity implements OnClickListener,Listener<JSONObject>,ErrorListener{
	private Button requestBtn;
	private TextView dispText;
	private static final String URL = "http://httpbin.org/get?param1=hello";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onClick(View v) {
		dispText.setText("请求中...");
		doJsonRequest();
	}
	
	private void doJsonRequest() {
		//第三个参数
		//A JSONObject to post with the request. Null is allowed and indicates no parameters will be posted along with request.
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Request.Method.GET,URL,null, JsonRequestAcitivity.this,JsonRequestAcitivity.this);
		jsonObjectRequest.setTag(JsonRequestAcitivity.class.getSimpleName());//设置tag callAll的时候使用
	}
	@Override
	public void onResponse(JSONObject response) {
		//success 
		dispText.setText("Success::"+response.toString());
	}
	@Override
	public void onErrorResponse(VolleyError error) {
		dispText.setText("Fail::"+error.toString());
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
}
