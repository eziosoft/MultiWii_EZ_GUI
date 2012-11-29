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
import com.ezio.multiwii.mw.MultiWii210;
import com.ezio.multiwii.mw.MultirotorData;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

class CopterOverlay extends Overlay {

	private Context context;
	private Projection projection;
	GeoPoint GCopter = new GeoPoint(0, 0);
	GeoPoint GHome = new GeoPoint(0, 0);

	Paint mPaint1 = new Paint();
	Paint mPaint2 = new Paint();
	Paint mPaint3 = new Paint();
	Paint p = new Paint();

	Point p1 = new Point();
	Point p2 = new Point();

	private List<GeoPoint> points = new ArrayList<GeoPoint>();
	private int pointsCount = 20;

	static int textSizeSmall = 25;
	static int textSizeMedium = 50;

	float scaledDensity = 0;

	public int SatNum = 5;

	public float DistanceToHome = 254;
	public float DirectionToHome = 45;

	public float Speed = 30;
	public float GPSAltitude = 20;
	public float Altitude = 23;

	public float Lat = (float) 23.233212, Lon = (float) 32.43214;
	public float Pitch = 10, Roll = 20, Azimuth = 30;
	public float Gforce = 1;

	public String State = "ARM";

	public float VBat = 0;
	public int PowerSum = 0;
	public int PowerTrigger = 0;
	public int I2CError = 0;

	public int TXRSSI = 0;
	public int RXRSSI = 0;

	public CopterOverlay(Context context) {

		this.context = context;
		scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;

		textSizeSmall = context.getResources().getDimensionPixelSize(R.dimen.textSizeSmall);
		textSizeMedium = context.getResources().getDimensionPixelSize(R.dimen.textSizeMedium);

		mPaint1.setDither(true);
		mPaint1.setColor(Color.RED);
		mPaint1.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint1.setStrokeJoin(Paint.Join.ROUND);
		mPaint1.setStrokeCap(Paint.Cap.ROUND);
		mPaint1.setStrokeWidth(1 * scaledDensity);
		// mPaint1.setShadowLayer(5, 10, 10, Color.GRAY);

		mPaint2.setColor(Color.YELLOW);
		mPaint2.setTextSize(30 * scaledDensity);

		mPaint3.setColor(Color.YELLOW);
		mPaint3.setStyle(Paint.Style.STROKE);
		mPaint3.setStrokeWidth(1 * scaledDensity);

		p.setColor(Color.YELLOW);
		p.setTextSize(20 * scaledDensity);
		p.setShadowLayer(8 * scaledDensity, 0, 0, Color.BLACK);

	}

	public void Set(GeoPoint copter, GeoPoint home, int satNum, float distanceToHome, float directionToHome, float speed, float gpsAltitude, float altitude, float lat, float lon, float pitch, float roll, float azimuth, float gforce, String state, int vbat, int powerSum, int powerTrigger, int txRSSI, int rxRSSI) {

		GCopter = copter;
		GHome = home;

		SatNum = satNum;
		DistanceToHome = distanceToHome;
		DirectionToHome = directionToHome;
		Speed = speed;
		GPSAltitude = gpsAltitude;
		Altitude = altitude;
		Lat = lat;
		Lon = lon;
		Pitch = pitch;
		Roll = roll;
		Azimuth = azimuth;
		Gforce = gforce;
		State = state;
		VBat = (float) (vbat / 10.0);
		PowerSum = powerSum;
		PowerTrigger = powerTrigger;
		TXRSSI = txRSSI;
		RXRSSI = rxRSSI;

		points.add(copter);

		if (points.size() > pointsCount) {
			points.remove(0);
		}

	}

	public void draw(Canvas canvas, MapView mapv, boolean shadow) {
		super.draw(canvas, mapv, shadow);

		projection = mapv.getProjection();

		projection.toPixels(GCopter, p1);
		projection.toPixels(GHome, p2);

		float x1 = (float) ((20 * scaledDensity * Math.sin((Azimuth) * Math.PI / 180)) + p1.x);
		float y1 = (float) ((20 * scaledDensity * Math.cos((Azimuth) * Math.PI / 180)) + p1.y);

		canvas.drawCircle(p1.x, p1.y, 20 * scaledDensity, mPaint1);
		canvas.drawCircle(x1, y1, 5, mPaint2);

		canvas.drawText("H", p2.x - mPaint2.measureText("H") / 2, p2.y + mPaint2.getTextSize() / 2 - 5 * scaledDensity, mPaint2);
		canvas.drawCircle(p2.x, p2.y, 20 * scaledDensity, mPaint3);

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
		canvas.drawText(context.getString(R.string.GPS_numSat), 0, a, p);
		a += textSizeMedium;
		p.setTextSize(textSizeMedium);
		canvas.drawText(String.valueOf(SatNum), 0, a, p);

		
		a += textSizeSmall;
		p.setTextSize(textSizeSmall);
		canvas.drawText(context.getString(R.string.Baro), 0, a, p);
		a += textSizeMedium;
		p.setTextSize(textSizeMedium);
		canvas.drawText("GPS:"+String.valueOf(GPSAltitude)+"  Baro:"+String.valueOf(Altitude), 0, a, p);
		
		a += textSizeSmall;
		p.setTextSize(textSizeSmall);
		canvas.drawText(context.getString(R.string.GPS_distanceToHome), 0, a, p);
		a += textSizeMedium;
		p.setTextSize(textSizeMedium);
		canvas.drawText(String.valueOf(DistanceToHome), 0, a, p);
		
		if(VBat>0)
		{
			a += textSizeSmall;
			p.setTextSize(textSizeSmall);
			canvas.drawText(context.getString(R.string.BattVoltage), 0, a, p);
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
		

	}

	public static int metersToRadius(float meters, MapView map, double latitude) {
		return (int) (map.getProjection().metersToEquatorPixels(meters) * (1 / Math.cos(Math.toRadians(latitude))));
	}
}