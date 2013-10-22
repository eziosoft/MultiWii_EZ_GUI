///*  MultiWii EZ-GUI
//    Copyright (C) <2012>  Bartosz Szczygiel (eziosoft)
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package com.ezio.multiwii.about;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//
//import com.ezio.multiwii.R;
//import com.ezio.multiwii.app.App;
//
//public class InfoActivity extends Activity {
//
//	App app;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.info_layout);
//
//		app = (App) getApplication();
//	}
//
//	public void DonateOnClick(View v) {
//
//		app.DonateButtonPressed++;
////		app.SaveSettings(true);
////		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=EZ88MU3VKXSGG&lc=GB&item_name=MultiWiiAllinOne&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted"));
////		startActivity(browserIntent);
//	}
//
//	public void CloseOnClick(View v) {
//		finish();
//	}
//}
