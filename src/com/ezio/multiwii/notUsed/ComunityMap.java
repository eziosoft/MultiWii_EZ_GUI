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
package com.ezio.multiwii.notUsed;

/*
 * 	if (app.DataSent == false) {
 HttpCli h = new HttpCli();
 h.execute(String.valueOf(location.getLatitude()) + ";"
 + String.valueOf(location.getLongitude()));
 app.DataSent = true;
 }
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class ComunityMap extends AsyncTask {

	
	public ComunityMap(double selectedLatitude, double selectedLongitude, String nick)
	{
		NumberFormat format = new DecimalFormat("0.############################################################"); // used
		// to
		// avoid

		String data= format.format(selectedLatitude)+";"+format.format(selectedLongitude)+";"+nick;
		
		try {
			executeHttpGet(data);
			Log.d("aaa","Added to comunity map");
		} catch (Exception e) {
			Log.d("aaa","Comunity map error "+e.getMessage());
			
		}
	}
	
	private void executeHttpGet(String data) throws Exception {
		BufferedReader in = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI("http://ezio.ovh.org/multiwii.php?a=" + data));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
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
				} catch (IOException e) {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
