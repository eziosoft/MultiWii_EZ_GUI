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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.ezio.multiwii.R;
import com.ezio.multiwii.R.string;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

class CopterOverlay extends Overlay {

	private Context			context;
	private Projection		projection;
	GeoPoint				GCopter			= new GeoPoint(0, 0);
	GeoPoint				GHome			= new GeoPoint(0, 0);
	float					Azimuth			= 45;

	Paint					mPaint1			= new Paint();
	Paint					mPaint2			= new Paint();
	Paint					mPaint3			= new Paint();
	Paint					p				= new Paint();

	Point					p1				= new Point();
	Point					p2				= new Point();

	private List<GeoPoint>	points			= new ArrayList<GeoPoint>();
	private int				pointsCount		= 20;

	static int				textSizeSmall	= 25;
	static int				textSizeMedium	= 50;

	public float			VBat			= 0;
	public int				PowerSum		= 0;
	public int				PowerTrigger	= 0;

	public CopterOverlay(Context context) {

		this.context = context;
		mPaint1.setDither(true);
		mPaint1.setColor(Color.RED);
		mPaint1.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint1.setStrokeJoin(Paint.Join.ROUND);
		mPaint1.setStrokeCap(Paint.Cap.ROUND);
		mPaint1.setStrokeWidth(2);
		// mPaint1.setShadowLayer(5, 10, 10, Color.GRAY);

		mPaint2.setColor(Color.YELLOW);
		mPaint2.setTextSize(40);

		mPaint3.setColor(Color.YELLOW);
		mPaint3.setStyle(Paint.Style.STROKE);
		mPaint3.setStrokeWidth(2);

		p.setColor(Color.YELLOW);
		p.setTextSize(20);
		p.setShadowLayer(8, 0, 0, Color.BLACK);

	}

	public void Set(GeoPoint copter, float azimuth, GeoPoint home, int vbat, int powerSum, int powerTrigger) {

		GCopter = copter;
		GHome = home;
		Azimuth = azimuth;

		points.add(copter);

		if (points.size() > pointsCount) {
			points.remove(0);
		}

		VBat = (float) (vbat / 10.0);
		PowerSum = powerSum;
		PowerTrigger = powerTrigger;

	}

	public void draw(Canvas canvas, MapView mapv, boolean shadow) {
		super.draw(canvas, mapv, shadow);

		projection = mapv.getProjection();

		projection.toPixels(GCopter, p1);
		projection.toPixels(GHome, p2);

		float x1 = (float) ((20 * Math.sin((Azimuth) * Math.PI / 180)) + p1.x);
		float y1 = (float) ((20 * Math.cos((Azimuth) * Math.PI / 180)) + p1.y);

		canvas.drawCircle(p1.x, p1.y, 20, mPaint1);
		canvas.drawCircle(x1, y1, 5, mPaint2);

		canvas.drawText("H", p2.x - mPaint2.measureText("H") / 2, p2.y + mPaint2.getTextSize() / 2 - 5, mPaint2);
		canvas.drawCircle(p2.x, p2.y, 20, mPaint3);

		if (points.size() > 2) {
			Path path = new Path();
			Point p = new Point();
			projection.toPixels(points.get(0), p);
			path.moveTo(p.x, p.y);

			for (int i = 1; i < points.size(); i++) {

				projection.toPixels(points.get(i), p);
				path.lineTo(p.x, p.y);

			}

			canvas.drawPath(path, mPaint3);
		}

		int a = textSizeSmall;
		p.setTextSize(textSizeSmall);
		canvas.drawText(context.getString(R.string.Battery), 0, a, p);

		a += textSizeMedium;
		p.setTextSize(textSizeMedium);
		canvas.drawText(String.valueOf(VBat), 0, a, p);

		a += textSizeSmall;
		p.setTextSize(textSizeSmall);
		canvas.drawText(context.getString(R.string.PowerSumPowerTrigger), 0, a, p);

		a += textSizeMedium;
		p.setTextSize(textSizeMedium);
		canvas.drawText(String.valueOf(PowerSum) + "/" + String.valueOf(PowerTrigger), 0, a, p);

	}

	public static int metersToRadius(float meters, MapView map, double latitude) {
		return (int) (map.getProjection().metersToEquatorPixels(meters) * (1 / Math.cos(Math.toRadians(latitude))));
	}
}