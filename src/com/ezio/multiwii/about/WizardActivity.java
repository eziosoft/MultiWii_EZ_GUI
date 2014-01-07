package com.ezio.multiwii.about;

import android.app.Activity;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.view.View;
import android.widget.ViewFlipper;

import com.ezio.multiwii.R;

public class WizardActivity extends Activity {

	private final int NbPages = 2;
	ViewFlipper VF;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wizard_layout);

		VF = (ViewFlipper) findViewById(R.id.viewFlipper1);
		VF.setInAnimation(this, android.R.anim.slide_in_left);
		VF.setOutAnimation(this, android.R.anim.slide_out_right);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public void NextOnClick(View v) {

		if (VF.getDisplayedChild() == NbPages - 1) {
			finish();
		} else {
			VF.showNext();
		}
	}

}
