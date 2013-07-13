package com.ezio.multiwii.dashboard;

import android.os.Bundle;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockActivity;
import com.ezio.multiwii.R;

public class Dashboard4Activity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dashboard4_layout);

	}

}
