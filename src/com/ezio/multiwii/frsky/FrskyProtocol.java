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
package com.ezio.multiwii.frsky;

import java.util.ArrayList;

import android.util.Log;

import communication.BT1;
import communication.Communication;

public class FrskyProtocol {

	public FrskyHubProtocol frskyHubProtocol = new FrskyHubProtocol();

	public int Analog1 = 0;
	public int Analog2 = 0;
	public int RxRSSI = 0;
	public int TxRSSI = 0;

	int f = 0;
	int frame[] = new int[11];

	BT1 bt;

	public FrskyProtocol(Communication bTFrsky) {
		bt = (BT1) bTFrsky;
	}

	public void ProcessSerialData(boolean appLogging) {
		while (bt.dataAvailable() > 0) {
			int b = bt.Read(); // was bt.Read8()

			if (b == 0x7e) {
				frame[f] = b;
				if (frame[0] == 0x7e && frame[10] == 0x7e) {

					if (frame[1] == 0xFE) {
						evaluateCommandFE(frame);
					}

					if (frame[1] == 0xFD) {
						evaluateCommandFD(frame);
					}

					frame = new int[11];
				}
				f = 0;
			}

			if (b == 0x7d)
				b = (bt.Read() ^ 0x20); // was bt.Read8()

			frame[f] = b;
			f++;
			if (f > 10)
				f = 0;
		}
	}

	void evaluateCommandFE(int[] frame) {
		Analog1 = frame[2];
		Analog2 = frame[3];
		TxRSSI = frame[4];
		RxRSSI = frame[5] / 2;
	}

	ArrayList<Integer> buffor = new ArrayList<Integer>();

	void evaluateCommandFD(int[] frame) {

		int validBytes = frame[2];

		int[] dataInFrame = new int[validBytes];

		for (int i = 4; i < 4 + validBytes; i++) {
			dataInFrame[i - 4] = frame[i];
		}

		int d5 = 0;
		for (int d : dataInFrame) {
			if (d5 == 1) {
				d = d ^ 0x60;
				d5 = 0;
			}

			if (d == 0x5d) {
				d5 = 1;

			}

			if (d5 == 0)
				buffor.add(d);
		}

		while (buffor.size() > 4) {
			if (buffor.get(0) == 0x5e && buffor.get(1) != 0x5e) {
				int[] hubFrame = new int[4];
				hubFrame[0] = buffor.get(0);
				hubFrame[1] = buffor.get(1);
				hubFrame[2] = buffor.get(2);
				hubFrame[3] = buffor.get(3);

				buffor.remove(0);
				buffor.remove(0);
				buffor.remove(0);
				buffor.remove(0);

				try {
					frskyHubProtocol.ProcessFrame(hubFrame);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Log.d("frsky", getHex(frame) + "->bytes " + String.valueOf(validBytes) + "   b=" + getHex(buffor.toArray()) + "    f=" + getHex(dataInFrame) + "    hubFrame=" + getHex(hubFrame));

			} else {

				buffor.remove(0);
			}

			// Log.d("frsky",
			// getHex(frame) + "->bytes " + String.valueOf(validBytes)
			// + "   b=" + getHex(buffor.toArray()) + "    f="
			// + getHex(dataInFrame));
		}

	}

	static final String HEXES = "0123456789ABCDEF";

	public static String getHex(int[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final int b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

	public static String getHex(Object[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final Object b : raw) {
			hex.append(HEXES.charAt(((Integer) b & 0xF0) >> 4)).append(HEXES.charAt(((Integer) b & 0x0F)));
		}
		return hex.toString();
	}

}
