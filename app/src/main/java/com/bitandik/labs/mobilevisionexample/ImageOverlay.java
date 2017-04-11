package com.bitandik.labs.mobilevisionexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ykro.
 */

public class ImageOverlay extends View {
  private Bitmap mBitmap;
  private PointF facePosition;

  private int previewWidth;
  private int previewHeight;

  private float widthScale = 1.0f;
  private float heightScale = 1.0f;
  private float faceWidth = -1.0f;
  private float faceHeight = -1.0f;

  private boolean drawing = false;


  public ImageOverlay(Context context, AttributeSet attrs) {
    super(context, attrs);
    mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.galileo);
  }

  public void setCameraInfo(int previewWidth, int previewHeight) {
    this.previewWidth = previewWidth;
    this.previewHeight = previewHeight;
    postInvalidate();
  }

  public void clear() {
    drawing = false;
    postInvalidate();
  }

  public void update(PointF facePosition, float faceWidth, float faceHeight) {
    this.faceWidth = faceWidth;
    this.faceHeight = faceHeight;
    this.facePosition = facePosition;
    drawing = true;
    postInvalidate();
  }

  public float scaleX(float horizontal) {
    return horizontal * widthScale;
  }

  public float scaleY(float vertical) {
    return vertical * heightScale;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if ((facePosition == null) ||
        (faceWidth == -1)      ||
        (faceHeight == -1)) {
      return;
    }

    Paint paint = new Paint();
    paint.setAntiAlias(true);
    paint.setFilterBitmap(true);
    paint.setDither(true);

    if ((previewWidth != 0) && (previewHeight != 0)) {
      widthScale = (float) canvas.getWidth() / (float) previewWidth;
      heightScale = (float) canvas.getHeight() / (float) previewHeight;
    }

    if (drawing) {
      Bitmap scaledBitmap = Bitmap.createScaledBitmap(mBitmap, Math.round(scaleX(faceWidth)), Math.round(scaleY(faceHeight)), true);
      canvas.drawBitmap(scaledBitmap, (canvas.getWidth() - scaleX(facePosition.x + faceWidth)),scaleY(facePosition.y),paint);
    } else {
      canvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }

  }
}
