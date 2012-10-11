package com.ezio.multiwii.Main;

import java.util.ArrayList;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

public class MyPagerAdapter extends PagerAdapter {
	private ArrayList<View> views;
	private String[] titles = new String[0];
	Context context;

	public MyPagerAdapter(Context context) {
		this.context = context;
		views = new ArrayList<View>();
		// views.add((View)context.findview);
		// views.add(new TextViewPage(context));
		// views.add(new ListView2Page(context));
		// views.add(new ButtonPage(context));
	}

	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		return titles[position];
	}

	public void SetTitles(String[] titles) {
		this.titles = titles;
	}

	public void AddView(View v) {
		views.add(v);
	}

	@Override
	public void destroyItem(View view, int arg1, Object object) {
		((ViewPager) view).removeView((LinearLayout) object);
	}

	@Override
	public void finishUpdate(View arg0) {

	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public Object instantiateItem(View view, int position) {

		View myView = views.get(position);
		((ViewPager) view).addView(myView);
		return myView;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {

	}

}
