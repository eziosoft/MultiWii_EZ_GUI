package com.ezio.multiwii;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;

public class InfoActivity extends SherlockActivity {

	App app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);

		app = (App) getApplication();
	}

	public void DonateOnClick(View v) {

		app.DonateButtonPressed++;
		app.SaveSettings(true);
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=EZ88MU3VKXSGG&lc=GB&item_name=MultiWiiAllinOne&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted"));
		startActivity(browserIntent);
	}

	public void CloseOnClick(View v) {
		finish();
	}
}
