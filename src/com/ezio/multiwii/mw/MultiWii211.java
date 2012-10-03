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

public class MultiWii211 extends MultiWii210 {

	public MultiWii211(BT b) {
		super(b);
	}

	@Override
	public void evaluateCommand(byte cmd, int dataSize) {

		int i;
		int icmd = (int) (cmd & 0xFF);
		switch (icmd) {

		case MSP_DEBUGMSG:
			while (dataSize-- > 0) {
				char c = (char) read8();
				if (c != 0) {
					DebugMSG += c;
				}
			}
			break;
		}

		super.evaluateCommand(cmd, dataSize);
	}

	// TODO add MSP_DEBUGMSG in Request

}
