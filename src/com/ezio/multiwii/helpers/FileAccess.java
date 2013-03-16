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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class FileAccess {

	String fileName;
	File file;
	File root;
	FileWriter filewriter;
	BufferedWriter out;

	public static String ReadFile(String fileName) {
		File sdcard = Environment.getExternalStorageDirectory();

		// Get the text file
		File file = new File(sdcard, fileName);

		// Read text from file
		StringBuilder text = new StringBuilder();

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
		} catch (IOException e) {
			// You'll need to add proper error handling here
		}

		// Set the text
		return text.toString();
	}

	public FileAccess(String fileName) {

		root = Environment.getExternalStorageDirectory();
		file = new File(root, fileName);
		try {
			Log.d("plik", file.toString());
			filewriter = new FileWriter(file);
			out = new BufferedWriter(filewriter);
			Log.d("plik", "fileAccess OK");
		} catch (IOException e) {

			Log.d("plik", "fileAccess ERR");
			// Toast.makeText(context, "Can't write to file",
			// Toast.LENGTH_LONG).show();
		}

	}

	public void Write(String s) {

		if (root.canWrite()) {
			try {
				out.append(s + "\n");
				Log.d("plik", "write OK");
			} catch (IOException e) {

				Log.d("plik", "fileAccess ERR");
				// Toast.makeText(context, "Can't write to file",
				// Toast.LENGTH_LONG).show();
			}
		}

	}

	public void closeFile() {
		try {
			out.close();
			Log.d("plik", "close OK");
		} catch (IOException e) {

			Log.d("plik", "fileAccess ERR");
			// Toast.makeText(context, "Can't close the file",
			// Toast.LENGTH_LONG).show();
		}

	}
}