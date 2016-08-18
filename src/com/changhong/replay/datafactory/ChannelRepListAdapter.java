package com.changhong.replay.datafactory;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.changhong.gehua.common.ChannelInfo;
import com.changhong.ghliveandreplay.R;

public class ChannelRepListAdapter extends BaseAdapter {
	private Context context;
	private List<ChannelInfo> repChannels = new ArrayList<ChannelInfo>();
	private LayoutInflater inflater;

	@Override
	public int getCount() {
		return repChannels.size();
	}

	public ChannelRepListAdapter(Context con) {
		this.context = con;
		inflater = LayoutInflater.from(context);
	}
	
	public void setData(List<ChannelInfo> curChannels) {
		this.repChannels = curChannels;
		notifyDataSetChanged();
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
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.epg_main_chanitem, null);
			viewHolder = new ViewHolder();

			viewHolder.channelIndex = (TextView) convertView.findViewById(R.id.epg_chan_Tview_chanindex);
			viewHolder.channelName = (TextView) convertView.findViewById(R.id.epg_chan_Tview_channame);
			viewHolder.channelIDView = (TextView) convertView.findViewById(R.id.channelId);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ChannelInfo channel = repChannels.get(position);

		viewHolder.channelIDView.setText(channel.getChannelNumber());


		viewHolder.channelIndex.setText(channel.getChannelNumber());

		//viewHolder.channelIndex.setTextColor(0xffffffff);

		viewHolder.channelName.setText(channel.getChannelName());
		return convertView;
	}

	public class ViewHolder {
		public TextView channelIndex;
		public TextView channelName;
		public TextView channelIDView;
	}

}
