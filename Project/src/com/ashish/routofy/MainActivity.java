package com.ashish.routofy;

import java.util.ArrayList;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.ashish.routofy.observablelist.CacheFragmentStatePagerAdapter;
import com.ashish.routofy.observablelist.ObservableListView;
import com.ashish.routofy.observablelist.ObservableScrollViewCallbacks;
import com.ashish.routofy.observablelist.ScrollState;
import com.ashish.routofy.observablelist.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

@SuppressLint({ "ResourceAsColor", "NewApi" })
public class MainActivity extends ActionBarActivity implements
		ObservableScrollViewCallbacks {

	ViewPager pager;
	ProgressDialog progressDialog;
	PagerSlidingTabStrip strip;
	LinearLayout header;
	RelativeLayout errorLayout;
	ArrayList<JsonObjectParse> arrayListObjects;
	ArrayList<ArrayList<JsonObjectParse>> finalArraylist;
	private ArrayList<Integer> userIds;
	Toolbar toolbar;
	private int mBaseTranslationY;

	String[] libs = { "Android support library v7", "PagerSlidingTabStrip",
			"Volley Library", "Android-ObservableScrollView",
			"NineOldAndroids(required by ObservableScrollView)", };
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		pager = (ViewPager) findViewById(R.id.pager);
		strip = (PagerSlidingTabStrip) findViewById(R.id.strip);
		errorLayout = (RelativeLayout) findViewById(R.id.error_layout);
		header = (LinearLayout) findViewById(R.id.main_laout);
		toolbar = (Toolbar) findViewById(R.id.toolbar_toolbar);

		findViewById(R.id.aboutbtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(v.getContext(),
						R.style.MyCustomThemeDialog);
				builder.setTitle("Libraries Used");
				builder.setItems(libs, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
				ScaleAnimation anim = new ScaleAnimation(0, 100, 0, 100, 0, 0);
			}
		});

		pager.setPageTransformer(false, new viewpagerTransformer(R.id.list));

		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle(null);

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Loading...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.hide();

		errorLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadData();
			}
		});

		strip.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				propagateToolbarState(toolbarIsShown());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		loadData();
	}

	private class MyAdapter extends CacheFragmentStatePagerAdapter {

		private int mScrollY;

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		public void setScrollY(int scrollY) {
			mScrollY = scrollY;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return userIds.get(position) + "";
		}

		@Override
		public int getCount() {
			return userIds.size();
		}

		@Override
		protected Fragment createItem(int position) {
			Bundle b = new Bundle();
			b.putParcelableArrayList("key", finalArraylist.get(position));
			if (0 < mScrollY) {
				b.putInt(ListFragment.ARG_INITIAL_POSITION, 1);
			}
			Fragment f = ListFragment.newInstance(b);
			return f;
		}

	}

	private void loadData() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null
				&& activeNetwork.isConnectedOrConnecting();
		if (!isConnected) {
			showErrorView();
			Toast.makeText(getApplicationContext(),
					"No internet connection detected", Toast.LENGTH_LONG)
					.show();
		} else {
			errorLayout.setVisibility(View.GONE);
			header.setVisibility(View.VISIBLE);
			progressDialog.show();

			String url = "http://jsonplaceholder.typicode.com/posts";
			JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
					new Listener<JSONArray>() {

						@Override
						public void onResponse(JSONArray arg0) {
							progressDialog.dismiss();
							arrayListObjects = JsonObjectParse
									.parseJsonArray(arg0);
							convertArrayListToViewPagerData();
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError arg0) {
							progressDialog.dismiss();
							showErrorView();
						}
					});

			VolleySingletonClass.getInstance(getApplicationContext())
					.addToRequestQueue(jsonArrayRequest);
		}
	}

	protected void convertArrayListToViewPagerData() {
		finalArraylist = new ArrayList<ArrayList<JsonObjectParse>>();
		userIds = new ArrayList<Integer>();
		for (int i = 0; i < arrayListObjects.size(); i++) {
			int userid = arrayListObjects.get(i).userId;
			if (!userIds.contains(userid)) {
				userIds.add(userid);
			}
		}
		for (int i = 0; i < userIds.size(); i++) {
			int userIdtoMatch = userIds.get(i);

			ArrayList<JsonObjectParse> arraylist1 = new ArrayList<>();
			for (int j = 0; j < arrayListObjects.size(); j++) {
				int userid = arrayListObjects.get(j).userId;
				if (userIdtoMatch == userid) {
					arraylist1.add(arrayListObjects.get(j));
				}
			}
			finalArraylist.add(arraylist1);
		}

		Log.v("as", finalArraylist.size() + "");
		populateDataInViewPager();
	}

	private void showErrorView() {
		header.setVisibility(View.GONE);
		errorLayout.setVisibility(View.VISIBLE);
	}

	private void populateDataInViewPager() {
		makeSizeOfDataTo5();

		adapter = new MyAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		strip.setShouldExpand(true);
		strip.setViewPager(pager);

		propagateToolbarState(toolbarIsShown());
	}

	private void makeSizeOfDataTo5() {
		if (userIds.size() > 5) {
			userIds.remove(userIds.size() - 1);
			makeSizeOfDataTo5();
		} else {
			return;
		}
	}

	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll,
			boolean dragging) {
		if (dragging) {
			int toolbarHeight = toolbar.getHeight();
			float currentHeaderTranslationY = ViewHelper
					.getTranslationY(header);
			if (firstScroll) {
				if (-toolbarHeight < currentHeaderTranslationY) {
					mBaseTranslationY = scrollY;
				}
			}
			float headerTranslationY = ScrollUtils.getFloat(
					-(scrollY - mBaseTranslationY), -toolbarHeight, 0);
			ViewPropertyAnimator.animate(header).cancel();
			ViewHelper.setTranslationY(header, headerTranslationY);
		}
	}

	@Override
	public void onDownMotionEvent() {

	}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {
		mBaseTranslationY = 0;

		Fragment fragment = getCurrentFragment();
		if (fragment == null) {
			return;
		}
		View view = fragment.getView();
		if (view == null) {
			return;
		}

		int toolbarHeight = toolbar.getHeight();
		final ObservableListView listView = (ObservableListView) view
				.findViewById(R.id.list);
		if (listView == null) {
			return;
		}
		int scrollY = listView.getCurrentScrollY();
		if (scrollState == ScrollState.DOWN) {
			showToolbar();
		} else if (scrollState == ScrollState.UP) {
			if (toolbarHeight <= scrollY) {
				hideToolbar();
			} else {
				showToolbar();
			}
		} else {
			// Even if onScrollChanged occurs without scrollY changing, toolbar
			// should be adjusted
			if (toolbarIsShown() || toolbarIsHidden()) {
				// Toolbar is completely moved, so just keep its state
				// and propagate it to other pages
				propagateToolbarState(toolbarIsShown());
			} else {
				// Toolbar is moving but doesn't know which to move:
				// you can change this to hideToolbar()
				showToolbar();
			}
		}
	}

	private Fragment getCurrentFragment() {
		return adapter.getItemAt(pager.getCurrentItem());
	}

	private void propagateToolbarState(boolean isShown) {
		int toolbarHeight = toolbar.getHeight();

		// Set scrollY for the fragments that are not created yet
		adapter.setScrollY(isShown ? 0 : toolbarHeight);

		// Set scrollY for the active fragments
		for (int i = 0; i < adapter.getCount(); i++) {
			// Skip current item
			if (i == pager.getCurrentItem()) {
				continue;
			}

			// Skip destroyed or not created item
			Fragment f = adapter.getItemAt(i);
			if (f == null) {
				continue;
			}

			View view = f.getView();
			if (view == null) {
				continue;
			}
			ObservableListView listView = (ObservableListView) view
					.findViewById(R.id.list);
			if (isShown) {
				// Scroll up
				if (0 < listView.getCurrentScrollY()) {
					listView.setSelection(0);
				}
			} else {
				// Scroll down (to hide padding)
				if (listView.getCurrentScrollY() < toolbarHeight) {
					listView.setSelection(1);
				}
			}
		}
	}

	private boolean toolbarIsShown() {
		return ViewHelper.getTranslationY(header) == 0;
	}

	private boolean toolbarIsHidden() {
		return ViewHelper.getTranslationY(header) == -toolbar.getHeight();
	}

	private void showToolbar() {
		float headerTranslationY = ViewHelper.getTranslationY(header);
		if (headerTranslationY != 0) {
			ViewPropertyAnimator.animate(header).cancel();
			ViewPropertyAnimator.animate(header).translationY(0)
					.setDuration(200).start();
		}
		propagateToolbarState(true);
	}

	private void hideToolbar() {
		float headerTranslationY = ViewHelper.getTranslationY(header);
		int toolbarHeight = toolbar.getHeight();
		if (headerTranslationY != -toolbarHeight) {
			ViewPropertyAnimator.animate(header).cancel();
			ViewPropertyAnimator.animate(header).translationY(-toolbarHeight)
					.setDuration(200).start();
		}
		propagateToolbarState(false);
	}

}
