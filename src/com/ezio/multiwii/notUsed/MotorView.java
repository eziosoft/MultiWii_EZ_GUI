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
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MotorView extends View {

	int				hh, ww;

	Paint			paint			= new Paint();
	Paint			paint1			= new Paint();
	Paint			paint2			= new Paint();
	Paint			paint3			= new Paint();

	int				motors			= 4;
	float			confX			= 3*(float) (2 * Math.PI / motors) / 2;	;

	public float	MotorsPower[]	= { 0, 0, 0, 0, 0, 0, 0, 0 };

	public void SetMotorsPower(float[] power) {
		MotorsPower = power;
		invalidate();
	}

	public void SetMotorsCount(int m, boolean X) {
		motors = m;

		if (X) {
			confX = 3*(float) (2 * Math.PI / motors) / 2;
		}
		else {
			confX = 2*(float) (2 * Math.PI / motors) / 2;
		}
	}

	public MotorView(Context context) {
		super(context);

		init();
	}

	public MotorView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	private void init() {

		paint.setAntiAlias(true);
		paint.setColor(Color.GREEN);
		paint.setStyle(Paint.Style.FILL);

		paint1.setAntiAlias(true);
		paint1.setColor(Color.RED);
		paint1.setStyle(Paint.Style.STROKE);
		paint1.setStrokeWidth(1);
		paint1.setTextSize(25);

		paint2.setAntiAlias(true);
		paint2.setColor(Color.GRAY);
		paint2.setStyle(Paint.Style.FILL);

		paint3.setAntiAlias(true);
		paint3.setColor(0xFFB400);
		paint3.setStyle(Paint.Style.FILL);
		paint3.setStrokeWidth(5);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		ww = w;
		hh = h;
		super.onSizeChanged(w, h, oldw, oldh);

	}

	@Override
	protected void onDraw(Canvas canvas) {

		canvas.drawColor(Color.TRANSPARENT);

		int power = 2000;
		int j = 0;
		for (float i = confX; i < 2 * Math.PI + confX-  Math.PI / motors ; i += 2 * Math.PI / motors) {
			float xa = (float) Math.cos(i) * 150 + ww / 2;
			float ya = (float) Math.sin(i) * 150 + hh / 2;

			// canvas.drawRect(xa,ya,xa+15,ya+15, paint);
			canvas.drawCircle(xa, ya, MotorsPower[j] / 40, paint);
			canvas.drawLine(ww / 2, hh / 2, xa, ya, paint3);

			canvas.drawCircle(xa, ya, MotorsPower[j] / 40, paint);
			canvas.drawText("M"+String.valueOf(j+1)+"="+String.valueOf(MotorsPower[j]), xa - paint1.measureText(String.valueOf(MotorsPower[j])) / 2, ya, paint1);
			j++;
		}

		canvas.drawCircle(ww / 2, hh / 2, 50, paint3);

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
}
