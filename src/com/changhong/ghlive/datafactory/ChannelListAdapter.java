package com.changhong.ghlive.datafactory;

import java.util.ArrayList;
import java.util.List;

import com.changhong.gehua.common.ChannelInfo;
import com.changhong.ghliveandreplay.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChannelListAdapter extends BaseAdapter {

	// LayoutInflater inflater =
	// LayoutInflater.from(getApplicationContext());

	private LayoutInflater mInflater = null;
	private List<ChannelInfo> myList = new ArrayList<ChannelInfo>();

	public ChannelListAdapter(Context context) {
		// 根据context上下文加载布局，这里的是Demo17Activity本身，即this
		this.mInflater = LayoutInflater.from(context);
	}

	public void setData(List<ChannelInfo> list) {
		myList = list;
		notifyDataSetChanged();
	}
	
	class ViewHolder {
		TextView channelResourceOrder;
		// TextView channelIndex;
		TextView channelName;
		// ImageView favView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.channelitem, null);
			holder = new ViewHolder();
			holder.channelResourceOrder = (TextView) convertView.findViewById(R.id.chanId);
			// holder.channelIndex = (TextView)
			// convertView.findViewById(R.id.chanIndex);
			holder.channelName = (TextView) convertView.findViewById(R.id.chanName);
			// holder.favView = (ImageView)
			// convertView.findViewById(R.id.chan_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.channelResourceOrder.setText((String) (myList.get(position).getChannelNumber() + ""));
		// holder.channelIndex.setText((String)
		// myList.get(position).getChannelCode());
		holder.channelName.setText((String) myList.get(position).getChannelName());

		return convertView;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return myList.size();
	}

}
