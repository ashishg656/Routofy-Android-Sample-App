package com.ashish.routofy;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {

	Context context;
	ArrayList<JsonObjectParse> data;
	private int lastPosition = -1;

	public ListAdapter(Context c, ArrayList<JsonObjectParse> data) {
		context = c;
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.list_item_layout, parent, false);
			
			holder = new Holder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		JsonObjectParse obj = data.get(position);
		holder.title.setText(obj.title);
		holder.body.setText(obj.body);
		
		Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
	    convertView.startAnimation(animation);
	    lastPosition = position;

		return convertView;
	}

}

class Holder {
	TextView title;
	TextView body;

	public Holder(View v) {
		title = (TextView) v.findViewById(R.id.tv1);
		body = (TextView) v.findViewById(R.id.tv2);
	}
}
