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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.ezio.multiwii.R;
import com.ezio.multiwii.R.string;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

public class Dashboard2View extends View {

	Context context;
	int ww, hh;
	Rect dim = new Rect();
	Paint p;
	Paint p1, p3, p4;

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

	static int textSizeSmall = 20;
	static int textSizeMedium = 40;
	static int textSizeBig = 80;

	NumberFormat format = new DecimalFormat(
			"0.############################################################"); // used
																				// to
																				// avoid
																				// scientific
																				// notation

	public void Set(int satNum, float distanceToHome, float directionToHome,
			float speed, float gpsAltitude, float altitude, float lat,
			float lon, float pitch, float roll, float azimuth, float gforce,
			String state, int vbat, int powerSum, int powerTrigger, int txRSSI,
			int rxRSSI) {
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
		this.invalidate();

	}

	public Dashboard2View(Context context) {
		super(context);

		this.context = context;
		// Display display = ((WindowManager) getContext().getSystemService(
		// Context.WINDOW_SERVICE)).getDefaultDisplay();

		getWindowVisibleDisplayFrame(dim);
		ww = dim.width();
		hh = dim.height();

		p = new Paint();
		p.setColor(Color.GREEN);
		p.setAntiAlias(true);
		p.setStyle(Style.STROKE);
		// p.setStrokeWidth(1);
		p.setTextSize(textSizeSmall);

		p4 = new Paint();
		p4.setColor(Color.GREEN);
		p4.setAntiAlias(true);
		p4.setStyle(Style.FILL);
		// p.setStrokeWidth(1);
		p4.setTextSize(textSizeSmall);

		p1 = new Paint();
		p1.setColor(Color.GREEN);
		p1.setAntiAlias(true);
		p1.setStyle(Style.STROKE);
		p1.setStrokeWidth(5);
		p1.setTextSize(textSizeSmall);

		p3 = new Paint();
		p3.setColor(Color.GREEN);
		p3.setAntiAlias(true);
		p3.setStyle(Style.STROKE);
		p3.setStrokeWidth(5);
		p3.setTextSize(textSizeSmall);
		p3.setPathEffect(new DashPathEffect(new float[] { 10, 20 }, 0));
		this.setBackgroundColor(Color.BLACK);

	}

