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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

public class SerialCDC_ACM extends Communication {

	private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

	private SerialInputOutputManager mSerialIoManager;

	private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {

		@Override
		public void onRunError(Exception e) {
			Log.d(TAG, "Runner stopped.");
		}

		@Override
		public void onNewData(final byte[] data) {
			for (int i = 0; i < data.length; i++)
				fifo.put(Integer.valueOf(data[i]));
			Log.d("aaa", "FiFo count:" + String.valueOf(fifo.size()));

		}
	};

	private UsbManager mUsbManager;
	UsbSerialDriver mSerial;

	SimpleQueue<Integer> fifo = new SimpleQueue<Integer>();

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	public SerialCDC_ACM(Context context) {
		super(context);
		mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		Enable();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Override
	public void Enable() {
		Toast.makeText(context, "Starting Serial Other Chips", Toast.LENGTH_SHORT).show();
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
			// Toast.makeText(context, "Serial device: " + mSerial,
			// Toast.LENGTH_LONG).show();
			onDeviceStateChange();
		}
	}

	@Override
	public void Connect(String address) {

		try {
			// mSerial.setBaudRate(Integer.parseInt(address));
			mSerial.setParameters(Integer.parseInt(address), UsbSerialDriver.DATABITS_8, UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
			// setParameters(mBaudRate, mDataBits, mStopBits, mParity);
		} catch (NumberFormatException e) {
		} catch (IOException e) {
		}
		Connected = true;
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
		stopIoManager();
		if (mSerial != null) {
			try {
				mSerial.close();
			} catch (IOException e) {
				// Ignore.
			}
			// mSerial = null;
		}
		Connected = false;

		Toast.makeText(context, "Serial port disconnected", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void Disable() {

		try {
			if (mSerial != null)
				mSerial.close();
			Toast.makeText(context, "Serial port disconnected", Toast.LENGTH_SHORT).show();
			Connected = false;
		} catch (IOException e) {
			// e.printStackTrace();
		}

	}

	private void stopIoManager() {
		if (mSerialIoManager != null) {
			Log.i(TAG, "Stopping io manager ..");
			mSerialIoManager.stop();
			mSerialIoManager = null;
		}
	}

	private void startIoManager() {
		if (mSerial != null) {
			Log.i(TAG, "Starting io manager ..");
			mSerialIoManager = new SerialInputOutputManager(mSerial, mListener);
			mExecutor.submit(mSerialIoManager);
		}
	}

	private void onDeviceStateChange() {
		stopIoManager();
		startIoManager();
	}

}
