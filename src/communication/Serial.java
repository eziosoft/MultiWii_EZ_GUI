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
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ezio.multiwii.R;

public class Serial extends Communication{

	public Serial(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void Enable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Connect(String address) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int dataAvailable() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte Read() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void Write(byte[] arr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Disable() {
		// TODO Auto-generated method stub
		
	}



}
