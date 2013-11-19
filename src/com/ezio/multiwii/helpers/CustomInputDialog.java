/*  MultiWii EZ-GUI
    Copyright (C) <2012>  Bartosz Szczygiel (eziosoft)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ezio.multiwii.helpers;

import java.text.NumberFormat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ezio.multiwii.R;

public class CustomInputDialog {

	public static void ShowCustomDialogOnClick(final View vv, Context context) {

		// min;max;step;decimalPlaces

		String tagValues[] = vv.getTag().toString().split(";");

		for (String string : tagValues) {
			Log.d("aaa", string);
		}

		final float min = Float.parseFloat(tagValues[0]);
		final float max = Float.parseFloat(tagValues[1]);
		final float step = Float.parseFloat(tagValues[2]);
		final int decimalPlaces = Integer.parseInt(tagValues[3]);

		final NumberFormat format = NumberFormat.getNumberInstance();
		format.setMinimumFractionDigits(decimalPlaces);
		format.setMaximumFractionDigits(decimalPlaces);
		format.setGroupingUsed(false);

		// get prompts.xml view
		LayoutInflater layoutInflater = LayoutInflater.from(context);

		View promptView = layoutInflater.inflate(R.layout.custom_input_dialog, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// set prompts.xml to be the layout file of the alertdialog builder
		alertDialogBuilder.setView(promptView);

		final EditText ETValue = (EditText) promptView.findViewById(R.id.editTextCustomDialog);

		ETValue.setText(String.valueOf(((EditText) vv).getText().toString()));
		final String OldValue = String.valueOf(((EditText) vv).getText().toString());

		Button minus = (Button) promptView.findViewById(R.id.buttonCustomDialogMinus);
		Button plus = (Button) promptView.findViewById(R.id.buttonCustomDialogPlus);
		TextView info = (TextView) promptView.findViewById(R.id.textViewCustomDialogInfo);

		if (tagValues.length > 4) {
			final String[] spinnerNames = tagValues[4].split("/");
			final String[] spinnerValues = tagValues[5].split("/");

			Spinner spinner = (Spinner) promptView.findViewById(R.id.spinnerCustomInputDialog);
			spinner.setVisibility(View.VISIBLE);
			((TextView) promptView.findViewById(R.id.TextViewCustomInputDialogInfoSpinner)).setVisibility(View.VISIBLE);
			ArrayAdapter aa = new ArrayAdapter(context, android.R.layout.simple_spinner_item, spinnerNames);
			spinner.setAdapter(aa);

			int i;
			for (i = spinnerValues.length - 1; i >= 0; i--) {
				if (ETValue.getText().toString().equals(spinnerValues[i])) {
					break;
				}
			}

			spinner.setSelection(i, true);

			if (Float.parseFloat(((EditText) ETValue).getText().toString().replace(",", ".")) < max && Float.parseFloat(((EditText) ETValue).getText().toString().replace(",", ".")) > min) {
				spinner.setSelection(0, true);
			}

			if (tagValues[6].equals("override")) {
				((TextView) promptView.findViewById(R.id.TextViewCustomInputDialogInfoSpinner)).setText(context.getString(R.string.OverrideBy));
			} else {
				((TextView) promptView.findViewById(R.id.TextViewCustomInputDialogInfoSpinner)).setText(tagValues[6]);
			}

			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				int count = 0;

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
					if (count >= 1) {
						ETValue.setText(spinnerValues[position]);
					}
					count++;
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
		} else {
			((TextView) promptView.findViewById(R.id.TextViewCustomInputDialogInfoSpinner)).setVisibility(View.GONE);
			((Spinner) promptView.findViewById(R.id.spinnerCustomInputDialog)).setVisibility(View.GONE);
		}

		info.setText(context.getString(R.string.Min) + "=" + tagValues[0] + "  " + context.getString(R.string.Max) + "=" + tagValues[1]);

		minus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				float CurrentValue = Float.parseFloat(((EditText) ETValue).getText().toString().replace(",", "."));
				CurrentValue -= step;

				if (CurrentValue > max)
					CurrentValue = max;
				if (CurrentValue < min)
					CurrentValue = min;

				ETValue.setText(format.format(CurrentValue));

			}
		});

		plus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				float CurrentValue = Float.parseFloat(((EditText) ETValue).getText().toString().replace(",", "."));
				CurrentValue += step;

				if (CurrentValue > max)
					CurrentValue = max;
				if (CurrentValue < min)
					CurrentValue = min;

				ETValue.setText(format.format(CurrentValue));

			}
		});

		// setup a dialog window
		alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if (!ETValue.getText().toString().equals("")) {
					// get user input and set it to result

					((EditText) vv).setText(ETValue.getText());
				} else {
					((EditText) vv).setText(OldValue);
				}
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		// create an alert dialog
		AlertDialog alertD = alertDialogBuilder.create();

		alertD.show();
	}
}
