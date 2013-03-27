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

import java.util.LinkedList;

import jp.ksksue.driver.serial.FTDriver;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.widget.Toast;

public class Serial extends Communication {

	// [FTDriver] Permission String
	private static final String ACTION_USB_PERMISSION = "jp.ksksue.tutorial.USB_PERMISSION";

	FTDriver mSerial;

	LinkedList<Integer> fifo = new LinkedList<Integer>();

	public Serial(Context context) {
		super(context);
		Enable();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Override
	public void Enable() {
		Toast.makeText(context, "Starting Serial...", Toast.LENGTH_SHORT).show();
		// [FTDriver] Create Instance
		mSerial = new FTDriver((UsbManager) context.getSystemService(Context.USB_SERVICE));

		// [FTDriver] setPermissionIntent() before begin()
		PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
		mSerial.setPermissionIntent(permissionIntent);

		// listen for new devices
		IntentFilter filter = new IntentFilter();
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		((Context) context).registerReceiver(mUsbReceiver, filter);

	}

	@Override
	public void Connect(String address) {
		// [FTDriver] Open USB Serial
		if (mSerial.begin(FTDriver.BAUD115200)) {
			Connected = true;
			Toast.makeText(context, "Serial connected", Toast.LENGTH_SHORT).show();

		} else {
			Connected = false;
			Toast.makeText(context, "Serial cannot connect", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean dataAvailable() {
		readToBuffer();
		return !fifo.isEmpty();
	}

	@Override
	public byte Read() {
		readToBuffer();
		return (byte) (fifo.removeFirst() & 0xff);
	}

	private void readToBuffer() {
		Connected = mSerial.isConnected();
		// [FTDriver] Create Read Buffer
		byte[] rbuf = new byte[4096]; // 1byte <--slow-- [Transfer Speed]
										// --fast-->
										// 4096 byte
		if (mSerial.isConnected()) {
			// [FTDriver] Read from USB Serial
			int len = mSerial.read(rbuf);

			for (int i = 0; i < len; i++)
				fifo.add(Integer.valueOf(rbuf[i]));
		}
	}

	@Override
	public void Write(byte[] arr) {
		Connected = mSerial.isConnected();

		if (mSerial.isConnected()) {
			mSerial.write(arr, arr.length);
		} else {
			Toast.makeText(context, "Write error - not connected", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void Close() {
		Connected = false;
		mSerial.end();
		Toast.makeText(context, "disconnect", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void Disable() {
		Connected = false;
		mSerial.end();
		context.unregisterReceiver(mUsbReceiver);
		Toast.makeText(context, "disconnect", Toast.LENGTH_SHORT).show();

	}

	// BroadcastReceiver when insert/remove the device USB plug into/from a USB
	// port
	BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
				Toast.makeText(context, "USB_DEVICE_ATTACHED", Toast.LENGTH_LONG).show();

				// mSerial.usbAttached(intent);
				// mSerial.begin(SERIAL_BAUDRATE);
				// mainloop();

			} else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				Toast.makeText(context, "USB_DEVICE_DETACHED", Toast.LENGTH_LONG).show();
				Connected = false;
				// mSerial.usbDetached(intent);
				// mSerial.end();
				// mStop = true;
			}
		}
	};
}
