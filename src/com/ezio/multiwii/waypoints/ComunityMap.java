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
package com.ezio.multiwii.waypoints;

/*
 * 	if (app.DataSent == false) {
 HttpCli h = new HttpCli();
 h.execute(String.valueOf(location.getLatitude()) + ";"
 + String.valueOf(location.getLongitude()));
 app.DataSent = true;
 }
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.ezio.multiwii.helpers.MyBase64;

public class ComunityMap extends AsyncTask {

	NumberFormat format = new DecimalFormat("0.############################################################");
	Context context;

	public ComunityMap(Context context) {
		this.context = context;
	}

	public void send(double selectedLatitude, double selectedLongitude, String nick, String description) {

		String data = format.format(selectedLatitude / 1e6) + ";" + format.format(selectedLongitude / 1e6) + ";" + MyBase64.encode(nick.getBytes()) + ";" + MyBase64.encode(description.getBytes());
		try {
			execute(data);
			Log.d("aaa", "Added to comunity map");
			Toast.makeText(context, "Point added to map", Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Log.d("aaa", "Comunity map error " + e.getMessage());
			Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();

		}
	}

	private void executeHttpGet(String data) throws Exception {
		BufferedReader in = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI("http://ezio.ovh.org/multiwii.php?a=" + data));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			String page = sb.toString();
			System.out.println(page);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected Object doInBackground(Object... params) {
		try {
			executeHttpGet((String) params[0]);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}
}
