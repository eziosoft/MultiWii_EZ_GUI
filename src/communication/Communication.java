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
package communication;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public abstract class Communication {
	public Handler mHandler;

	public static final String TAG = "MULTIWII"; // debug

	public boolean Connected = false;
	public String address = "";

	Context context;

	protected int mState;
	
	public long BytesSent=0;
	public long BytesRecieved=0;

	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0; // we're doing nothing
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing
													// connection
	public static final int STATE_CONNECTED = 3; // now connected to a remote
													// device

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	public Communication(Context context) {
		this.context = context;
	}

	public void SetHandler(Handler handler) {
		mHandler = handler;
		Log.d("ccc", "Communication Got Handler. SetHandler()");
	}

	/**
	 * Set the current state of the chat connection
	 * 
	 * @param state
	 *            An integer defining the current connection state
	 */
	protected synchronized void setState(int state) {
		// if (D)
		Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;

		// Give the new state to the Handler so the UI Activity can update
		if (mHandler != null)
			mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
		else
			Log.d("ccc", "setState() Handle=null error state" + " -> " + state);

	}

	/**
	 * Return the current connection state.
	 */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * Sends Message to UI via Handler
	 * 
	 * @param message
	 */
	protected void sendMessageToUI_Toast(String message) {
		// Send a failure message back to the Activity
		if (mHandler != null) {
			Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
			Bundle bundle = new Bundle();
			bundle.putString(TOAST, message);
			msg.setData(bundle);
			mHandler.sendMessage(msg);
		}
	}

	public void sendDeviceName(String deviceName) {
		// Send the name of the connected device back to the UI Activity
		if (mHandler != null) {
			Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
			Bundle bundle = new Bundle();
			bundle.putString(DEVICE_NAME, deviceName);
			msg.setData(bundle);
			mHandler.sendMessage(msg);
		}
	}

	public abstract void Enable();

	/**
	 * After connection is made set Connected=true
	 * 
	 * @param address
	 *            -address of device
	 */
	public abstract void Connect(String address, int speed);

	public abstract boolean dataAvailable();

	public abstract byte Read();

	public  void Write(byte[] arr){
		BytesSent+=arr.length;
	}

	public abstract void Close();

	public abstract void Disable();
}
