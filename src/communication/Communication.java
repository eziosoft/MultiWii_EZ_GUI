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

public abstract class Communication {
	public static final String TAG = "MULTIWII"; // debug

	public boolean Connected = false;
	public String address = "";

	public boolean ConnectionLost = false;
	public int ReconnectTry = 0;

	Context context;

	public Communication(Context context) {
		this.context = context;
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

	public abstract void Write(byte[] arr);

	public abstract void Close();

	public abstract void Disable();
}
