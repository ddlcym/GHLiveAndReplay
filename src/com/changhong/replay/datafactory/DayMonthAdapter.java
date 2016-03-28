package com.changhong.replay.datafactory;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.epg_main_weekitem, null);	
			viewHolder.day=(TextView)convertView.findViewById(R.id.epg_week_Tview_date);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.day.setText(mlist.get(position));
		return convertView;
	}
	
 class ViewHolder {
		
		TextView day;

	}

}
