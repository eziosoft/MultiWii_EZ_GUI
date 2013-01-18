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
package com.ezio.multiwii.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

class MapCirclesOverlay extends Overlay {

	private Context context;
	private Projection projection;
	GeoPoint GHome = new GeoPoint(0, 0);
	GeoPoint GYou = new GeoPoint(0, 0);
	Paint mPaint = new Paint();
	Paint mPaint1 = new Paint();

	float scaledDensity = 0;

	public MapCirclesOverlay(Context context) {
		this.context = context;
		scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;

		mPaint.setDither(true);
		mPaint.setAntiAlias(false);
		mPaint.setColor(Color.GREEN);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(1.5f*scaledDensity);
		mPaint.setTextSize(10*scaledDensity);
		// mPaint.setAlpha(70);

		mPaint1.setDither(true);
		mPaint1.setAntiAlias(false);
		mPaint1.setColor(Color.YELLOW);
		mPaint1.setStyle(Paint.Style.STROKE);
		// mPaint1.setStrokeJoin(Paint.Join.ROUND);
		// mPaint1.setStrokeCap(Paint.Cap.ROUND);
		mPaint1.setStrokeWidth(1.5f*scaledDensity);
		mPaint1.setTextSize(20*scaledDensity);
		// mPaint1.setAlpha(50);

	}

	public void Set(GeoPoint ghome, GeoPoint gyou) {
		GHome = ghome;
		GYou = gyou;

	}

	public void draw(Canvas canvas, MapView mapv, boolean shadow) {
		super.draw(canvas, mapv, shadow);

		projection = mapv.getProjection();

		Point p1 = new Point();

		projection.toPixels(GHome, p1);

		// int distance = 2;
		// for (int i = distance; i <= 10; i += distance) {
		// if (metersToRadius(i, mapv, GHome.getLatitudeE6() / 1e6) > 0) {
		// canvas.drawCircle(p1.x, p1.y, metersToRadius(i, mapv,
		// GHome.getLatitudeE6() / 1e6), mPaint);
		// canvas.drawText(String.valueOf(i), p1.x + metersToRadius(i, mapv,
		// GHome.getLatitudeE6() / 1e6), p1.y, mPaint);
		// }
		// }

		projection.toPixels(GYou, p1);

		canvas.drawText("You", p1.x, p1.y, mPaint1);
		canvas.drawCircle(p1.x, p1.y, 5, mPaint1);
	}

	public static int metersToRadius(float meters, MapView map, double latitude) {
		return (int) (map.getProjection().metersToEquatorPixels(meters) * (1 / Math.cos(Math.toRadians(latitude))));
	}
}