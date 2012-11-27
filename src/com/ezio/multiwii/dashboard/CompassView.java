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
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CompassView extends View {

	private Paint paint1 = new Paint();
	private Paint paint2 = new Paint();
	private Paint paint3 = new Paint();
	private Path mPath = new Path();
	public float kier;
	int color = Color.GREEN;
	int textColor = Color.YELLOW;
	int hh = 80, ww = 80;
	String text = "";
	float scaledDensity = 0;

	public void SetHeading(float h) {
		kier = -h;
		invalidate();
	}

	public CompassView(Context context) {
		super(context);

		init();
	}

	public CompassView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	private void init() {
		// Construct a wedge-shaped path
		mPath.moveTo(0, -20 * scaledDensity);
		mPath.lineTo(-10 * scaledDensity, 30 * scaledDensity);
		mPath.lineTo(0, 20 * scaledDensity);
		mPath.lineTo(10 * scaledDensity, 30 * scaledDensity);
		mPath.close();

		SetColor(color, textColor);

		scaledDensity = getResources().getDisplayMetrics().scaledDensity;
	}

	public void SetText(String Text) {
		text = Text;
	}

	public void SetColor(int c, int text_color) {
		color = c;
		textColor = text_color;

		paint2.setAntiAlias(true);
		paint2.setColor(color);
		paint2.setStyle(Paint.Style.FILL);

		paint1.setAntiAlias(true);
		paint1.setColor(color);
		paint1.setStyle(Paint.Style.STROKE);
		paint1.setStrokeWidth(2 * scaledDensity);

		paint3.setAntiAlias(true);
		paint3.setColor(textColor);
		paint3.setStyle(Paint.Style.STROKE);
		paint3.setStrokeWidth(1 * scaledDensity);
		paint3.setTextSize(10 * scaledDensity);
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

		int cx = ww / 2;
		int cy = hh / 2;

		canvas.translate(cx, cy);
		canvas.rotate(kier);

		canvas.drawPath(mPath, paint2);
		canvas.drawOval(new RectF(-hh / 2, -hh / 2, hh / 2, hh / 2), paint1);
		if (text.length() > 0)
			canvas.drawText(text, 0 - paint3.measureText(text) / 2, -20 * scaledDensity, paint3);
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
