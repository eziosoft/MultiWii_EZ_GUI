package com.ezio.multiwii.dashboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.ezio.multiwii.R;
import com.ezio.multiwii.helpers.Functions;

public class HorizonClass {

	boolean D = false;
	Paint mPaint;
	Rect DrawingRec;
	int ww = 0, hh = 0;
	float tmp = 0;

	Bitmap[] bmp = new Bitmap[4];

	Matrix matrix = new Matrix();

	Context context;

	public float roll = 15;
	public float pitch = 20;

	public HorizonClass(Context context, AttributeSet attrs) {
		// super(context, attrs);
		this.context = context;
		init();
	}

	public void init() {
		bmp[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ati0);
		bmp[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ati1);
		bmp[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ati2);
		bmp[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.ati3);

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.rgb(50, 50, 50));
		mPaint.setStyle(Style.FILL_AND_STROKE);
		mPaint.setTextSize(12);

		DrawingRec = new Rect();
	}

	public void Set(float pitch, float roll) {
		this.roll = -roll;
		this.pitch = pitch;
	}

	public void Draw(Canvas c, int x, int y) {
		// super.onDraw(c);
		// c.drawRect(DrawingRec, mPaint);

		if (!D) {
			matrix.reset();
			matrix.postRotate(roll, bmp[0].getWidth() / 2, bmp[0].getHeight() / 2);
			matrix.postTranslate(x + (ww - bmp[0].getWidth()) / 2, y + (hh - bmp[0].getHeight()) / 2);
			c.drawBitmap(bmp[0], matrix, null);

			matrix.reset();
			if (pitch > 90)
				pitch = 90;
			if (pitch < -90)
				pitch = -90;
			tmp = Functions.map(pitch, -90, 90, -(bmp[1].getHeight() / 2), bmp[1].getHeight() / 2);
			matrix.postRotate(roll, bmp[1].getWidth() / 2, bmp[1].getHeight() / 2 - tmp);
			matrix.postTranslate(x + (ww - bmp[1].getWidth()) / 2, y + ((hh - bmp[1].getHeight()) / 2) + tmp);
			c.drawBitmap(bmp[1], matrix, null);

			matrix.reset();
			matrix.postRotate(roll, bmp[2].getWidth() / 2, bmp[2].getHeight() / 2);
			matrix.postTranslate(x + (ww - bmp[2].getWidth()) / 2, y + (hh - bmp[2].getHeight()) / 2);
			c.drawBitmap(bmp[2], matrix, null);

			matrix.reset();
			matrix.postRotate(0, bmp[3].getWidth() / 2, bmp[3].getHeight() / 2);
			matrix.postTranslate(x + (ww - bmp[3].getWidth()) / 2, y + (hh - bmp[3].getHeight()) / 2);
			c.drawBitmap(bmp[3], matrix, null);
		}

	}

	public void onSizeChanged(int w, int h) {
		ww = (int) (w);
		hh = (int) (h);

		DrawingRec = new Rect(0, 0, ww, hh);

		if (!D) {
			float factor = getFactor(bmp[3], ww, hh);

			bmp[0] = scaleToFill(bmp[0], factor);
			bmp[1] = scaleToFill(bmp[1], factor);
			bmp[2] = scaleToFill(bmp[2], factor);
			bmp[3] = scaleToFill(bmp[3], factor);
		}

	}

	// Scale and keep aspect ratio
	private Bitmap scaleToFill(Bitmap b, int width, int height) {
		float factorH = height / (float) b.getWidth();
		float factorW = width / (float) b.getWidth();
		float factorToUse = (factorH > factorW) ? factorW : factorH;
		return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorToUse), (int) (b.getHeight() * factorToUse), true);
	}

	// Scale and keep aspect ratio
	private Bitmap scaleToFill(Bitmap b, float factorToUse) {
		return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorToUse), (int) (b.getHeight() * factorToUse), true);
	}

	float getFactor(Bitmap b, int width, int height) {
		float factorH = height / (float) b.getWidth();
		float factorW = width / (float) b.getWidth();
		float factorToUse = (factorH > factorW) ? factorW : factorH;
		return factorToUse;
	}

}
