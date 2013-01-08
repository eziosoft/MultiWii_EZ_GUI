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
package com.ezio.multiwii.mapoffline;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.ezio.multiwii.R;

class MapOfflineCopterOverlay extends Overlay {
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

	Bitmap bmp;
	float scaleBMP = 0.2f;

	public MapOfflineCopterOverlay(Context context) {
		super(context);
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

		p.setColor(Color.CYAN);
		p.setTextSize(20);
		p.setShadowLayer(8, 0, 0, Color.BLACK);
		p.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/gunplay.ttf"));// octin
		// sports
		// free.ttf"));

		textSizeSmall = context.getResources().getDimensionPixelSize(R.dimen.textSizeSmall);
		textSizeMedium = context.getResources().getDimensionPixelSize(R.dimen.textSizeMedium);
		// textSizeBig =
		// getResources().getDimensionPixelSize(R.dimen.textSizeBig);

		bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.m);
		
		scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
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

		VBat = (float) (vbat / 10.0);
		PowerSum = powerSum;
		PowerTrigger = powerTrigger;

	
	}

	public void draw(Canvas canvas, MapView mapv, boolean shadow) {
		// super.draw(canvas, mapv, shadow);

		projection = mapv.getProjection();

		final BoundingBoxE6 boundingBox = projection.getBoundingBox();

		projection.toPixels(GCopter, p1);
		projection.toPixels(GHome, p2);

		
		//draw copter
		Matrix matrix = new Matrix();

		matrix.preRotate(-Azimuth + 180, p1.x, p1.y);
		matrix.preTranslate(p1.x - bmp.getWidth() / 2 * scaleBMP, p1.y - bmp.getHeight() / 2 * scaleBMP);
		matrix.preScale(scaleBMP, scaleBMP);

		canvas.drawBitmap(bmp, matrix, p);
	
		// float x1 = (float) ((20 * Math.sin((Azimuth) * Math.PI / 180)) +
		// p1.x);
		// float y1 = (float) ((20 * Math.cos((Azimuth) * Math.PI / 180)) +
		// p1.y);

		//canvas.drawCircle(p1.x, p1.y, 20, mPaint1);
		//canvas.drawCircle(x1, y1, 5, mPaint2);
		
		//end copter

	

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

		// /
		int a = textSizeSmall;
		p.setTextSize(textSizeSmall);
		DrawStaticText(context.getString(R.string.GPS_numSat), 0, a, p, boundingBox, canvas);
		a += textSizeMedium;
		p.setTextSize(textSizeMedium);
		DrawStaticText(String.valueOf(SatNum), 0, a, p, boundingBox, canvas);

		a += textSizeSmall;
		p.setTextSize(textSizeSmall);
		DrawStaticText(context.getString(R.string.Baro), 0, a, p, boundingBox, canvas);
		a += textSizeMedium;
		p.setTextSize(textSizeMedium);
		DrawStaticText("GPS:" + String.valueOf(GPSAltitude) + "  Baro:" + String.format("%.2f", Altitude), 0, a, p, boundingBox, canvas);

		a += textSizeSmall;
		p.setTextSize(textSizeSmall);
		DrawStaticText(context.getString(R.string.GPS_distanceToHome), 0, a, p, boundingBox, canvas);
		a += textSizeMedium;
		p.setTextSize(textSizeMedium);
		DrawStaticText(String.valueOf(DistanceToHome), 0, a, p, boundingBox, canvas);

		if (VBat > 0) {
			a += textSizeSmall;
			p.setTextSize(textSizeSmall);
			DrawStaticText(context.getString(R.string.BattVoltage), 0, a, p, boundingBox, canvas);
			a += textSizeMedium;
			p.setTextSize(textSizeMedium);
			DrawStaticText(String.valueOf(VBat), 0, a, p, boundingBox, canvas);

			a += textSizeSmall;
			p.setTextSize(textSizeSmall);
			DrawStaticText(context.getString(R.string.PowerSumPowerTrigger), 0, a, p, boundingBox, canvas);
			a += textSizeMedium;
			p.setTextSize(textSizeMedium);
			DrawStaticText(String.valueOf(PowerSum) + "/" + String.valueOf(PowerTrigger), 0, a, p, boundingBox, canvas);

		}

	}

	void DrawStaticText(String text, float x, float y, Paint p, BoundingBoxE6 boundingBox, Canvas c) {
		Rect rect = projection.toPixels(boundingBox);
		c.drawText(text, rect.left + x, rect.top + y, p);
	}

	public static int metersToRadius(float meters, MapView map, double latitude) {
		return (int) (map.getProjection().metersToEquatorPixels(meters) * (1 / Math.cos(Math.toRadians(latitude))));
	}
}