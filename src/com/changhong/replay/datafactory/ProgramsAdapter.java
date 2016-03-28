package com.changhong.replay.datafactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.changhong.gehua.common.ProgramInfo;
import com.changhong.ghliveandreplay.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProgramsAdapter extends BaseAdapter{

	private Context context;
	List<ProgramInfo> EpgEventdata = new ArrayList<ProgramInfo>();

	public ProgramsAdapter(Context context) {
		this.context = context;
	}

	public void setData(List<ProgramInfo> EpgEventdata){
		this.EpgEventdata=EpgEventdata;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return EpgEventdata != null ? EpgEventdata.size() : 0;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		LayoutInflater inflater = LayoutInflater.from(context);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.epg_main_eventitem,
					null);
		}

		ProgramInfo epgEvent = (ProgramInfo) EpgEventdata.get(position);
		convertView.setTag(epgEvent);
		TextView timeView = (TextView) convertView
				.findViewById(R.id.epg_event_Tview_time);
		timeView.setText(getTimes(epgEvent));
		TextView eventView = (TextView) convertView
				.findViewById(R.id.epg_event_Tview_info);
		eventView.setText(epgEvent.getEventName());
//		TextView bookView = (TextView) convertView
//				.findViewById(R.id.epg_event_Tview_timer);
		// Calendar c = Calendar.getInstance();
		// c.setTime(new Date());
		// c.add(Calendar.DAY_OF_MONTH, WeeklistItemindex);
		// SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
		// String tmpDayString = dateFormat.format(c.getTime());
		return convertView;
	}
	
	private String getTimes(ProgramInfo program){
		String times=null;
		Date beginDate=program.getBeginTime();
		Date endDate=program.getEndTime();
		String begin=beginDate.getHours()+":"+beginDate.getMinutes();
		String end=endDate.getHours()+":"+endDate.getMinutes();
		times=begin+"~"+end;
		return times;
	}
}
