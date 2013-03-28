package com.ezio.multiwii.dashboard.dashboard3;

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
import android.view.View;

import com.ezio.multiwii.R;

public class AltitudeView extends View {

	boolean D = false;
	Paint mPaint;
	Rect DrawingRec;
	int ww = 0, hh = 0;
	int tmp = 0;

	Bitmap[] bmp = new Bitmap[4];

	Matrix matrix = new Matrix();

	Context context;

	public float alt = 150;

	public AltitudeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	private void init() {
		bmp[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.alt3);
		bmp[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.alt1);
		bmp[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.hand2);
		bmp[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.hand1);

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.rgb(50, 50, 50));
		mPaint.setStyle(Style.FILL_AND_STROKE);
		mPaint.setTextSize(12);

		DrawingRec = new Rect();
	}

	public void Set(float alt) {
		this.alt = alt;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas c) {
		super.onDraw(c);
		c.drawRect(DrawingRec, mPaint);

		if (!D) {
			matrix.reset();
			// matrix.postRotate(map(alt % 100, 0, 100, 0, 360),
			// bmp[0].getWidth() / 2, bmp[0].getHeight() / 2);
			matrix.postTranslate((ww - bmp[0].getWidth()) / 2, (hh - bmp[0].getHeight()) / 2);
			c.drawBitmap(bmp[0], matrix, null);

			matrix.reset();
			matrix.postTranslate((ww - bmp[1].getWidth()) / 2, ((hh - bmp[1].getHeight()) / 2));
			c.drawBitmap(bmp[1], matrix, null);

			matrix.reset();
			matrix.preTranslate(0, -bmp[2].getHeight() * 0.30f);
			matrix.postRotate(map(alt, 0, 1000, 0, 360), bmp[2].getWidth() / 2, bmp[2].getHeight() / 2);
			matrix.postTranslate((ww - bmp[2].getWidth()) / 2, (hh - bmp[2].getHeight()) / 2);
			c.drawBitmap(bmp[2], matrix, null);

			matrix.reset();
			matrix.preTranslate(0, -bmp[3].getHeight() * 0.30f);
			matrix.postRotate(map(alt % 100, 0, 100, 0, 360), bmp[3].getWidth() / 2, bmp[3].getHeight() / 2);
			matrix.postTranslate((ww - bmp[3].getWidth()) / 2, (hh - bmp[3].getHeight()) / 2);
			c.drawBitmap(bmp[3], matrix, null);
		}

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// Account for padding
		float xpad = (float) (getPaddingLeft() + getPaddingRight());
		float ypad = (float) (getPaddingTop() + getPaddingBottom());

		ww = (int) (w - xpad);
		hh = (int) (h - ypad);

		DrawingRec = new Rect(getPaddingLeft(), getPaddingTop(), ww, hh);

		if (!D) {
			float factor = getFactor(bmp[1], ww, hh);

			bmp[0] = scaleToFill(bmp[0], ww, hh);
			bmp[1] = scaleToFill(bmp[1], factor);
			bmp[2] = scaleToFill(bmp[2], factor);
			bmp[3] = scaleToFill(bmp[3], factor);
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		int size = Math.min(parentHeight, parentWidth);
		this.setMeasuredDimension(size, size);
	}

	// Scale and keep aspect ratio
	static public Bitmap scaleToFill(Bitmap b, int width, int height) {
		float factorH = height / (float) b.getWidth();
		float factorW = width / (float) b.getWidth();
		float factorToUse = (factorH > factorW) ? factorW : factorH;
		return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorToUse), (int) (b.getHeight() * factorToUse), false);
	}

	// Scale and keep aspect ratio
	static public Bitmap scaleToFill(Bitmap b, float factorToUse) {
		return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorToUse), (int) (b.getHeight() * factorToUse), false);
	}

	float getFactor(Bitmap b, int width, int height) {
		float factorH = height / (float) b.getWidth();
		float factorW = width / (float) b.getWidth();
		float factorToUse = (factorH > factorW) ? factorW : factorH;
		return factorToUse;
	}

	public static int map(int x, int in_min, int in_max, int out_min, int out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	public static float map(float x, float in_min, float in_max, float out_min, float out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

}
