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
package com.ezio.multiwii.dashboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PitchRollCircleView extends View {
	private Paint paint1 = new Paint();
	private Paint paint2 = new Paint();

	public float x, y;
	int color = Color.GREEN;
	int hh = 80, ww = 80;

	static float scaledDensity = 0;

	public void SetRollPitch(float Roll, float Pitch) {
		x = Roll;
		y = Pitch;

		invalidate();

		setMinimumHeight(80);
		setMinimumWidth(80);
	}

	public PitchRollCircleView(Context context) {
		super(context);

		init();

	}

	public PitchRollCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	private void init() {

		SetColor(color);

		scaledDensity = getResources().getDisplayMetrics().scaledDensity;
	}

	public void SetColor(int c) {
		color = c;
		paint2.setAntiAlias(true);
		paint2.setColor(color);
		paint2.setStyle(Paint.Style.FILL);

		paint1.setAntiAlias(true);
		paint1.setColor(color);
		paint1.setStyle(Paint.Style.STROKE);
		paint1.setStrokeWidth(2f * scaledDensity);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		ww = w;
		hh = h;
		init();
		super.onSizeChanged(w, h, oldw, oldh);

	}

	@Override
	protected void onDraw(Canvas canvas) {

		canvas.drawColor(Color.TRANSPARENT);

		canvas.drawCircle(ww / 2, hh / 2, hh / 2 - 2 * scaledDensity, paint1);
		canvas.drawCircle(ww / 2, hh / 2, 25 * scaledDensity, paint1);
		canvas.drawLine(ww / 2, hh / 2, ww / 2 + x * 2 * scaledDensity, hh / 2 - y * 2 * scaledDensity, paint1);
		canvas.drawCircle(ww / 2 + x * 2 * scaledDensity, hh / 2 - y * 2 * scaledDensity, 20 * scaledDensity, paint2);
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
