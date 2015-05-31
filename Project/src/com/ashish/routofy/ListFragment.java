package com.ashish.routofy;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.ashish.routofy.observablelist.ObservableListView;
import com.ashish.routofy.observablelist.ObservableScrollViewCallbacks;
import com.ashish.routofy.observablelist.ScrollUtils;

public class ListFragment extends Fragment {
	ArrayList<JsonObjectParse> arrayList;
	ObservableListView list;
	public static final String ARG_INITIAL_POSITION = "ARG_INITIAL_POSITION";

	public static ListFragment newInstance(Bundle b) {
		ListFragment frag = new ListFragment();
		frag.setArguments(b);
		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_layout, container, false);
		list = (ObservableListView) view.findViewById(R.id.list);
		
		Activity parentActivity = getActivity();
		if (parentActivity instanceof ObservableScrollViewCallbacks) {
            // Scroll to the specified position after layout
            Bundle args = getArguments();
            if (args != null && args.containsKey(ARG_INITIAL_POSITION)) {
                final int initialPosition = args.getInt(ARG_INITIAL_POSITION, 0);
                ScrollUtils.addOnGlobalLayoutListener(list, new Runnable() {
                    @Override
                    public void run() {
                        // scrollTo() doesn't work, should use setSelection()
                        list.setSelection(initialPosition);
                    }
                });
            }

            // TouchInterceptionViewGroup should be a parent view other than ViewPager.
            // This is a workaround for the issue #117:
            // https://github.com/ksoichiro/Android-ObservableScrollView/issues/117
            list.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.root));

            list.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		arrayList = getArguments().getParcelableArrayList("key");

		ListAdapter adapter = new ListAdapter(getActivity(), arrayList);
		list.setAdapter(adapter);

	}

}
