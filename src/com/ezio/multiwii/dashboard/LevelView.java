package com.ezio.multiwii.dashboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class LevelView extends View {

	private float angle = 0;

	private Paint paint = new Paint();
	private Paint paint1 = new Paint();
	private Paint paint2 = new Paint();
	private Paint paint3 = new Paint();

	public boolean arrow = false;

	RectF DrawingRec = new RectF();
	RectF OvalRect = new RectF();
	int hh = 100, ww = 100;

	int temp = 1;

	public void SetAngle(float angle) {
		this.angle = angle;
		invalidate();
	}

	public LevelView(Context context) {
		super(context);
		init();
	}

	public LevelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void init() {
		paint.setAntiAlias(true);
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeWidth(2);

		paint1.setAntiAlias(true);
		paint1.setColor(Color.WHITE);
		paint1.setStyle(Paint.Style.FILL_AND_STROKE);
		paint1.setStrokeWidth(2);
		paint1.setTextSize(30);

		paint2.setAntiAlias(true);
		paint2.setColor(Color.YELLOW);
		paint2.setStyle(Paint.Style.FILL_AND_STROKE);
		paint2.setStrokeWidth(2);
		paint2.setTextSize(30);
		
		paint3.setAntiAlias(true);
		paint3.setColor(Color.BLACK);
		paint3.setStyle(Paint.Style.STROKE);
		paint3.setStrokeWidth(2);
		paint3.setTextSize(30);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT);
		int cx = ww / 2;
		int cy = hh / 2;
		canvas.translate(cx, cy);
		canvas.rotate(angle);

		canvas.drawOval(OvalRect, paint);

		canvas.drawLine(-temp / 2.2f, 0, temp / 2.2f, 0, paint1);

		if (arrow) {
			canvas.drawLine(temp / 3f, temp / 8f, temp / 2.2f, 0, paint1);
			canvas.drawLine(temp / 3f, -temp / 8f, temp / 2.2f, 0, paint1);
		} else {
			canvas.drawLine(0, temp / 8f, 0, 0, paint1);
		}

		canvas.drawText(String.valueOf(angle), 0 - paint1.measureText(String.valueOf(angle)) / 2, -temp / 8, paint2);
		canvas.drawOval(OvalRect, paint3);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// Account for padding
		float xpad = (float) (getPaddingLeft() + getPaddingRight());
		float ypad = (float) (getPaddingTop() + getPaddingBottom());

		ww = (int) (w - xpad);
		hh = (int) (h - ypad);

		DrawingRec = new RectF(getPaddingLeft(), getPaddingTop(), ww, hh);

		temp = Math.min(hh, ww);
		OvalRect = new RectF(-temp / 2.2f, -temp / 2.2f, temp / 2.2f, temp / 2.2f);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int desiredWidth = 100;
		int desiredHeight = 100;

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		// Measure Width
		if (widthMode == MeasureSpec.EXACTLY) {
			// Must be this size
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			// Can't be bigger than...
			width = Math.min(desiredWidth, widthSize);
		} else {
			// Be whatever you want
			width = desiredWidth;
		}

		// Measure Height
		if (heightMode == MeasureSpec.EXACTLY) {
			// Must be this size
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			// Can't be bigger than...
			height = Math.min(desiredHeight, heightSize);
		} else {
			// Be whatever you want
			height = desiredHeight;
		}

		// MUST CALL THIS
		setMeasuredDimension(width, height);

	}

}
