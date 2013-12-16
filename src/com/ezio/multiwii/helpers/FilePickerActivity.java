package com.ezio.multiwii.helpers;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ezio.multiwii.R;

public class FilePickerActivity extends Activity {

	Spinner SPFiles;
	String extension = "";

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.file_picker_layout);
		getWindow().setLayout(LayoutParams.MATCH_PARENT /* width */, LayoutParams.WRAP_CONTENT /* height */);

		SPFiles = (Spinner) findViewById(R.id.spinnerFiles);

		extension = "mission";
		loadFilesNamesToSpinner(extension);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	void FinishAndSendFileName(String fileName) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra("fileName", fileName);
		setResult(RESULT_OK, returnIntent);
		finish();

		// jezeli nie ma danych zwrotnych
		// Intent returnIntent = new Intent();
		// setResult(RESULT_CANCELED, returnIntent);
		// finish();
	}

	private void loadFilesNamesToSpinner(String extension) {
		File folder = new File(Environment.getExternalStorageDirectory() + "/MultiWiiLogs");
		boolean success = false;
		if (!folder.exists()) {
			success = folder.mkdir();
		} else {
			success = true;
		}

		if (success) {
			File sdCardRoot = Environment.getExternalStorageDirectory();
			File yourDir = new File(sdCardRoot, "MultiWiiLogs");
			ArrayList<String> l = new ArrayList<String>();

			if (yourDir.listFiles() != null) {
				for (File f : yourDir.listFiles()) {
					if (f.isFile())
						if (f.getName().contains(extension))
							l.add(f.getName().replace("." + extension, ""));
				}
			}

			ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, l);

			SPFiles.setAdapter(aa);
		}
	}

	public void OpenSelectedFileOnClick(View v) {
		if (SPFiles.getCount() > 0)
			FinishAndSendFileName(Environment.getExternalStorageDirectory() + "/MultiWiiLogs/" + SPFiles.getSelectedItem().toString() + "." + extension);
	}

}
