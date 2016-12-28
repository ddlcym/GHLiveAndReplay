package com.changhong.replay.datafactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.R.drawable;
import android.R.raw;
import android.content.Context;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.gehua.common.Utils;
import com.changhong.ghliveandreplay.R;

public class DayMonthAdapter extends BaseAdapter{
	private static final String TAG = "DayMonthAdapter";
	private List<String> mlist=new ArrayList<String>();
	private Context mContext=null;
	
	public DayMonthAdapter(Context con){
		mContext=con;
	}
	
	public void setData(List<String> list){
		mlist=list;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlist != null ? mlist.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		String day;
		String week=null;
		String daystring =null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.epg_main_weekitem, null);	
			viewHolder.day=(TextView)convertView.findViewById(R.id.epg_week_Tview_date);
			viewHolder.week=(TextView)convertView.findViewById(R.id.epg_week_Tview_week);
			//viewHolder.line=(ImageView)convertView.findViewById(R.id.epg_week_line);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		day=mlist.get(position);
		try {
			 daystring = Utils.stringTostring(day);
			 Log.i(TAG, daystring);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		Log.i(TAG, daystring.substring(5));
		viewHolder.day.setText(daystring.substring(5));
		try {
			Date date=Utils.strToDate(day);
			if(Utils.isToday(date)){
				week="今天";
			}else{
				week=Utils.DateToWeek(date);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String weekendString = week = "("+week+")";
		viewHolder.week.setText(weekendString);
		//viewHolder.line.setBackgroundResource(R.color.touming);
		return convertView;
	}
	
 class ViewHolder {
		
		TextView day;
		TextView week;
		//ImageView line;
	}

}