	@Override
	protected void onDraw(Canvas c) {
		super.onDraw(c);

		// c.drawRect(0, 0, ww - 1, hh - 1, p);

		int a = textSizeSmall;
		p.setTextSize(textSizeSmall);
		c.drawText(context.getString(R.string.Satellites), 0, a, p);

		a += textSizeMedium;
		p.setTextSize(textSizeMedium);
		c.drawText(String.valueOf(SatNum), 0, a, p);

		a += textSizeSmall;
		p.setTextSize(textSizeSmall);
		c.drawText(context.getString(R.string.GPS_distanceToHome), 0, a, p);

		a += textSizeMedium;
		p.setTextSize(textSizeMedium);
		c.drawText(String.valueOf(DistanceToHome), 0, a, p);

		a += textSizeSmall;
		p.setTextSize(textSizeSmall);
		c.drawText(context.getString(R.string.GPS_directionToHome), 0, a, p);

		a += textSizeMedium;
		p.setTextSize(textSizeMedium);
		c.drawText(String.valueOf(DirectionToHome), 0, a, p);

		a += textSizeSmall;
		p.setTextSize(textSizeSmall);
		c.drawText(context.getString(R.string.GPS_speed), 0, a, p);

		a += textSizeMedium;
		p.setTextSize(textSizeMedium);
		c.drawText(String.valueOf(Speed), 0, a, p);

		a += textSizeSmall;
		p.setTextSize(textSizeSmall);
		c.drawText(context.getString(R.string.GPS_altitude), 0, a, p);

		a += textSizeMedium;
		p.setTextSize(textSizeMedium);
		c.drawText(String.valueOf(GPSAltitude), 0, a, p);

		a += textSizeSmall;
		p.setTextSize(textSizeSmall);
		c.drawText(context.getString(R.string.TxRSSI), 0, a, p);

		a += 5;
		p.setTextSize(textSizeMedium);
		c.drawRect(new Rect(0, a, 150, a + textSizeSmall), p);
		c.drawRect(new Rect(0, a, (int) map(TXRSSI, 0, 110, 0, 150), a
				+ textSizeSmall), p4);

		a += textSizeSmall * 2;
		p.setTextSize(textSizeSmall);
		c.drawText(context.getString(R.string.RxRSSI), 0, a, p);

		a += 5;
		p.setTextSize(textSizeMedium);
		c.drawRect(new Rect(0, a, 150, a + textSizeSmall), p);
		c.drawRect(new Rect(0, a, (int) map(RXRSSI, 0, 110, 0, 150), a
				+ textSizeSmall), p4);

		a = hh - textSizeMedium;
		p.setTextSize(textSizeSmall);
		c.drawText(context.getString(R.string.LatxLon), 0, a, p);

		a = hh;
		p.setTextSize(textSizeMedium);
		c.drawText(
				format.format(Lat / Math.pow(10, 7)) + " x "
						+ format.format(Lon / Math.pow(10, 7)), 0, a, p);

		// //////////////////////////////
		a = hh - textSizeMedium;
		p.setTextSize(textSizeSmall);
		c.drawText(context.getString(R.string.Azimuth),
				ww - p.measureText(context.getString(R.string.Azimuth)), a, p);

		a = hh;
		p.setTextSize(textSizeMedium);
		c.drawText(String.valueOf(Azimuth),
				ww - p.measureText(String.valueOf(Azimuth)), a, p);

		a -= textSizeMedium + textSizeSmall;
		p.setTextSize(textSizeMedium);
		c.drawText(Integer.toString((int) Pitch),
				ww - p.measureText(Integer.toString((int) Pitch)), a, p);

		a -= textSizeMedium;
		p.setTextSize(textSizeSmall);
		c.drawText(context.getString(R.string.Pitch),
				ww - p.measureText(context.getString(R.string.Pitch)), a, p);

		a -= textSizeSmall;
		p.setTextSize(textSizeMedium);
		c.drawText(Integer.toString((int) Roll),
				ww - p.measureText(Integer.toString((int) Roll)), a, p);

		a -= textSizeMedium;
		p.setTextSize(textSizeSmall);
		c.drawText(context.getString(R.string.Roll),
				ww - p.measureText(context.getString(R.string.Roll)), a, p);

		a -= textSizeSmall;
		p.setTextSize(textSizeMedium);
		c.drawText(String.format("%.2f", Altitude),
				ww - p.measureText(String.format("%.2f", Altitude)), a, p);

		a -= textSizeMedium;
		p.setTextSize(textSizeSmall);
		c.drawText(context.getString(R.string.GPS_altitude),
				ww - p.measureText(context.getString(R.string.GPS_altitude)),
				a, p);

		a -= textSizeSmall;
		p.setTextSize(textSizeMedium);
		c.drawText(String.format("%.2f", Gforce),
				ww - p.measureText(String.format("%.2f", Gforce)), a, p);

		a -= textSizeMedium;
		p.setTextSize(textSizeSmall);
		c.drawText(context.getString(R.string.gforce),
				ww - p.measureText(context.getString(R.string.gforce)), a, p);

		a = 0;
		a += textSizeSmall;
		p.setTextSize(textSizeSmall);
		c.drawText(context.getString(R.string.state),
				ww - p.measureText(context.getString(R.string.state)), a, p);

		a += textSizeMedium;
		p.setTextSize(textSizeMedium);
		c.drawText(String.valueOf(State),
				ww - p.measureText(String.valueOf(State)), a, p);

		a += textSizeSmall;
		p.setTextSize(textSizeSmall);
		c.drawText(context.getString(R.string.Battery),
				ww - p.measureText(context.getString(R.string.Battery)), a, p);

		a += textSizeMedium;
		p.setTextSize(textSizeMedium);
		c.drawText(String.valueOf(VBat),
				ww - p.measureText(String.valueOf(VBat)), a, p);

		a += textSizeSmall;
		p.setTextSize(textSizeSmall);
		c.drawText(
				context.getString(R.string.PowerSumPowerTrigger),
				ww
						- p.measureText(context
								.getString(R.string.PowerSumPowerTrigger)), a,
				p);

		a += textSizeMedium;
		p.setTextSize(textSizeMedium);
		c.drawText(
				String.valueOf(PowerSum) + "/" + String.valueOf(PowerTrigger),
				ww
						- p.measureText(String.valueOf(PowerSum) + "/"
								+ String.valueOf(PowerTrigger)), a, p);

		float x1, y1, x2, y2;
		x1 = (float) (200 * Math.sin((-Roll - 90) * Math.PI / 180)) + ww / 2;
		y1 = (float) ((float) ((200 * Math.cos((-Roll - 90) * Math.PI / 180)) + hh / 2) - (Pitch) / 35 * 200 / 2);

		x2 = (float) (200 * Math.sin((-Roll - 270) * Math.PI / 180)) + ww / 2;
		y2 = (float) ((float) ((200 * Math.cos((-Roll - 270) * Math.PI / 180)) + hh / 2) - (Pitch) / 35 * 200 / 2);

		c.drawLine(x1, y1, x2, y2, p1);

		x1 = (float) (200 * Math.sin((-Roll - 45) * Math.PI / 180)) + ww / 2;
		y1 = (float) ((float) ((200 * Math.cos((-Roll - 45) * Math.PI / 180)) + hh / 2) - (Pitch) / 35 * 200 / 2);

		x2 = (float) (200 * Math.sin((-Roll - 315) * Math.PI / 180)) + ww / 2;
		y2 = (float) ((float) ((200 * Math.cos((-Roll - 315) * Math.PI / 180)) + hh / 2) - (Pitch) / 35 * 200 / 2);

		c.drawLine(x1, y1, x2, y2, p3);

		x1 = (float) (200 * Math.sin((-Roll - 135) * Math.PI / 180)) + ww / 2;
		y1 = (float) ((float) ((200 * Math.cos((-Roll - 135) * Math.PI / 180)) + hh / 2) - (Pitch) / 35 * 200 / 2);

		x2 = (float) (200 * Math.sin((-Roll - 225) * Math.PI / 180)) + ww / 2;
		y2 = (float) ((float) ((200 * Math.cos((-Roll - 225) * Math.PI / 180)) + hh / 2) - (Pitch) / 35 * 200 / 2);

		c.drawLine(x1, y1, x2, y2, p3);

		// ////

		x1 = (float) (200 * Math.sin((-Roll + 180) * Math.PI / 180)) + ww / 2;
		y1 = (float) (200 * Math.cos((-Roll + 180) * Math.PI / 180)) + hh / 2;

		x2 = (float) (180 * Math.sin((-Roll + 180) * Math.PI / 180)) + ww / 2;
		y2 = (float) (180 * Math.cos((-Roll + 180) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p1);

		x1 = (float) (200 * Math.sin((-Roll + 190) * Math.PI / 180)) + ww / 2;
		y1 = (float) (200 * Math.cos((-Roll + 190) * Math.PI / 180)) + hh / 2;

		x2 = (float) (190 * Math.sin((-Roll + 190) * Math.PI / 180)) + ww / 2;
		y2 = (float) (190 * Math.cos((-Roll + 190) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p3);

		x1 = (float) (200 * Math.sin((-Roll + 200) * Math.PI / 180)) + ww / 2;
		y1 = (float) (200 * Math.cos((-Roll + 200) * Math.PI / 180)) + hh / 2;

		x2 = (float) (190 * Math.sin((-Roll + 200) * Math.PI / 180)) + ww / 2;
		y2 = (float) (190 * Math.cos((-Roll + 200) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p3);

		x1 = (float) (200 * Math.sin((-Roll + 210) * Math.PI / 180)) + ww / 2;
		y1 = (float) (200 * Math.cos((-Roll + 210) * Math.PI / 180)) + hh / 2;

		x2 = (float) (190 * Math.sin((-Roll + 210) * Math.PI / 180)) + ww / 2;
		y2 = (float) (190 * Math.cos((-Roll + 210) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p3);

		x1 = (float) (200 * Math.sin((-Roll + 225) * Math.PI / 180)) + ww / 2;
		y1 = (float) (200 * Math.cos((-Roll + 225) * Math.PI / 180)) + hh / 2;

		x2 = (float) (190 * Math.sin((-Roll + 225) * Math.PI / 180)) + ww / 2;
		y2 = (float) (190 * Math.cos((-Roll + 225) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p1);

		x1 = (float) (200 * Math.sin((-Roll + 170) * Math.PI / 180)) + ww / 2;
		y1 = (float) (200 * Math.cos((-Roll + 170) * Math.PI / 180)) + hh / 2;

		x2 = (float) (190 * Math.sin((-Roll + 170) * Math.PI / 180)) + ww / 2;
		y2 = (float) (190 * Math.cos((-Roll + 170) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p3);

		x1 = (float) (200 * Math.sin((-Roll + 160) * Math.PI / 180)) + ww / 2;
		y1 = (float) (200 * Math.cos((-Roll + 160) * Math.PI / 180)) + hh / 2;

		x2 = (float) (190 * Math.sin((-Roll + 160) * Math.PI / 180)) + ww / 2;
		y2 = (float) (190 * Math.cos((-Roll + 160) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p3);

		x1 = (float) (200 * Math.sin((-Roll + 150) * Math.PI / 180)) + ww / 2;
		y1 = (float) (200 * Math.cos((-Roll + 150) * Math.PI / 180)) + hh / 2;

		x2 = (float) (190 * Math.sin((-Roll + 150) * Math.PI / 180)) + ww / 2;
		y2 = (float) (190 * Math.cos((-Roll + 150) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p3);

		x1 = (float) (200 * Math.sin((-Roll + 135) * Math.PI / 180)) + ww / 2;
		y1 = (float) (200 * Math.cos((-Roll + 135) * Math.PI / 180)) + hh / 2;

		x2 = (float) (190 * Math.sin((-Roll + 135) * Math.PI / 180)) + ww / 2;
		y2 = (float) (190 * Math.cos((-Roll + 135) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p1);

		c.drawCircle(ww / 2, hh / 2, 10, p1);
		c.drawLine(ww / 2, hh / 2 - 10, ww / 2, hh / 2 - 30, p1);
		c.drawLine(ww / 2 - 10, hh / 2, ww / 2 - 30, hh / 2, p1);
		c.drawLine(ww / 2 + 10, hh / 2, ww / 2 + 30, hh / 2, p1);

		RectF r = new RectF(ww / 2 - 200, hh / 2 - 200, ww / 2 + 200,
				hh / 2 + 200);
		// c.drawRect(r, p);
		c.drawArc(r, Roll - 45, -90, false, p1);
		// c.drawArc(oval, startAngle, sweepAngle, useCenter, paint)
		c.drawLine(ww / 2, hh / 2 - 200, ww / 2, hh / 2 - 230, p1);

	}

	float map(float x, float in_min, float in_max, float out_min, float out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

}
