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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.View;

import com.ezio.multiwii.R;
import com.ezio.multiwii.helpers.Functions;

public class Dashboard2View extends View {

	Context context;
	int ww, hh;
	Rect dim = new Rect();
	Paint p;
	Paint p1, p3, p4, pgrid, p2;

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

	private static float scale = 1f; // not used

	static int textSizeSmall = 0;
	static int textSizeMedium = 0;
	static int textSizeBig = 0;

	static int HorizonCircleSize = 200;
	static int AngleIndicatorLenght = 10;
	static int AngleIndicatorLenghtLong = 20;
	float scaledDensity = 0;
	NumberFormat format = new DecimalFormat("0.############################################################"); // used
																												// to
																												// avoid
																												// scientific
																												// notation

	public void Set(int satNum, float distanceToHome, float directionToHome, float speed, float gpsAltitude, float altitude, float lat, float lon, float pitch, float roll, float azimuth, float gforce, String state, int vbat, int powerSum, int powerTrigger, int txRSSI, int rxRSSI) {
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

		scale = 1;
		// textSizeSmall = (int) (20 * scale);
		// textSizeMedium = (int) (40 * scale);
		// textSizeBig = (int) (80 * scale);

		textSizeSmall = getResources().getDimensionPixelSize(R.dimen.textSizeSmall);
		textSizeMedium = getResources().getDimensionPixelSize(R.dimen.textSizeMedium);
		textSizeBig = getResources().getDimensionPixelSize(R.dimen.textSizeBig);
		HorizonCircleSize = getResources().getDimensionPixelSize(R.dimen.HorizonCircleSize);
		AngleIndicatorLenght = getResources().getDimensionPixelSize(R.dimen.AngleIndicatorLenght);
		AngleIndicatorLenghtLong = getResources().getDimensionPixelSize(R.dimen.AngleIndicatorLenghtLong);
		scaledDensity = getResources().getDisplayMetrics().scaledDensity;

		// grid
		pgrid = new Paint();
		pgrid.setColor(Color.CYAN);
		pgrid.setAntiAlias(true);
		pgrid.setStyle(Style.STROKE);
		pgrid.setStrokeWidth(0);
		pgrid.setAlpha(30);

		// text
		p = new Paint();
		p.setColor(Color.CYAN);
		p.setAntiAlias(true);
		p.setStyle(Style.STROKE);
		p.setTextSize(textSizeSmall);
		p.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/gunplay.ttf"));

		// digits
		p2 = new Paint();
		p2.setColor(Color.YELLOW);
		p2.setAntiAlias(true);
		p2.setStyle(Style.STROKE);
		p2.setTextSize(textSizeMedium);
		p2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/digital7.ttf"));

		// progressbar fill
		p4 = new Paint();
		p4.setColor(Color.YELLOW);
		p4.setAntiAlias(true);
		p4.setStyle(Style.FILL);
		p4.setTextSize(textSizeSmall);
		p4.setAlpha(150);

		// line
		p1 = new Paint();
		p1.setColor(Color.GREEN);
		p1.setAntiAlias(true);
		p1.setStyle(Style.STROKE);
		p1.setStrokeWidth(2f * scaledDensity);
		p1.setTextSize(textSizeSmall);
		p1.setAlpha(150);

		// dashed line center
		p3 = new Paint();
		p3.setColor(Color.GREEN);
		p3.setAntiAlias(true);
		p3.setStyle(Style.STROKE);
		p3.setStrokeWidth(2f * scaledDensity);
		p3.setTextSize(textSizeSmall);
		p3.setPathEffect(new DashPathEffect(new float[] { 10 * scaledDensity, 20 * scaledDensity }, 0));
		p3.setAlpha(150);

		this.setBackgroundColor(Color.BLACK);

	}

	void drawCompass(Canvas c, float x, float y, float wight, int step, int range, int value) {

		value = value - range / 2 + step;
		p.setTextSize(textSizeSmall);
		for (float i = 0 + step; i <= range - step; i++) {
			if (value % step == 0) {
				String t = String.valueOf((int) value);
				c.drawText(t, (x + i * wight / range - p.measureText(t) / 2), y, p);
				c.drawLine((x + i * wight / range), y, (x + i * wight / range), (y + 10 * scaledDensity), p);
			}

			value++;
		}

		c.drawRect(x, (y - textSizeSmall), (x + wight), (y + 10 * scaledDensity), p);
		c.drawLine((x + wight / 2), (y - textSizeSmall), (x + wight / 2), (y + 10 * scaledDensity), p);

	}

