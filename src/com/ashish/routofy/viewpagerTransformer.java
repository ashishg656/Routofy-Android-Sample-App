package com.ashish.routofy;

import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class viewpagerTransformer implements PageTransformer {

	private int id;
	private int border = 0;
	private float speed = 0.2f;

	public viewpagerTransformer(int id) {
		this.id = id;
	}

	@SuppressLint("NewApi")
	@Override
	public void transformPage(View view, float position) {
		View parallaxView = view.findViewById(id);

		if (position > -1 && position < 1) {
			float width = parallaxView.getWidth();
			parallaxView.setTranslationX(-(position * width * speed));
			float sc = ((float) view.getWidth() - border) / view.getWidth();
			if (position == 0) {
				view.setScaleX(1);
				view.setScaleY(1);
			} else {
				view.setScaleX(sc);
				view.setScaleY(sc);
			}
		}

	}
}
