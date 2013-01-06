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
package com.ezio.multiwii.radio;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.ezio.multiwii.helpers.Functions;

public class StickView extends View {

	public float x, y;
	int hh, ww;

	Paint paint = new Paint();
	Paint paint1 = new Paint();
	Paint paint2 = new Paint();
	Paint paint3 = new Paint();

	float scaledDensity = 0;

	public float InputX(float x) {
		float a = Functions.map(x, 0, ww, 1000, 2000);
		if (a > 2000)
			a = 2000;
		if (a < 1000)
			a = 1000;
		return a;
	}

	public float InputY(float y) {
		float a = Functions.map(hh - y, 0, ww, 1000, 2000);
		if (a > 2000)
			a = 2000;
		if (a < 1000)
			a = 1000;
		return a;

	}

	public void SetPosition(float xx, float yy) {
		x = (ww / 2) + ((xx - 1500) / 500) * (ww / 2);
		y = (hh / 2) - ((yy - 1500) / 500) * (hh / 2);
		invalidate();

	}

	public StickView(Context context) {
		super(context);

		init();
	}

	public StickView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	private void init() {

		paint.setAntiAlias(true);
		paint.setColor(Color.GREEN);
		paint.setStyle(Paint.Style.FILL);

		paint1.setAntiAlias(true);
		paint1.setColor(Color.BLACK);
		paint1.setStyle(Paint.Style.STROKE);

		paint2.setColor(Color.GRAY);
		paint2.setStyle(Paint.Style.FILL);

		paint3.setColor(Color.YELLOW);
		paint3.setStyle(Paint.Style.FILL);

		scaledDensity = getResources().getDisplayMetrics().scaledDensity;

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		ww = w;
		hh = h;
		super.onSizeChanged(w, h, oldw, oldh);
		SetPosition(1500, 1500);

	}

	@Override
	protected void onDraw(Canvas canvas) {

		canvas.drawColor(Color.TRANSPARENT);

		canvas.drawRect(1, 1, ww - 1, hh - 1, paint2);

		canvas.drawLine(0, hh / 2, ww, hh / 2, paint3);
		canvas.drawLine(ww / 2, 0, ww / 2, hh, paint3);

		canvas.drawCircle(ww / 2, hh / 2, 5 * scaledDensity, paint1);

		canvas.drawLine(ww / 2 - 5 * scaledDensity, hh / 2, x - 15 * scaledDensity, y, paint1);
		canvas.drawLine(ww / 2 + 5 * scaledDensity, hh / 2, x + 15 * scaledDensity, y, paint1);

		canvas.drawLine(ww / 2, hh / 2 - 5 * scaledDensity, x, y - 15 * scaledDensity, paint1);
		canvas.drawLine(ww / 2, hh / 2 + 5 * scaledDensity, x, y + 15 * scaledDensity, paint1);

		canvas.drawCircle(x, y, 15 * scaledDensity, paint);

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