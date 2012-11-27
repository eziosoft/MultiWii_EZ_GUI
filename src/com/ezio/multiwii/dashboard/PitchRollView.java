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
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class PitchRollView extends View {

	private Paint paint = new Paint();
	private Paint paint1 = new Paint();

	private float ang;

	int color = Color.YELLOW;

	static float scaledDensity = 0;

	public void SetAngle(float angle) {
		ang = angle;
		invalidate();
	}

	public PitchRollView(Context context) {
		super(context);

		init();
	}

	public PitchRollView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	private void init() {

		SetColor(color);
		scaledDensity = getResources().getDisplayMetrics().scaledDensity;
	}

	public void SetColor(int c) {
		color = c;
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2*scaledDensity);

		paint1.setAntiAlias(true);
		paint1.setColor(Color.YELLOW);
		paint1.setStyle(Paint.Style.FILL_AND_STROKE);
		paint1.setStrokeWidth(1);
		paint1.setTextSize(30*scaledDensity);
	}

	int hh, ww;

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		ww = w;
		hh = h;
		super.onSizeChanged(w, h, oldw, oldh);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT);
		int cx = ww / 2;
		int cy = hh / 2;
		canvas.translate(cx, cy);
		canvas.rotate(ang);
		// canvas.drawPath(mPath, mPaint);
		canvas.drawLine(-ww / 2, 0, ww / 2, 0, paint);
		canvas.drawOval(new RectF(-hh / 2, -hh / 2, hh / 2, hh / 2), paint);
		canvas.drawText(String.valueOf(ang), 0 - paint1.measureText(String.valueOf(ang)) / 2, 0, paint1);
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
