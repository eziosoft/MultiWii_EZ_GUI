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
package com.ezio.multiwii.mw;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ezio.multiwii.R;

public class BT {

	public boolean Connected = false;
	public static final String TAG = "MULTIWII";
	private static final boolean D = true;
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;
	private InputStream inStream = null;
	// Well known SPP UUID (will *probably* map to
	// RFCOMM channel 1 (default) if not in use);
	// see comments in onResume().
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// ==> hardcode your server's MAC address here <==
	public String address = "";

	Context context;

	public boolean ConnectionLost = false;
	public int ReconnectTry = 0;

	Handler handler;

	public BT(Context con) {
		context = con;

		GetAdapter();
	}

	public void GetAdapter() {
		if (D)
			Log.d(TAG, "+++ ON CREATE +++");

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(context, context.getString(R.string.Bluetoothisnotavailable), Toast.LENGTH_LONG).show();
			// finish();
			return;
		}

		if (!mBluetoothAdapter.isEnabled()) {
			// Toast.makeText(context,
			// "Please enable your BT and re-run this program.",
			// Toast.LENGTH_LONG).show();
			mBluetoothAdapter.enable();
			// finish();
			return;
		}

		if (D)
			Log.d(TAG, "+++ DONE IN ON CREATE, GOT LOCAL BT ADAPTER +++");
	}

	public void BTDisable() {
		try {
			mBluetoothAdapter.disable();
		} catch (Exception e) {

		}

	}

	public void GetRemoteDevice(String MAC) {
		if (D) {
			Log.d(TAG, "+ ON RESUME +");
			Log.d(TAG, "+ ABOUT TO ATTEMPT CLIENT CONNECT +");

		}

		Toast.makeText(context, context.getString(R.string.Connecting), Toast.LENGTH_LONG).show();
		// app.Speak("Connecting");

		address = MAC;

		// When this returns, it will 'know' about the server,
		// via it's MAC address.
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

		// We need two things before we can successfully connect
		// (authentication issues aside): a MAC address, which we
		// already have, and an RFCOMM channel.
		// Because RFCOMM channels (aka ports) are limited in
		// number, Android doesn't allow you to use them directly;
		// instead you request a RFCOMM mapping based on a service
		// ID. In our case, we will use the well-known SPP Service
		// ID. This ID is in UUID (GUID to you Microsofties)
		// format. Given the UUID, Android will handle the
		// mapping for you. Generally, this will return RFCOMM 1,
		// but not always; it depends what other BlueTooth services
		// are in use on your Android device.
		try {
			btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
			Log.e(TAG, "ON RESUME: Socket creation failed.", e);
			Toast.makeText(context, context.getString(R.string.Unabletoconnect), Toast.LENGTH_LONG).show();
		}

		// Discovery may be going on, e.g., if you're running a
		// 'scan for devices' search from your handset's Bluetooth
		// settings, so we call cancelDiscovery(). It doesn't hurt
		// to call it, but it might hurt not to... discovery is a
		// heavyweight process; you don't want it in progress when
		// a connection attempt is made.
		mBluetoothAdapter.cancelDiscovery();
	}

	public void Connect(String MAC) {

		// Blocking connect, for a simple client nothing else can
		// happen until a successful connection is made, so we
		// don't care if it blocks.

		if (mBluetoothAdapter.isEnabled()) {
			try {

				GetRemoteDevice(MAC);
				btSocket.connect();
				Connected = true;
				ConnectionLost = false;
				ReconnectTry = 0;
				Log.d(TAG, "BT connection established, data transfer link open.");
				Toast.makeText(context, context.getString(R.string.Connected), Toast.LENGTH_LONG).show();

				// app.Speak("Connected");

			} catch (IOException e) {
				try {
					btSocket.close();
					Connected = false;
					ConnectionLost = true;
					Toast.makeText(context, context.getString(R.string.Unabletoconnect), Toast.LENGTH_LONG).show();
					// app.Speak("Unable to connect");

				} catch (IOException e2) {
					Log.e(TAG, "ON RESUME: Unable to close socket during connection failure", e2);

				}
			}

			// Create a data stream so we can talk to server.
			if (D)
				Log.d(TAG, "+ getOutputStream  getInputStream +");

			try {
				outStream = btSocket.getOutputStream();
				inStream = btSocket.getInputStream();

			} catch (IOException e) {
				Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
			}
		}

	}

	public void Send(String out) {

		byte[] msgBuffer = out.getBytes();
		try {
			outStream.write(msgBuffer);
		} catch (IOException e) {
			Log.e(TAG, "SEND : Exception during write.", e);
		}
	}

	public void Write(byte[] arr) {
		try {
			if (Connected)
				outStream.write(arr);
		} catch (IOException e) {
			Log.e(TAG, "SEND : Exception during write.", e);
			CloseSocket();
			ConnectionLost = true;
		}
	}

	public void CloseSocket() {
		if (outStream != null) {
			try {
				outStream.flush();
			} catch (IOException e) {
				Log.e(TAG, "ON PAUSE: Couldn't flush output stream.", e);
			}
		}

		try {
			btSocket.close();
			Connected = false;

			Toast.makeText(context, context.getString(R.string.Disconnected), Toast.LENGTH_LONG).show();
			// app.Speak("Disconnected");

		} catch (Exception e2) {
			Log.e(TAG, "ON PAUSE: Unable to close socket.", e2);
		}

	}

	public int available() {
		int a = 0;

		try {
			if (Connected)
				a = inStream.available();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return a;
	}

	int Read32() {
		byte[] b = new byte[4];

		try {
			inStream.read(b, 0, 4);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Read error ", e);
		}
		return (b[0] & 0xff) + ((b[1] & 0xff) << 8) + ((b[2] & 0xff) << 16) + ((b[3] & 0xff) << 24);
	}

	public int Read16() {
		byte[] b = new byte[2];

		try {
			inStream.read(b, 0, 2);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Read error ", e);
		}

		return (b[0] & 0xff) + (b[1] << 8);
	}

	public int Read8() {
		byte[] b = new byte[1];

		try {
			inStream.read(b, 0, 1);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Read error ", e);
		}

		return b[0] & 0xff;
	}

	public byte Read() {
		byte a = 0;

		try {
			a = (byte) inStream.read();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return a;
	}

	public byte[] ReadFrame(int framesize) {
		byte[] a = new byte[framesize];
		try {
			inStream.read(a, 0, framesize);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return a;

	}

}
