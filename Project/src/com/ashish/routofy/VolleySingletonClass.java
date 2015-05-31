package com.ashish.routofy;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingletonClass {

	private static VolleySingletonClass mInstance;
	private RequestQueue mRequestQueue;
	private static Context mCtx;

	private VolleySingletonClass(Context context) {
		mCtx = context;
		mRequestQueue = getRequestQueue();
	}

	public static synchronized VolleySingletonClass getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new VolleySingletonClass(context);
		}
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley
					.newRequestQueue(mCtx.getApplicationContext());
		}
		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req) {
		getRequestQueue().add(req);
	}

}
