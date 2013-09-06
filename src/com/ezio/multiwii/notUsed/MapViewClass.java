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

import android.content.Context;
import android.graphics.Canvas;

import com.google.android.maps.MapView;

public class MapViewClass extends MapView {

	float	azimuth	= 90;
	boolean	compass	= true;

	public MapViewClass(Context arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		if (compass) {
			// rotate the canvas with the pivot on the center of the screen
			canvas.rotate(-azimuth, getWidth() * 0.5f, getHeight() * 0.5f);
			super.dispatchDraw(canvas);
			canvas.restore();
		}
	}

}
