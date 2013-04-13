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
package communication;

import java.io.IOException;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

public class SerialNew extends Communication {

	private UsbManager mUsbManager;
	UsbSerialDriver mSerial;

	SimpleQueue<Integer> fifo = new SimpleQueue<Integer>();

	boolean loopStop = false;

	public SerialNew(Context context) {
		super(context);
		mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		Enable();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Override
	public void Enable() {
		mSerial = UsbSerialProber.acquire(mUsbManager);
		Log.d(TAG, "Resumed, mSerialDevice=" + mSerial);
		if (mSerial == null) {
			Toast.makeText(context, "No serial device.", Toast.LENGTH_LONG).show();
		} else {
			try {
				mSerial.open();

			} catch (IOException e) {
				Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
				Toast.makeText(context, "Error opening device: " + e.getMessage(), Toast.LENGTH_LONG).show();
				Connected = false;
				try {
					mSerial.close();
					Connected = false;
				} catch (IOException e2) {
					// Ignore.
				}
				mSerial = null;
				return;
			}
			Toast.makeText(context, "Serial device: " + mSerial, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void Connect(String address) {

		try {
			mSerial.setBaudRate(Integer.parseInt(address));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connected = true;
		startMainLoop();
	}

	@Override
	public boolean dataAvailable() {
		return !fifo.isEmpty();
	}

	@Override
	public byte Read() {
		return (byte) (fifo.get() & 0xff);

	}

	@Override
	public void Write(byte[] arr) {

		if (Connected) {
			try {
				mSerial.write(arr, 0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// Toast.makeText(context,
			// "Serial port Write error - not connected",
			// Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void Close() {
		Connected = false;
		loopStop = true;
		try {
			mSerial.close();
			Toast.makeText(context, "Serial port disconnected", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void Disable() {
		Connected = false;
		loopStop = true;
		try {
			mSerial.close();
			Toast.makeText(context, "Serial port disconnected", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void readToBuffer() {
		byte[] rbuf = new byte[4096];

		if (Connected) {

			int len = 0;
			try {
				len = mSerial.read(rbuf, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Log.d("aaa", "ReadtoBuffer:" + String.valueOf(rbuf));
			for (int i = 0; i < len; i++)
				fifo.put(Integer.valueOf(rbuf[i]));
			// Log.d("aaa", "FiFo count:" + String.valueOf(fifo.size()));
		}
	}

	private void startMainLoop() {
		new Thread(mLoop).start();
	}

	private Runnable mLoop = new Runnable() {
		@Override
		public void run() {
			while (!loopStop) {// this is the main loop for transferring
				readToBuffer();
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
}
