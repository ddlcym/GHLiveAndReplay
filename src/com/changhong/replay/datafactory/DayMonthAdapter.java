package com.changhong.replay.datafactory;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.changhong.gehua.common.Utils;
import com.changhong.ghliveandreplay.R;

public class DayMonthAdapter extends BaseAdapter{
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
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.epg_main_weekitem, null);	
			viewHolder.day=(TextView)convertView.findViewById(R.id.epg_week_Tview_date);
			viewHolder.week=(TextView)convertView.findViewById(R.id.epg_week_Tview_week);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		day=mlist.get(position);
		viewHolder.day.setText(Utils.truncateDaateString(day, 5, day.length()));
		try {
			week=Utils.DateToWeek(Utils.strToDate(day));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		viewHolder.week.setText(week);
		return convertView;
	}
	
 class ViewHolder {
		
		TextView day;
		TextView week;

	}

}