	void drawVertical(Canvas c, float x, float y, float heigh, int step, int range, int value) {

		value = value - range / 2 + step;
		p.setTextSize(textSizeSmall);
		for (float i = 0 + step; i <= range - step; i++) {
			if (value % step == 0) {
				String t = String.valueOf((int) value);
				c.drawText(t, x - p.measureText(t) / 2, (y + i * heigh / range), p);
				c.drawLine(x - 10 * scaledDensity, (y + i * heigh / range), (x + 10 * scaledDensity), (y + i * heigh / range), p);
			}

			value++;
		}

		c.drawRect((x - textSizeSmall), y, (x + 10 * scaledDensity), (y + heigh), p);
		c.drawLine((x - textSizeSmall), (y + heigh / 2), (x + 10 * scaledDensity), (y + heigh / 2), p);

	}

	@Override
	protected void onDraw(Canvas c) {
		super.onDraw(c);

		// debug
		if (false) {
			SatNum = 5;
			DistanceToHome = 254;
			DirectionToHome = 45;
			Speed = 30;
			GPSAltitude = 20;
			Altitude = 23;
			Lat = 238233212f;
			Lon = 32343214f;
			Pitch = 10;
			Roll = 20;
			Azimuth = 67;
			Gforce = 1;
			TXRSSI = 50;
			RXRSSI = 80;
			VBat = 11.3f;
			State = "LEVEL BARO MAG GPS HOLD";

		}

		// grid
		if (true) {
			for (int i = 0; i <= ww; i += ww / 10) {
				c.drawLine(i, 0, i, hh, pgrid);
			}

			for (int i = 0; i <= hh; i += hh / 10) {
				c.drawLine(0, i, ww, i, pgrid);
			}
		}
		// end grid

		int a = textSizeSmall;
		if (SatNum > 0)
			c.drawText(context.getString(R.string.Satellites), 0, a, p);

		a += textSizeMedium;
		if (SatNum > 0)
			c.drawText(String.valueOf(SatNum), 0, a, p2);

		a += textSizeSmall;
		if (SatNum > 0)
			c.drawText(context.getString(R.string.GPS_distanceToHome), 0, a, p);

		a += textSizeMedium;
		if (SatNum > 0)
			c.drawText(String.valueOf(DistanceToHome), 0, a, p2);

		a += textSizeSmall;
		if (SatNum > 0)
			c.drawText(context.getString(R.string.GPS_directionToHome), 0, a, p);

		a += textSizeMedium;
		if (SatNum > 0)
			c.drawText(String.valueOf(DirectionToHome), 0, a, p2);

		a += textSizeSmall;
		if (SatNum > 0)
			c.drawText(context.getString(R.string.GPS_speed), 0, a, p);

		a += textSizeMedium;
		if (SatNum > 0)
			c.drawText(String.valueOf(Speed), 0, a, p2);

		a += textSizeSmall;
		if (SatNum > 0)
			c.drawText(context.getString(R.string.GPS_altitude), 0, a, p);

		a += textSizeMedium;
		if (SatNum > 0)
			c.drawText(String.valueOf(GPSAltitude), 0, a, p2);

		a += textSizeSmall;
		if (TXRSSI != 0)
			c.drawText(context.getString(R.string.TxRSSI), 0, a, p);

		a += 5;
		if (TXRSSI != 0)
			c.drawRect(0, a, (int) (80 * scaledDensity), a + textSizeSmall, p);

		if (TXRSSI != 0)
			c.drawRect(0, a, (int) Functions.map(TXRSSI, 0, 110, 0, 80 * scaledDensity), a + textSizeSmall, p4);

		a += textSizeSmall * 2;
		if (TXRSSI != 0)
			c.drawText(context.getString(R.string.RxRSSI), 0, a, p);

		a += 5;
		if (TXRSSI != 0)
			c.drawRect(0, a, (int) (80 * scaledDensity), a + textSizeSmall, p);
		if (TXRSSI != 0)
			c.drawRect(0, a, (int) Functions.map(RXRSSI, 0, 110, 0, 80 * scaledDensity), a + textSizeSmall, p4);

		if (SatNum > 0) {
			a = hh;
			c.drawText(format.format(Lat / Math.pow(10, 7)), 0, a, p2);

			a -= textSizeMedium;
			c.drawText(context.getString(R.string.GPS_latitude), 0, a, p);

			a -= textSizeSmall;
			c.drawText(format.format(Lon / Math.pow(10, 7)), 0, a, p2);

			a -= textSizeMedium;
			c.drawText(context.getString(R.string.GPS_longitude), 0, a, p);
		}
		// //////////////////////////////
		a = hh - textSizeMedium;
		if (Azimuth != 0)
			c.drawText(context.getString(R.string.Azimuth), ww - p.measureText(context.getString(R.string.Azimuth)), a, p);

		a = hh;
		if (Azimuth != 0)
			c.drawText(String.valueOf(Azimuth), ww - p2.measureText(String.valueOf(Azimuth)), a, p2);

		a -= textSizeMedium + textSizeSmall;
		c.drawText(Integer.toString((int) Pitch), ww - p2.measureText(Integer.toString((int) Pitch)), a, p2);

		a -= textSizeMedium;
		c.drawText(context.getString(R.string.Pitch), ww - p.measureText(context.getString(R.string.Pitch)), a, p);

		a -= textSizeSmall;
		c.drawText(Integer.toString((int) Roll), ww - p2.measureText(Integer.toString((int) Roll)), a, p2);

		a -= textSizeMedium;
		c.drawText(context.getString(R.string.Roll), ww - p.measureText(context.getString(R.string.Roll)), a, p);

		a -= textSizeSmall;
		if (Altitude != 0)
			c.drawText(String.format("%.2f", Altitude), ww - p2.measureText(String.format("%.2f", Altitude)), a, p2);

		a -= textSizeMedium;
		if (Altitude != 0)
			c.drawText(context.getString(R.string.GPS_altitude), ww - p.measureText(context.getString(R.string.GPS_altitude)), a, p);

		a -= textSizeSmall;
		c.drawText(String.format("%.2f", Gforce), ww - p2.measureText(String.format("%.2f", Gforce)), a, p2);

		a -= textSizeMedium;
		c.drawText(context.getString(R.string.gforce), ww - p.measureText(context.getString(R.string.gforce)), a, p);

		a = 0;
		a += textSizeSmall;
		c.drawText(context.getString(R.string.state), ww - p.measureText(context.getString(R.string.state)), a, p);

		a += textSizeMedium;
		c.drawText(String.valueOf(State), ww - p2.measureText(String.valueOf(State)), a, p2);

		a += textSizeSmall;
		if (VBat != 0)
			c.drawText(context.getString(R.string.Battery), ww - p.measureText(context.getString(R.string.Battery)), a, p);

		a += textSizeMedium;
		if (VBat != 0)
			c.drawText(String.valueOf(VBat), ww - p2.measureText(String.valueOf(VBat)), a, p2);

		a += textSizeSmall;
		if (VBat != 0)
			c.drawText(context.getString(R.string.PowerSumPowerTrigger), ww - p.measureText(context.getString(R.string.PowerSumPowerTrigger)), a, p);

		a += textSizeMedium;
		if (VBat != 0)
			c.drawText(String.valueOf(PowerSum) + "/" + String.valueOf(PowerTrigger), ww - p2.measureText(String.valueOf(PowerSum) + "/" + String.valueOf(PowerTrigger)), a, p2);

		// horyzon lines
		float x1, y1, x2, y2;
		x1 = (float) (HorizonCircleSize * scale * Math.sin((-Roll - 90) * Math.PI / 180)) + ww / 2;
		y1 = (float) ((float) ((HorizonCircleSize * scale * Math.cos((-Roll - 90) * Math.PI / 180)) + hh / 2) - (Pitch) / 35 * 200 / 2);

		x2 = (float) (HorizonCircleSize * scale * Math.sin((-Roll - 270) * Math.PI / 180)) + ww / 2;
		y2 = (float) ((float) ((HorizonCircleSize * scale * Math.cos((-Roll - 270) * Math.PI / 180)) + hh / 2) - (Pitch) / 35 * 200 / 2);

		c.drawLine(x1, y1, x2, y2, p1);

		x1 = (float) (HorizonCircleSize * scale * Math.sin((-Roll - 45) * Math.PI / 180)) + ww / 2;
		y1 = (float) ((float) ((HorizonCircleSize * scale * Math.cos((-Roll - 45) * Math.PI / 180)) + hh / 2) - (Pitch) / 35 * 200 / 2);

		x2 = (float) (HorizonCircleSize * scale * Math.sin((-Roll - 315) * Math.PI / 180)) + ww / 2;
		y2 = (float) ((float) ((HorizonCircleSize * scale * Math.cos((-Roll - 315) * Math.PI / 180)) + hh / 2) - (Pitch) / 35 * 200 / 2);

		c.drawLine(x1, y1, x2, y2, p3);

		x1 = (float) (HorizonCircleSize * scale * Math.sin((-Roll - 135) * Math.PI / 180)) + ww / 2;
		y1 = (float) ((float) ((HorizonCircleSize * scale * Math.cos((-Roll - 135) * Math.PI / 180)) + hh / 2) - (Pitch) / 35 * 200 / 2);

		x2 = (float) (HorizonCircleSize * scale * Math.sin((-Roll - 225) * Math.PI / 180)) + ww / 2;
		y2 = (float) ((float) ((HorizonCircleSize * scale * Math.cos((-Roll - 225) * Math.PI / 180)) + hh / 2) - (Pitch) / 35 * 200 / 2);

		c.drawLine(x1, y1, x2, y2, p3);

		// ////

		x1 = (float) (HorizonCircleSize * scale * Math.sin((-Roll + 180) * Math.PI / 180)) + ww / 2;
		y1 = (float) (HorizonCircleSize * scale * Math.cos((-Roll + 180) * Math.PI / 180)) + hh / 2;

		x2 = (float) ((HorizonCircleSize - AngleIndicatorLenghtLong) * scale * Math.sin((-Roll + 180) * Math.PI / 180)) + ww / 2;
		y2 = (float) ((HorizonCircleSize - AngleIndicatorLenghtLong) * scale * Math.cos((-Roll + 180) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p1);

		x1 = (float) (HorizonCircleSize * scale * Math.sin((-Roll + 190) * Math.PI / 180)) + ww / 2;
		y1 = (float) (HorizonCircleSize * scale * Math.cos((-Roll + 190) * Math.PI / 180)) + hh / 2;

		x2 = (float) ((HorizonCircleSize - AngleIndicatorLenght) * scale * Math.sin((-Roll + 190) * Math.PI / 180)) + ww / 2;
		y2 = (float) ((HorizonCircleSize - AngleIndicatorLenght) * scale * Math.cos((-Roll + 190) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p3);

		x1 = (float) (HorizonCircleSize * scale * Math.sin((-Roll + 200) * Math.PI / 180)) + ww / 2;
		y1 = (float) (HorizonCircleSize * scale * Math.cos((-Roll + 200) * Math.PI / 180)) + hh / 2;

		x2 = (float) ((HorizonCircleSize - AngleIndicatorLenght) * scale * Math.sin((-Roll + 200) * Math.PI / 180)) + ww / 2;
		y2 = (float) ((HorizonCircleSize - AngleIndicatorLenght) * scale * Math.cos((-Roll + 200) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p3);

		x1 = (float) (HorizonCircleSize * scale * Math.sin((-Roll + 210) * Math.PI / 180)) + ww / 2;
		y1 = (float) (HorizonCircleSize * scale * Math.cos((-Roll + 210) * Math.PI / 180)) + hh / 2;

		x2 = (float) ((HorizonCircleSize - AngleIndicatorLenght) * scale * Math.sin((-Roll + 210) * Math.PI / 180)) + ww / 2;
		y2 = (float) ((HorizonCircleSize - AngleIndicatorLenght) * scale * Math.cos((-Roll + 210) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p3);

		x1 = (float) (HorizonCircleSize * scale * Math.sin((-Roll + 225) * Math.PI / 180)) + ww / 2;
		y1 = (float) (HorizonCircleSize * scale * Math.cos((-Roll + 225) * Math.PI / 180)) + hh / 2;

		x2 = (float) ((HorizonCircleSize - AngleIndicatorLenght) * scale * Math.sin((-Roll + 225) * Math.PI / 180)) + ww / 2;
		y2 = (float) ((HorizonCircleSize - AngleIndicatorLenght) * scale * Math.cos((-Roll + 225) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p1);

		x1 = (float) (HorizonCircleSize * scale * Math.sin((-Roll + 170) * Math.PI / 180)) + ww / 2;
		y1 = (float) (HorizonCircleSize * scale * Math.cos((-Roll + 170) * Math.PI / 180)) + hh / 2;

		x2 = (float) ((HorizonCircleSize - AngleIndicatorLenght) * scale * Math.sin((-Roll + 170) * Math.PI / 180)) + ww / 2;
		y2 = (float) ((HorizonCircleSize - AngleIndicatorLenght) * scale * Math.cos((-Roll + 170) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p3);

		x1 = (float) (HorizonCircleSize * scale * Math.sin((-Roll + 160) * Math.PI / 180)) + ww / 2;
		y1 = (float) (HorizonCircleSize * scale * Math.cos((-Roll + 160) * Math.PI / 180)) + hh / 2;

		x2 = (float) ((HorizonCircleSize - AngleIndicatorLenght) * scale * Math.sin((-Roll + 160) * Math.PI / 180)) + ww / 2;
		y2 = (float) ((HorizonCircleSize - AngleIndicatorLenght) * scale * Math.cos((-Roll + 160) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p3);

		x1 = (float) (HorizonCircleSize * scale * Math.sin((-Roll + 150) * Math.PI / 180)) + ww / 2;
		y1 = (float) (HorizonCircleSize * scale * Math.cos((-Roll + 150) * Math.PI / 180)) + hh / 2;

		x2 = (float) ((HorizonCircleSize - AngleIndicatorLenght) * scale * Math.sin((-Roll + 150) * Math.PI / 180)) + ww / 2;
		y2 = (float) ((HorizonCircleSize - AngleIndicatorLenght) * scale * Math.cos((-Roll + 150) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p3);

		x1 = (float) (HorizonCircleSize * scale * Math.sin((-Roll + 135) * Math.PI / 180)) + ww / 2;
		y1 = (float) (HorizonCircleSize * scale * Math.cos((-Roll + 135) * Math.PI / 180)) + hh / 2;

		x2 = (float) ((HorizonCircleSize - AngleIndicatorLenght) * scale * Math.sin((-Roll + 135) * Math.PI / 180)) + ww / 2;
		y2 = (float) ((HorizonCircleSize - AngleIndicatorLenght) * scale * Math.cos((-Roll + 135) * Math.PI / 180)) + hh / 2;

		c.drawLine(x1, y1, x2, y2, p1);

		c.drawCircle(ww / 2, hh / 2, 5 * scaledDensity, p1);
		c.drawLine(ww / 2, hh / 2 - 5 * scaledDensity, ww / 2, hh / 2 - 15 * scaledDensity, p1);
		c.drawLine(ww / 2 - 5 * scaledDensity, hh / 2, ww / 2 - 15 * scaledDensity, hh / 2, p1);
		c.drawLine(ww / 2 + 5 * scaledDensity, hh / 2, ww / 2 + 15 * scaledDensity, hh / 2, p1);

		RectF r = new RectF(ww / 2 - HorizonCircleSize * scale, hh / 2 - HorizonCircleSize * scale, ww / 2 + HorizonCircleSize * scale, hh / 2 + HorizonCircleSize * scale);
		c.drawArc(r, Roll - 45, -90, false, p1);

		c.drawLine(ww / 2, hh / 2 - HorizonCircleSize * scale, ww / 2, hh / 2 - (HorizonCircleSize + AngleIndicatorLenghtLong) * scale, p1);

		if (Azimuth != 0)
			drawCompass(c, ww / 3, hh - 12 * scaledDensity, ww / 3, 10, 80, (int) Azimuth);
		if (Altitude != 0)
			drawVertical(c, 2 * ww / 3, hh / 3, hh / 3, 2, 10, (int) Altitude);
		if (SatNum > 0)
			drawVertical(c, ww / 3, hh / 3, hh / 3, 2, 10, (int) Speed);

	}

}
